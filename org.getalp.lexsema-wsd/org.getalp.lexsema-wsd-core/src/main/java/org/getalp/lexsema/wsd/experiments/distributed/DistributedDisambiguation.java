package org.getalp.lexsema.wsd.experiments.distributed;

import org.getalp.lexsema.io.annotresult.ConfigurationWriter;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.measures.lesk.AnotherLeskSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.evaluation.*;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.MultiThreadCuckooSearch;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.DistributedConfigurationScorerWithCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class DistributedDisambiguation
{
    private static final Logger logger = LoggerFactory.getLogger(DistributedDisambiguation.class);
    private static final long MS_IN_S = 1000L;
    private static final long MS_IN_M = 60000L;
    private static final int ITERATIONS = 50000;
    private static final double MIN_LEVY_SCALE = 0.5;
    private static final double MAX_LEVY_SCALE = 1.5;

    private DistributedDisambiguation() {
    }

    private static CorpusLoader loadCorpus(String resourceURI){

        CorpusLoader corpusLoader = new Semeval2007CorpusLoader(DistributedDisambiguation.class.getResourceAsStream(resourceURI));
        corpusLoader.load();
        return corpusLoader;
    }

    private static LRLoader loadLexicalResource(String resourceURI){
        return new DictionaryLRLoader(DistributedDisambiguation.class.getResourceAsStream(resourceURI), true);
    }

    private static void loadSensesForDocument(Iterable<Text> corpusLoader, LRLoader lrLoader){

        for(Text document: corpusLoader) {
            logger.info("\tLoading senses for {}...",document.getId());
            lrLoader.loadSenses(document);
        }
    }

    private static void disambiguate(Iterable<Text> corpusLoader, Disambiguator disambiguator){
        GoldStandard goldStandard = new Semeval2007GoldStandard();
        Evaluation evaluation = new StandardEvaluation();
        for (Document document : corpusLoader) {
            logger.info("Disambiguating document {}", document.getId());
            Configuration configuration = disambiguator.disambiguate(document);
            logger.info(evaluation.evaluate(goldStandard, configuration).toString());
            logger.info("Writing results...");
            ConfigurationWriter sw = new SemevalWriter(String.format("../%s.ans", document.getId()));
            sw.write(document, getConfigurationAssignments(configuration));
            logger.info("Done!");
        }
    }

    private static int[] getConfigurationAssignments(Configuration configuration){
        return configuration.getAssignments();
    }

    public static void main(String... args) throws java.io.IOException {
        int iterations = ITERATIONS;
        double minLevyLocation = 1;
        double maxLevyLocation = 5;
        double minLevyScale = MIN_LEVY_SCALE;
        double maxLevyScale = MAX_LEVY_SCALE;

        long startTime = System.currentTimeMillis();

        logger.info("Loading corpus...");
        CorpusLoader corpusLoader = loadCorpus("/semeval2007/eng-coarse-all-words.xml");

        logger.info("Loading lexical resource...");
        LRLoader lrLoader = loadLexicalResource("/semeval2007/dict_semeval2007task7_embeddings.xml");

        loadSensesForDocument(corpusLoader,lrLoader);

        ConfigurationScorer scorer = new DistributedConfigurationScorerWithCache(new AnotherLeskSimilarity(),"spark://localhost:12345");
        Disambiguator cuckooDisambiguator = new MultiThreadCuckooSearch(iterations, minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, scorer, true);
        disambiguate(corpusLoader,cuckooDisambiguator);
        cuckooDisambiguator.release();
        
        long endTime = System.currentTimeMillis();
        logger.info("Total time elapsed in execution of Cuckoo Search Algorithm is : ");
        logger.info("{} ms.", endTime - startTime);
        logger.info("{} s.", (endTime - startTime) / MS_IN_S);
        logger.info("{} m.", (endTime - startTime) / MS_IN_M);
    }
}
