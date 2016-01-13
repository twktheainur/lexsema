package org.getalp.lexsema.wsd.experiments.embeddings;

import edu.mit.jwi.Dictionary;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.io.word2vec.SerializedModelWord2VecLoader;
import org.getalp.lexsema.io.word2vec.Word2VecLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.word2vec.Word2VecGlossCosineSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.evaluation.Semeval2007GoldStandard;
import org.getalp.lexsema.wsd.evaluation.StandardEvaluation;
import org.getalp.lexsema.wsd.method.MultiThreadCuckooSearch;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.MultiThreadConfigurationScorerWithCache;

import java.io.File;

public final class EmbeddingsDisambiguation
{
    private EmbeddingsDisambiguation() {
    }

    public static void main(String[] args) throws java.io.IOException {
        int iterations = 50000;
        double minLevyLocation = 1;
        double maxLevyLocation = 5;
        double minLevyScale = 0.5;
        double maxLevyScale = 1.5;
         
        //if (args.length >= 1) iterations = Integer.valueOf(args[0]);
        /*
        if (args.length >= 2) levyLocation = Double.valueOf(args[1]);
        if (args.length >= 3) levyScale = Double.valueOf(args[2]);
        if (args.length >= 4) nestsNumber = Integer.valueOf(args[3]);
        if (args.length >= 5) destroyedNests = Integer.valueOf(args[4]);
        */
        /*
        System.out.println("Parameters value : " +
                           "<scorer calls = " + iterations + "> " +
                           "<levy location = " + levyLocation + "> " +
                           "<levy scale = " + levyScale + "> " +
                           "<nests number = " + nestsNumber + "> " +
                           "<destroyed nests = " + destroyedNests + "> ");
        */
        long startTime = System.currentTimeMillis();

        CorpusLoader dl = new Semeval2007CorpusLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml");

        Dictionary dictionary = new Dictionary(new File("../data/wordnet/2.1/dict"));
        dictionary.open();

        LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7_embeddings.xml"), false);

        Word2VecLoader word2VecLoader = new SerializedModelWord2VecLoader();
        word2VecLoader.loadGoogle(new File(args[0]),true);
            WordVectors vectors = word2VecLoader.getWordVectors();

        //ConfigurationScorer scorer = new SemEval2007Task7PerfectConfigurationScorer();
        //ConfigurationScorer scorer = new ConfigurationScorerWithCache(new Word2VecGlossDistanceSimilarity(vectors,new MahalanobisDistance(), (Filter)null));
        //ConfigurationScorer perfectScorer = new SemEval2007Task7PerfectConfigurationScorer();
        //ConfigurationScorer scorer = new MultiThreadConfigurationScorerWithCache(new Word2VecGlossDistanceSimilarity(vectors,new MahalanobisDistance(), (Filter)null));
        //ConfigurationScorer scorer = new MultiThreadConfigurationScorerWithCache(new AnotherLeskSimilarity());
        ConfigurationScorer scorer = new MultiThreadConfigurationScorerWithCache(new Word2VecGlossCosineSimilarity(vectors,false));
        //ConfigurationScorer scorer = new MatrixConfigurationScorer(new AnotherLeskSimilarity(), new SumMatrixScorer(),Runtime.getRuntime().availableProcessors());
        //CuckooSearchDisambiguator cuckooDisambiguator = new CuckooSearchDisambiguator(new StopCondition(StopCondition.Condition.SCORERCALLS, iterations), levyLocation, levyScale, nestsNumber, destroyedNests, scorer, true);
        MultiThreadCuckooSearch cuckooDisambiguator = new MultiThreadCuckooSearch(iterations, minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, scorer, true);
        
        System.out.println("Loading texts...");
        dl.load();

        Semeval2007GoldStandard goldStandard = new Semeval2007GoldStandard();
        StandardEvaluation evaluation = new StandardEvaluation();

        for (Document d : dl)
        {
            System.out.println("Starting document " + d.getId());
            
            System.out.println("Loading senses...");
            lrloader.loadSenses(d);

            System.out.println("Disambiguating...");
            Configuration c = cuckooDisambiguator.disambiguate(d);
            System.err.println(evaluation.evaluate(goldStandard, c));
            //System.out.println("Writing results...");
            //SemevalWriter sw = new SemevalWriter("../" + d.getId() + ".ans");
            //sw.write(d, c.getAssignments());
            
            System.out.println("Done!");
        }
        
        cuckooDisambiguator.release();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Cuckoo Search Algorithm is : ");
        System.out.println((endTime - startTime) + " ms.");
        System.out.println(((endTime - startTime) / 1000l) + " s.");
        System.out.println(((endTime - startTime) / 60000l) + " m.");
    }
}
