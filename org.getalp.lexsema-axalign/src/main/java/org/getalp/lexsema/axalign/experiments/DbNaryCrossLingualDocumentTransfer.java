package org.getalp.lexsema.axalign.experiments;

import org.getalp.lexsema.io.text.*;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaRemoteSPARQLStore;
import org.getalp.lexsema.ontolex.graph.storage.JenaTDBStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.getalp.lexsema.similarity.measures.lesk.AnotherLeskSimilarity;
import org.getalp.lexsema.translation.DbNaryDisambiguatingTranslator;
import org.getalp.lexsema.translation.Translator;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.MultiThreadCuckooSearch;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorerWithCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static java.io.File.separator;


@SuppressWarnings("OverlyCoupledClass")
public final class DbNaryCrossLingualDocumentTransfer {

    private static final int CUCKOO_ITERATION=50000;
    private static final double CUCKOO_LEVY_LOCATION=1d;
    private static final double CUCKOO_MAX_LEVY_LOCATION=1d;
    private static final double CUCKOO_MIN_LEVY_SCALE =.5d;
    private static final double CUCKOO_MAX_LEVY_SCALE =1.5d;

    public static final String ONTOLOGY_PROPERTIES = String.format("data%sontology.properties", separator);
    private static final Pattern SEPARATOR_PATTERN = Pattern.compile("|");
    static Language[] loadLanguages = {
            Language.FRENCH, Language.ENGLISH,
            Language.RUSSIAN
    };
    private static final Logger logger = LoggerFactory.getLogger(DbNaryCrossLingualDocumentTransfer.class);

    private final DBNary dbNary;
    @SuppressWarnings("LawOfDemeter")
    private final ConfigurationScorer configurationScorer = new ConfigurationScorerWithCache(new AnotherLeskSimilarity());
    private final Disambiguator disambiguator = new MultiThreadCuckooSearch(CUCKOO_ITERATION, CUCKOO_LEVY_LOCATION, CUCKOO_MAX_LEVY_LOCATION, CUCKOO_MIN_LEVY_SCALE, CUCKOO_MAX_LEVY_SCALE, configurationScorer, true);

    private DbNaryCrossLingualDocumentTransfer(DBNary dbNary) {
        this.dbNary = dbNary;
    }


    public static void main(String... args) throws NoSuchVocableException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String sourceLang = args[0];
        String targetLang = args[1];
        String sourceDir = args[2];
        String targetDir = args[3];

        try {

            DBNary dbNary;
            if (args.length > 4) {
                String dbPath = args[4];
                dbNary = instantiateDBNary(dbPath);
            } else {
                dbNary = instantiateDBNary();
            }

            Language lang1 = Language.fromCode(sourceLang);
            Language lang2 = Language.fromCode(targetLang);


            @SuppressWarnings("LocalVariableOfConcreteClass") DbNaryCrossLingualDocumentTransfer translator = new DbNaryCrossLingualDocumentTransfer(dbNary);

            File sourceDirectoryFile = new File(sourceDir);
            File targetDirectoryFile = new File(targetDir);
            if (!targetDirectoryFile.exists() && targetDirectoryFile.mkdirs()) {

                for (File child : sourceDirectoryFile.listFiles((dir, name) -> !name.equals(".DS_Store"))) {
                    try (BufferedReader br = new BufferedReader(new FileReader(child))) {

                        String text = loadInputText(br);

                        String translated = translator.translate(text, lang1, lang2);

                        File transferredFile = new File(targetDirectoryFile, child.getName());
                        try (PrintWriter pw = new PrintWriter(transferredFile)) {
                            pw.println(translated);
                            pw.flush();
                            pw.close();
                        }
                    }
                }
                translator.close();
            } else {
                logger.error("The specified target directory does not exist and could not be created.");
            }
        } catch (IOException ex){
            logger.error("Input/Output error: {}", ex.getLocalizedMessage());
        }
    }

    private static String loadInputText(BufferedReader br) throws IOException {
        StringBuilder text = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            text.append(line);
            text.append(" ");
            line = br.readLine();
        }
        return text.toString();
    }

    private static boolean isLanguage(Language language, String target) {
        return language.getISO2Code().equals(target);
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

        TextProcessor textProcessor;
//        SnowballStemmer snowballStemmer;
        Collection<String> sourceStopList;
        Collection<String> targetStopList;
        if (isLanguage(lang1, "en")) {
            textProcessor = new EnglishDKPTextProcessor();
            sourceStopList = loadStopList("english_stop.txt");
        } else if (isLanguage(lang1, "fr")) {
            textProcessor = new FrenchDKPTextProcessor();
            sourceStopList = loadStopList("french_stop.txt");
        } else if (isLanguage(lang1, "de")){
            textProcessor = new GermanDKPTextProcessor();
            sourceStopList = loadStopList("german_stop.txt");
        } else {
            textProcessor = new RussianPythonTextProcessor();
            sourceStopList = loadStopList("russian_stop.txt");
        }
        if (isLanguage(lang2, "en")) {
            targetStopList = loadStopList("english_stop.txt");
//            snowballStemmer = new englishStemmer();
        } else if (isLanguage(lang2, "fr")) {
            targetStopList = loadStopList("french_stop.txt");
//            snowballStemmer = new frenchStemmer();
        } else if (isLanguage(lang2, "de")) {
            targetStopList = loadStopList("german_stop.txt");
//            snowballStemmer = new germanStemmer();
        } else {
//            snowballStemmer = new russianStemmer();
            targetStopList = loadStopList("russian_stop.txt");
        }
        Translator translator = new DbNaryDisambiguatingTranslator(dbNary, textProcessor, disambiguator, sourceStopList, targetStopList);
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
                    String[] fields = SEPARATOR_PATTERN.split(line);
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
