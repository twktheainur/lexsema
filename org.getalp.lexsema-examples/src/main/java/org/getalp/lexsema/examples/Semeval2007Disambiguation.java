package org.getalp.lexsema.examples;

import edu.mit.jwi.Dictionary;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.io.word2vec.MultilingualSerializedModelWord2VecLoader;
import org.getalp.lexsema.io.word2vec.MultilingualWord2VecLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.word2vec.Word2VecGlossDistanceSimilarity;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.evaluation.Evaluation;
import org.getalp.lexsema.wsd.evaluation.GoldStandard;
import org.getalp.lexsema.wsd.evaluation.Semeval2007GoldStandard;
import org.getalp.lexsema.wsd.evaluation.StandardEvaluation;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.sequencial.SimplifiedLesk;
import org.getalp.lexsema.wsd.method.sequencial.parameters.SimplifiedLeskParameters;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;
import org.getalp.ml.matrix.distance.RiemannianDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class Semeval2007Disambiguation {
    private static Logger logger = LoggerFactory.getLogger(TextSimilarity.class);

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        if(args.length<1){
            usage();
        }

        CorpusLoader corpusLoader = new Semeval2007CorpusLoader("../data/senseval2007_task7/test/eng-coarse-all-words-t1.xml");

        LRLoader lrloader = new WordnetLoader(new Dictionary(new File("../data/wordnet/2.1/dict")))
                .extendedSignature(true).loadDefinitions(true);
        MultilingualWord2VecLoader word2VecLoader = new MultilingualSerializedModelWord2VecLoader();
        word2VecLoader.loadGoogle(new File(args[0]),true);

        SimilarityMeasure similarityMeasure =
                new Word2VecGlossDistanceSimilarity(word2VecLoader.getWordVectors(Language.ENGLISH),
                        new RiemannianDistance(), null);
//        SimilarityMeasure similarityMeasure =
//                new Word2VecGlossCosineSimilarity(word2VecLoader.getWordVectors(Language.ENGLISH),
//                       false);

//        SimilarityMeasure similarityMeasure =
//                new TverskiIndexSimilarityMeasureBuilder()
//                        .distance(new ScaledLevenstein()).alpha(1d).beta(0.0d).gamma(0.0d).fuzzyMatching(true)
//                        .build();
        ConfigurationScorer configurationScorer =
                new TverskyConfigurationScorer(similarityMeasure,Runtime.getRuntime().availableProcessors());

        GoldStandard goldStandard = new Semeval2007GoldStandard();
        Evaluation evaluation = new StandardEvaluation();
        SimplifiedLeskParameters simplifiedLeskParameters = new SimplifiedLeskParameters().setFallbackFS(false);
        Disambiguator disambiguator = new SimplifiedLesk(10 ,similarityMeasure,simplifiedLeskParameters,1);
        System.err.println("Loading texts");
        corpusLoader.load();
        for (Document document : corpusLoader) {
            System.err.println("\tLoading senses...");
            lrloader.loadSenses(document);
            System.err.println("\tDisambiguating... ");
            Configuration result = disambiguator.disambiguate(document);
            logger.info(evaluation.evaluate(goldStandard,result).toString());
//            for(int i=0;i<result.size();i++){
//                Word word = document.getWord(i);
//                if(!document.getSenses(i).isEmpty()) {
//                    String sense = document.getSenses(i).get(result.getAssignment(i)).getId();
//                    logger.info("Sense " + sense + " assigned to " + word);
//                }
//            }

            System.err.println("done!");
        }
        disambiguator.release();
    }

    private static void usage() {
        logger.error("Usage -- org.getalp.lexsema.examples.TextDisambiguation \"My text\"");
        System.exit(1);
    }
}
