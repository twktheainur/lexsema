package org.getalp.lexsema.acceptali.cli;


import org.apache.commons.cli.*;
import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureGenerator;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureGeneratorFactory;
import org.getalp.lexsema.acceptali.closure.writer.FileTranslationClosureWriter;
import org.getalp.lexsema.acceptali.closure.writer.TranslationClosureWriter;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class GenerateClosure {

    private static final Logger logger = LoggerFactory.getLogger(GenerateClosure.class);

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

    private CommandLine cmd; // Command Line arguments

    private DBNary dbNary;

    private String location = DEFAULT_OUTPUT;
    private Store vts;
    private String targetDirectory = DEFAULT_OUTPUT;

    private String vocable;
    private int depth;
    private String sourceLanguage;


    private GenerateClosure(String... args) throws NoSuchMethodException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        loadArgs(args);
    }

    public static void main(String... args) throws IOException, NoSuchVocableException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        @SuppressWarnings("LocalVariableOfConcreteClass") GenerateClosure generateClosure = new GenerateClosure(args);
        generateClosure.writeClosures(generateClosure.generate());
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
        } catch (ParseException e) {
            logger.error("Error parsing arguments: {}", e.getLocalizedMessage());
            printUsage();
            System.exit(1);
        }
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

    }

    private Map<LexicalEntry, LexicalResourceTranslationClosure<LexicalSense>> generate() throws NoSuchVocableException {
        Vocable v = dbNary.getVocable(vocable, Language.fromCode(sourceLanguage));
        List<LexicalEntry> ventries = dbNary.getLexicalEntries(v);
        if (!ventries.isEmpty()) {
            Map<LexicalEntry, LexicalResourceTranslationClosure<LexicalSense>> closures = new HashMap<>();
            for (LexicalEntry lexicalEntry : ventries) {
                TranslationClosureGenerator gtc = TranslationClosureGeneratorFactory.createCompositeGenerator(dbNary, lexicalEntry);
                closures.put(lexicalEntry, generateEntryClosure(gtc));
            }
            return closures;
        }
        return null;
    }

    private LexicalResourceTranslationClosure<LexicalSense> generateEntryClosure(TranslationClosureGenerator generator) {
        return generator.generateClosure(depth);
    }

    private void writeClosures(Map<LexicalEntry, LexicalResourceTranslationClosure<LexicalSense>> closures) throws IOException {
        for (Map.Entry<LexicalEntry, LexicalResourceTranslationClosure<LexicalSense>> closureEntry : closures.entrySet()) {
            Path p = Paths.get(targetDirectory, String.format("%s_%d", closureEntry.getKey().getLemma(), closureEntry.getKey().getNumber()));
            Files.createDirectory(p);
            writeClosure(closureEntry.getValue(), p);
        }
    }

    private void writeClosure(LexicalResourceTranslationClosure<LexicalSense> closure, Path path) {
        if (closure != null) {
            TranslationClosureWriter writer = new FileTranslationClosureWriter(path.toString());
            writer.writeClosure(closure);
        } else {
            logger.error("The closure is empty... Aborting.");
        }
    }
}
