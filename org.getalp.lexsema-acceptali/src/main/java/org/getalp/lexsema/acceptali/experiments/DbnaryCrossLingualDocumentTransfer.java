package org.getalp.lexsema.acceptali.experiments;

import org.getalp.lexsema.io.text.EnglishDKPSentenceProcessor;
import org.getalp.lexsema.io.text.FrenchDKPSentenceProcessor;
import org.getalp.lexsema.io.text.RussianPythonSentenceProcessor;
import org.getalp.lexsema.io.text.SentenceProcessor;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaRemoteSPARQLStore;
import org.getalp.lexsema.ontolex.graph.storage.JenaTDBStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.translation.DbNaryDisambiguatingTranslator;
import org.getalp.lexsema.translation.Translator;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.SimulatedAnnealing;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import org.tartarus.snowball.ext.frenchStemmer;
import org.tartarus.snowball.ext.russianStemmer;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.TreeSet;

import static java.io.File.separator;


@SuppressWarnings("OverlyCoupledClass")
public final class DbnaryCrossLingualDocumentTransfer {

    public static final String ONTOLOGY_PROPERTIES = String.format("data%sontology.properties", separator);
    public static final double P_0 = 0.8;
    public static final double COOLING_RATE = 0.8;
    static Language[] loadLanguages = {
            Language.FRENCH, Language.ENGLISH,
            Language.RUSSIAN
    };
    private static Logger logger = LoggerFactory.getLogger(DbnaryCrossLingualDocumentTransfer.class);

    private DBNary dbNary;
    @SuppressWarnings("LawOfDemeter")
    private SimilarityMeasure sim = new TverskiIndexSimilarityMeasureBuilder().alpha(1d).beta(0d).gamma(0d).fuzzyMatching(false).computeRatio(false).build();
    private ConfigurationScorer configurationScorer = new TverskyConfigurationScorer(sim, Runtime.getRuntime().availableProcessors());
    private Disambiguator disambiguator = new SimulatedAnnealing(P_0, COOLING_RATE,5,100,configurationScorer);

    private DbnaryCrossLingualDocumentTransfer(DBNary dbNary) {
        this.dbNary = dbNary;
    }


    public static void main(String[] args) throws IOException, NoSuchVocableException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String sourceLang = args[0];
        String targetLang = args[1];
        String sourceDir = args[2];
        String targetDir = args[3];

        DBNary dbNary;
        if (args.length > 4) {
            String dbPath = args[4];
            dbNary = instantiateDBNary(dbPath);
        } else {
            dbNary = instantiateDBNary();
        }

        Language lang1 = Language.fromCode(sourceLang);
        Language lang2 = Language.fromCode(targetLang);


        @SuppressWarnings("LocalVariableOfConcreteClass") DbnaryCrossLingualDocumentTransfer translator = new DbnaryCrossLingualDocumentTransfer(dbNary);

        File sourceDirectoryFile = new File(sourceDir);
        File targetDirectoryFile = new File(targetDir);
        if (!targetDirectoryFile.exists() && targetDirectoryFile.mkdirs()) {

            for (File child : sourceDirectoryFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return !name.equals(".DS_Store");
                }
            })) {
                try (BufferedReader br = new BufferedReader(new FileReader(child))) {
                    String text = "";
                    String line = br.readLine();
                    while (line != null) {
                        text += line + " ";
                        line = br.readLine();
                    }
                    String translated = translator.translate(text,lang1, lang2);
                    File transferredFile = new File(targetDirectoryFile, child.getName());
                    @SuppressWarnings("IOResourceOpenedButNotSafelyClosed") PrintWriter pw = new PrintWriter(transferredFile);
                    pw.println(translated);
                    pw.flush();
                    pw.close();
                }
            }
            translator.close();
        } else {
            logger.error("The specified target directory does not exist and could not be created.");
        }
    }

    private static boolean languageIs(Language l, String target) {
        return l.getISO2Code().equals(target);
    }

    private static DBNary instantiateDBNary() throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Store vts = new JenaRemoteSPARQLStore("http://kaiko.getalp.org/sparql");
        StoreHandler.registerStoreInstance(vts);
        //StoreHandler.DEBUG_ON = true;
        OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        // Creating DBNary wrapper
        return (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, loadLanguages);
    }

    private static DBNary instantiateDBNary(String path) throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        //Store vts = new JenaRemoteSPARQLStore("http://kaiko.getalp.org/sparql");
        Store vts = new JenaTDBStore(path);
        StoreHandler.registerStoreInstance(vts);
        //StoreHandler.DEBUG_ON = true;
        OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        // Creating DBNary wrapper
        return (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, loadLanguages);
    }

    public String translate(String text, Language lang1, Language lang2) {

        SentenceProcessor sentenceProcessor;
        SnowballStemmer snowballStemmer;
        Collection<String> sourceStopList;
        Collection<String> targetStopList;
        if (languageIs(lang1, "en")) {
            sentenceProcessor = new EnglishDKPSentenceProcessor();
            sourceStopList = loadStopList("english_stop.txt");
        } else if (languageIs(lang1, "fr")) {
            sentenceProcessor = new FrenchDKPSentenceProcessor();
            sourceStopList = loadStopList("french_stop.txt");
        } else {
            sentenceProcessor = new RussianPythonSentenceProcessor();
            sourceStopList = loadStopList("russian_stop.txt");
        }
        if (languageIs(lang2, "en")) {
            targetStopList = loadStopList("english_stop.txt");
            snowballStemmer = new englishStemmer();
        } else if (languageIs(lang2, "fr")) {
            targetStopList = loadStopList("french_stop.txt");
            snowballStemmer = new frenchStemmer();
        } else {
            sentenceProcessor = new RussianPythonSentenceProcessor();
            snowballStemmer = new russianStemmer();
            targetStopList = loadStopList("russian_stop.txt");
        }
        Translator translator = new DbNaryDisambiguatingTranslator(dbNary, sentenceProcessor, disambiguator, snowballStemmer, sourceStopList, targetStopList);
        return translator.translate(text, lang1, lang2);
    }

    public void close() {
        disambiguator.release();
    }

    private Collection<String> loadStopList(String name) {
        Collection<String> result = new TreeSet<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(name)))) {
            String line = "";
            while (line != null) {
                if (line.contains("|")) {
                    String[] fields = line.split("|");
                    if (!fields[0].isEmpty()) {
                        line = fields[0];
                    }
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    result.add(line);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return result;
    }

}
