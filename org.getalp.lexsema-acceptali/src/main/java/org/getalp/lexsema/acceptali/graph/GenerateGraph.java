package org.getalp.lexsema.acceptali.graph;


import org.apache.commons.cli.*;
import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureGenerator;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureGeneratorFactory;
import org.getalp.lexsema.acceptali.closure.writer.FileTranslationClosureWriter;
import org.getalp.lexsema.acceptali.closure.writer.TranslationClosureWriter;
import org.getalp.lexsema.acceptali.graph.generator.TranslationGraphGenerator;
import org.getalp.lexsema.acceptali.graph.generator.TranslationGraphGeneratorImpl;
import org.getalp.lexsema.acceptali.graph.processing.TranslationProcessing;
import org.getalp.lexsema.acceptali.graph.tools.ToolGraph;
import org.getalp.lexsema.acceptali.graph.writer.LexicalEntryIdProvider;
import org.getalp.lexsema.acceptali.graph.writer.TranslationGraphWriter;
import org.getalp.lexsema.acceptali.graph.writer.TranslationGraphWriterImpl;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaMemoryStore;
import org.getalp.lexsema.ontolex.graph.storage.JenaRemoteSPARQLStore;
import org.getalp.lexsema.ontolex.graph.storage.JenaTDBStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.getalp.lexsema.util.Language;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jgrapht.Graph;
import scala.util.parsing.combinator.lexical.Lexical;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class GenerateGraph {

    private static final Logger logger = LoggerFactory.getLogger(GenerateGraph.class);

    private static final String ONTOLOGY_PROPERTIES = "data" + File.separatorChar + "ontology.properties";
    private static final String LANGUAGE_OPTION = "l";
    private static final String STORAGE_TYPE_OPTION = "st";
    private static final String STORAGE_LOCATION_OPTION = "sl";
    private static final String OUTPUT_OPTION = "o";
    private static final String DEFAULT_LANGUAGES = "fr,en,de,it";
    private static final String DEFAULT_OUTPUT = "closureOutput";
    private static final String DEFAULT_STORAGE_TYPE = "tdb";

    private static final Options options; // Command line op

    static {
        options = new Options();
        options.addOption("h", false, "Prints usage and exits. ");
        options.addOption(LANGUAGE_OPTION, true,
                String.format("Languages (fr, en, de, pt, ru, bg, tr, es, it, fi, el, pl, jp). %s by default.", DEFAULT_LANGUAGES));
        options.addOption(STORAGE_TYPE_OPTION, true, "Type of the triple store (tdb, virtuoso, file, remote [default=tdb]");
        options.addOption(STORAGE_LOCATION_OPTION, true, "The location of the store (file, url, jdbc string)");
        options.addOption(OUTPUT_OPTION, true, "The output directory to which to write the closure");
    }

    private CommandLine cmd = null; // Command Line arguments

    private DBNary dbNary;

    private String location = DEFAULT_OUTPUT;
    private Store vts;
    private String targetDirectory = DEFAULT_OUTPUT;

    private String vocable;
    private int depth;
    private String sourceLanguage;


    private GenerateGraph(String... args) throws NoSuchMethodException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        loadArgs(args);
    }

    public static void main(String... args) throws IOException, NoSuchVocableException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        @SuppressWarnings("LocalVariableOfConcreteClass") GenerateGraph generateGraph = new GenerateGraph(args);
        //generateGraph.writeClosures(generateGraph.generate());
        Graph<LexicalEntry,DefaultEdge> g = generateGraph.generate() ;
        generateGraph.writeClosure(g);
        TranslationProcessing tp = new TranslationProcessing() ;
        Collection<Set<LexicalEntry>> cliques = tp.getCliques(g) ;
        Collection<Set<LexicalEntry>> ambigSets = tp.getAmbiguitySets(cliques) ;
        Vocable voc = generateGraph.dbNary.getVocable(generateGraph.vocable, Language.fromCode(generateGraph.sourceLanguage));
        List<LexicalEntry> ventries = generateGraph.dbNary.getLexicalEntries(voc);
        if (!ventries.isEmpty()) {
            for(LexicalEntry v1 : ventries){
                LexicalEntry v2 ;
                Set<DefaultEdge> deSet = g.edgesOf(v1) ;
                Object[] firstEdgeNeighbor = deSet.toArray();
                DefaultEdge de = (DefaultEdge)firstEdgeNeighbor[0] ;
                if (g.getEdgeTarget(de).equals(v1)){
                    v2 = g.getEdgeSource(de) ;
                }else{
                    v2 = g.getEdgeTarget(de) ;
                }
                Map<LexicalEntry,Double> prob = tp.senseUniformPaths(g,v1,v2,ambigSets) ;
                for(LexicalEntry v : prob.keySet()){
                    System.out.println(v+" : "+prob.get(v)) ;
                }
            }
        }
        //System.out.println("\nCliques : "+tp.seeSets(cliques)+"\n") ;
        //System.out.println("\nAmbiguity Sets : "+tp.seeSets(ambigSets)+"\n") ;
    }

    private static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        String help =
                String.format("Generates a translation closure and saves it to a directory that can be used as input for FileTranslationClosureGenerator%s", System.lineSeparator());
        formatter.printHelp("java -cp %spath%sto%slexsema-acceptali org.getalp.lexsema.acceptali.cli.GenerateClosure [OPTIONS] vocable source_language depth",
                "With OPTIONS in:", options,
                help, false);
    }


    private void loadArgs(String... args) throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        CommandLineParser parser = new PosixParser();
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Error parsing arguments: {}", e.getLocalizedMessage());
            printUsage();
            System.exit(1);
        }
        if (cmd.hasOption("h")) {
            printUsage();
            System.exit(0);
        }

        processStorageOptions();

        String cmdLanguages = DEFAULT_LANGUAGES;

        String[] languageCodeList = cmdLanguages.split(",");
        Language[] languages = new Language[languageCodeList.length];
        for (int i = 0; i < languageCodeList.length; i++) {
            languages[i] = Language.fromCode(languageCodeList[i]);
        }

        if (cmd.hasOption(OUTPUT_OPTION)) {
            targetDirectory = cmd.getOptionValue(OUTPUT_OPTION);
        }

        String[] remainingArgs = cmd.getArgs();

        if (remainingArgs.length < 3) {
            logger.error("You must supply the vocable, source_language and the depth");
            printUsage();
            System.exit(1);
        }

        vocable = remainingArgs[0];

        sourceLanguage = remainingArgs[1];

        depth = Integer.valueOf(remainingArgs[2]);

        OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        dbNary = (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, languages);
    }

    @SuppressWarnings("InstanceVariableUsedBeforeInitialized")
    private void processStorageOptions() throws IOException {
        if (cmd.hasOption(STORAGE_LOCATION_OPTION)) {
            location = cmd.getOptionValue(STORAGE_LOCATION_OPTION);
        } else {
            logger.error("You must supply a storage location (-sl option)");
            printUsage();
            System.exit(1);
        }

        String storageType = DEFAULT_STORAGE_TYPE;

        if (cmd.hasOption(STORAGE_TYPE_OPTION)) {
            storageType = cmd.getOptionValue(STORAGE_TYPE_OPTION);
        }
        //tdb, virtuoso, file, remote
        switch (storageType) {
            case "tdb":
                vts = new JenaTDBStore(location);

                break;
            case "file":
                vts = new JenaMemoryStore(location);
                break;
            case "remote":
                vts = new JenaRemoteSPARQLStore(location);
                break;
            default:
                printUsage();
                System.exit(1);
                break;
        }
        StoreHandler.registerStoreInstance(vts);
        //StoreHandler.DEBUG_ON = true;

    }

    private /*Map<LexicalEntry, Graph<LexicalEntry,DefaultEdge>>*/ Graph<LexicalEntry,DefaultEdge> generate() throws NoSuchVocableException {
        System.out.println("vocable : "+vocable+" language : "+Language.fromCode(sourceLanguage)) ;
        Vocable v = dbNary.getVocable(vocable, Language.fromCode(sourceLanguage));
        System.out.println("Vocable : "+v.getVocable()) ;
        List<LexicalEntry> ventries = dbNary.getLexicalEntries(v);
        System.out.println("nombre d'entrees lexicales : "+ventries.size()) ;
        if (!ventries.isEmpty()) {
            //Map<LexicalEntry,Graph<LexicalEntry,DefaultEdge>> translations = new HashMap<>();
            Graph<LexicalEntry,DefaultEdge> translations = new SimpleGraph<LexicalEntry, DefaultEdge>(DefaultEdge.class) ;
            for(LexicalEntry lexicalEntry: ventries) {
                System.out.println("entree : "+lexicalEntry.getLemma()+"_"+lexicalEntry.getNumber()) ;
                TranslationGraphGenerator gtg = TranslationGraphGeneratorImpl.createTranslationGraphGenerator(dbNary, lexicalEntry);
                //translations.put(lexicalEntry,generateEntryGraph(gtg));
                translations = ToolGraph.importGraph(translations,generateEntryGraph(gtg)) ;
            }
            System.out.println("traitement de toutes les entrees termine") ;
            return translations ;
        }
        System.out.println("la liste des entrees lexicales est nulle") ;
        return null;
    }

    private Graph<LexicalEntry,DefaultEdge> generateEntryGraph(TranslationGraphGenerator generator) {
        return generator.generateGraph(depth);
    }

    /*private void writeClosures(Map<LexicalEntry, Graph<LexicalEntry,DefaultEdge>> translations) throws IOException {
        for(Map.Entry<LexicalEntry, Graph<LexicalEntry,DefaultEdge>>translationEntry : translations.entrySet()) {
            //Path p = Paths.get(targetDirectory, String.format("%s_%d", translationEntry.getKey().getLemma(), translationEntry.getKey().getNumber()));
            //Files.createDirectory(p);
            File dir = new File(targetDirectory) ;
            if(!dir.exists()){
                dir.mkdirs() ;
            }
            String path = targetDirectory+"/"+translationEntry.getKey().getLemma()+"_"+translationEntry.getKey().getNumber()+".dot" ;
            writeClosure(translationEntry.getValue(),path);
        }
    }*/

    private void writeClosure(Graph<LexicalEntry,DefaultEdge> translationGraph) {
        try {
            if (translationGraph != null) {
                File dir = new File(targetDirectory) ;
                if(!dir.exists()){
                    dir.mkdirs() ;
                }
                String path = targetDirectory+"/"+vocable+"_"+sourceLanguage+".dot" ;
                Writer translationGraphWriter = new PrintWriter(path);
                VertexNameProvider vertIdProv = new LexicalEntryIdProvider() ;
                DOTExporter dotExp = new DOTExporter(vertIdProv,null,null) ;
                dotExp.export(translationGraphWriter,translationGraph) ;

                //TranslationGraphWriter writer = new TranslationGraphWriterImpl(path.toString());
                //writer.writeTranslation(translationGraph);
            } else {
                logger.error("The graph is empty... Aborting.");
            }
        }catch(FileNotFoundException e){
            System.out.println("FileNotFoundExcetion") ;
        }
    }
}