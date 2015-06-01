package org.getalp.lexsema.acceptali.experiments;

import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosureImpl;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureGenerator;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureGeneratorFactory;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureSemanticSignatureGenerator;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureSemanticSignatureGeneratorImpl;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaTDBStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.StringSemanticSymbolImpl;
import org.getalp.lexsema.translation.BingAPITranslator;
import org.getalp.lexsema.translation.CachedTranslator;
import org.getalp.lexsema.translation.Translator;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import static java.io.File.separator;


public final class PrintTranslatedClosure {
    public static final String DB_PATH = String.format("%sVolumes%sRAMDisk", separator, separator);
    public static final String ONTOLOGY_PROPERTIES = String.format("data%sontology.properties", separator);
    public static final File CLOSURE_SAVE_PATH = new File(String.format("..%sdata%sclosure_river", separator, separator));
    /**
     * twk.theainur@live.co.uk account
     */
    //public static final String BING_APP_ID = "dbnary";
    //public static final String BING_APP_KEY = "H2pC+d3b0L0tduSZzRafqPZyV6zmmzMmj9+AEpc9b1E=";

    /**
     * ainuros@outlook.com account
     */
    public static final String BING_APP_ID = "dbnary_hyper";
    public static final String BING_APP_KEY = "IecT6H4OjaWo3OtH2pijfeNIx1y1bML3grXz/Gjo/+w=";

    public static final int DEPTH = 1;
    static Language[] loadLanguages = {
            Language.FRENCH, Language.ENGLISH, Language.ITALIAN, Language.SPANISH,
            Language.PORTUGUESE, Language.BULGARIAN, Language.CATALAN, Language.FINNISH,
            Language.GERMAN, Language.RUSSIAN, Language.GREEK, Language.TURKISH
    };
    private static Logger logger = LoggerFactory.getLogger(PrintTranslatedClosure.class);


    private PrintTranslatedClosure() {
        System.setProperty("org.slf4j.simpleLogger.showShortLogName", "true");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
    }


    public static void main(String[] args) throws IOException, NoSuchVocableException {
        try {
            logger.info("Generating or Loading Closure...");
            Set<Sense> closureSet = generateTranslationClosureWithSignatures(instantiateDBNary());

            Translator translator = new CachedTranslator("Bing", new BingAPITranslator(BING_APP_ID, BING_APP_KEY));

            printTranslatedClosure(closureSet, translator, Language.ENGLISH);

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                InstantiationException | ClassNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private static Set<Sense> flatSenseClosure(LexicalResourceTranslationClosure<Sense> closure) {
        return closure.senseFlatClosure();
    }

    private static Set<Sense> generateTranslationClosureWithSignatures(DBNary dbNary) throws NoSuchVocableException {
        LexicalResourceTranslationClosure<LexicalSense> closure;

        if (CLOSURE_SAVE_PATH.exists()) {
            TranslationClosureGenerator gtc = TranslationClosureGeneratorFactory.createFileGenerator(dbNary, CLOSURE_SAVE_PATH.getAbsolutePath());
            closure = generateLexicalSenseClosure(gtc, DEPTH);
        } else {
            Vocable v = dbNary.getVocable("river", Language.ENGLISH);
            List<LexicalEntry> ventries = dbNary.getLexicalEntries(v);
            if (!ventries.isEmpty()) {
                TranslationClosureGenerator gtc = TranslationClosureGeneratorFactory.createVocablePOSGenerator(v, "http://www.lexinfo.net/ontology/2.0/lexinfo#noun", dbNary);
                closure = generateLexicalSenseClosure(gtc, DEPTH);
            } else {
                closure = new LexicalResourceTranslationClosureImpl();
            }
        }
        TranslationClosureSemanticSignatureGenerator semanticSignatureGenerator =
                new TranslationClosureSemanticSignatureGeneratorImpl();
        LexicalResourceTranslationClosure<Sense> sigClosure = semanticSignatureGenerator.generateSemanticSignatures(closure);
        logger.info(sigClosure.toString());
        return flatSenseClosure(sigClosure);
    }

    private static LexicalResourceTranslationClosure<LexicalSense> generateLexicalSenseClosure(TranslationClosureGenerator ctg, int degree) {
        return ctg.generateClosure(degree);
    }

    private static DBNary instantiateDBNary() throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Store vts = new JenaTDBStore(DB_PATH);
        StoreHandler.registerStoreInstance(vts);
        //StoreHandler.DEBUG_ON = true;
        OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        // Creating DBNary wrapper
        return (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, loadLanguages);
    }

    @SuppressWarnings("FeatureEnvy")
    private synchronized static void printTranslatedClosure(Iterable<Sense> closure, Translator translator, Language targetLanguage) {
        for (Sense sense : closure) {
            SemanticSignature originalSignature = sense.getSemanticSignature();
            String definition = sense.getDefinition();
            String translatedDefinition = translator.translate(definition, sense.getLanguage(), targetLanguage);
            StringSemanticSignature translatedSignature = new StringSemanticSignatureImpl();
            StringTokenizer tokenizer = new StringTokenizer(translatedDefinition);
            while (tokenizer.hasMoreTokens()) {
                translatedSignature.addSymbol(new StringSemanticSymbolImpl(tokenizer.nextToken(), 1d));
            }
            sense.setSemanticSignature(translatedSignature);
            logger.info(sense.toString());
            sense.setSemanticSignature(originalSignature);
        }
    }
}
