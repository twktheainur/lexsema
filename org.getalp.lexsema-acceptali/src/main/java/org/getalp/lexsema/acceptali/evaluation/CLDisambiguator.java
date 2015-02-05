package org.getalp.lexsema.acceptali.evaluation;

import com.wcohen.ss.ScaledLevenstein;
import edu.stanford.nlp.util.Pair;
import org.getalp.lexsema.io.annotresult.CLWSDWriter;
import org.getalp.lexsema.io.clwsd.CLWSDLoader;
import org.getalp.lexsema.io.clwsd.TargetWordEntry;
import org.getalp.lexsema.io.clwsd.TargetedWSDLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dbnary.DBNaryLoader;
import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Translation;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaTDBStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.sequencial.SimplifiedLesk;
import org.getalp.lexsema.wsd.method.sequencial.parameters.SimplifiedLeskParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CLDisambiguator {

    public static final String targetCWSDDirectory = File.separatorChar + "Users" + File.separatorChar +
            "tchechem" + File.separatorChar + "wsgetalp" + File.separatorChar + "data" + File.separatorChar +
            "semeval2013_task10_clwsd" + File.separatorChar + "Sample1WAllSent_Task3";

    public static final String outputCWSDDirectory = File.separatorChar + "Users" + File.separatorChar +
            "tchechem" + File.separatorChar + "wsgetalp" + File.separatorChar + "data" + File.separatorChar +
            "semeval2013_task10_clwsd" + File.separatorChar + "Result";

    public static final String DB_PATH = ".." + File.separatorChar + "data" + File.separatorChar + "dbnary" + File.separatorChar + "dbnary_full";
    public static final String ONTOLOGY_PROPERTIES = "data" + File.separatorChar + "ontology.properties";
    private final static Logger logger = LoggerFactory.getLogger(CLDisambiguator.class);
    static Language[] loadLanguages = {
            Language.FRENCH, Language.ITALIAN, Language.SPANISH,
            Language.GERMAN
    };
    private Map<Language, DBNary> dbnaryMap;
    private DBNary sourceDbnary;

    public CLDisambiguator(DBNary sourceDbnary) {

        System.setProperty("org.slf4j.simpleLogger.showShortLogName", "true");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");

        dbnaryMap = new HashMap<>();
        this.sourceDbnary = sourceDbnary;
        registerDBNary(sourceDbnary.getLanguage(), sourceDbnary);
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        long startTime = System.currentTimeMillis();

        Store vts = new JenaTDBStore(DB_PATH);
        //vts.setCachingEnabled(true);
        StoreHandler.registerStoreInstance(vts);
        //StoreHandler.DEBUG_ON = true;
        OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        // Creating DBNary wrapper

        CLDisambiguator clDisambiguator = new CLDisambiguator((DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, Language.ENGLISH));
        for (Language l : loadLanguages) {
            try {
                clDisambiguator.registerDBNary(l, (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, l));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                    InstantiationException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        LRLoader lrloader = new DBNaryLoader(DB_PATH, ONTOLOGY_PROPERTIES, Language.ENGLISH).loadDefinition(true);

        SimilarityMeasure similarityMeasure;

        //noinspection LawOfDemeter
        similarityMeasure = new TverskiIndexSimilarityMeasureBuilder()
                .distance(new ScaledLevenstein())
                .computeRatio(true)
                .alpha(1d)
                .beta(0.5d)
                .gamma(0.5d)
                .fuzzyMatching(true)
                .quadraticWeighting(false)
                .extendedLesk(false)
                .randomInit(false)
                .regularizeOverlapInput(false)
                .optimizeOverlapInput(false)
                .regularizeRelations(false)
                .optimizeRelations(false)
                .isDistance(true)
                .build();

        SimplifiedLeskParameters algorithmParameters = new SimplifiedLeskParameters()
                .setMinimize(false);
        Disambiguator disambiguator = new SimplifiedLesk(20, similarityMeasure, algorithmParameters, 1);

        clDisambiguator.processTargets(lrloader, disambiguator);
        disambiguator.release();
        logger.info(String.format("runtime: %.2f m", (double) (System.currentTimeMillis() - startTime) / 1000d / 60d));
    }

    public void registerDBNary(Language language, DBNary dbNary) {
        dbnaryMap.put(language, dbNary);
    }

    public void processTargets(LRLoader lrLoader, Disambiguator disambiguator) {
        CLWSDWriter writer = null;
        try {
            writer = new CLWSDWriter(outputCWSDDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        TargetedWSDLoader targetWSDLoader = new CLWSDLoader(targetCWSDDirectory);
        logger.info("Loading dataset...");
        targetWSDLoader.load();
        for (TargetWordEntry entry : targetWSDLoader) {
            Word targetWord = entry.getTargetWord();
            int contextIndex = 1;
            loggerInfoTargetWordProgress(targetWord);
            for (Pair<Sentence, Integer> context : entry) {
                Sentence s = context.first();
                //Integer index = findTargetIndex(s, targetWord);
                Integer index = context.second();
                if (index != -1) {
                    logger.info(String.format("Loading senses for context %d ...", contextIndex));
                    lrLoader.loadSenses(s);
                    logger.info(String.format("Disambiguating context %d ...", contextIndex));
                    Configuration c = disambiguator.disambiguate(s);
                    Sense targetSense = getTargetSense(s, c, index);

                    if (targetSense != null) {

                        List<Translation> translations;

                        translations = sourceDbnary.getTranslations(targetSense, loadLanguages);

                        if (!translations.isEmpty()) {
                            for(Language targetLanguage: loadLanguages) {
                                Translation translation = null;
                                for (Translation candidateTranslation : translations) {
                                    Language candidateLanguage = candidateTranslation.getLanguage();
                                    if(candidateLanguage.equals(targetLanguage)){
                                        translation = candidateTranslation;
                                        break;
                                    }
                                }
                                if(translation!=null) {
                                    writer.writeEntry(targetWord, contextIndex, translation, targetLanguage);
                                }
                            }
                        }
                    }
                    contextIndex++;
                }
            }
        }
    }


    private int findTargetIndex(Document s, LexicalEntry targetWord) {
        for (int i = 0; i < s.size(); i++) {
            Word currentWord = s.getWord(0, i);
            if (matchLemma(currentWord, targetWord)) {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("LawOfDemeter")
    private boolean matchLemma(LexicalEntry a, LexicalEntry b) {
        return a.getLemma().equals(b.getLemma());
    }

    private void loggerInfoTargetWordProgress(Word targetWord) {
        logger.info(String.format("Processing %s ...", targetWord.getId()));
    }

    private Sense getTargetSense(Document s, Configuration c, int index) {
        int assignment = c.getAssignment(index);
        if (assignment > -1) {
            return s.getSenses(index).get(assignment);
        } else {
            return null;
        }
    }
}
