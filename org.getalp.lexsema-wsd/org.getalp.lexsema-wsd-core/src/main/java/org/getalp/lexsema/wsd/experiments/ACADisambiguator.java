package org.getalp.lexsema.wsd.experiments;

import edu.mit.jwi.Dictionary;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.lesk.AnotherLeskSimilarity;
import org.getalp.lexsema.similarity.measures.lesk.IndexedOverlapSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.org.getalp.lexsema.wsd.evaluation.Evaluation;
import org.getalp.lexsema.wsd.configuration.org.getalp.lexsema.wsd.evaluation.GoldStandard;
import org.getalp.lexsema.wsd.configuration.org.getalp.lexsema.wsd.evaluation.Semeval2007GoldStandard;
import org.getalp.lexsema.wsd.configuration.org.getalp.lexsema.wsd.evaluation.StandardEvaluation;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.MultiThreadCuckooSearch;
import org.getalp.lexsema.wsd.method.aca.AntColonyAlgorithm;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.MatrixConfigurationScorer;
import org.getalp.lexsema.wsd.score.MultiThreadConfigurationScorerWithCache;
import org.getalp.lexsema.wsd.score.SemEval2007Task7PerfectConfigurationScorer;
import org.getalp.ml.matrix.score.SumMatrixScorer;

import java.io.File;
import java.io.PrintWriter;

public class ACADisambiguator
{
    public static void main(String[] args) throws Exception
    {
        int iterations = 100;
        double initialEnergy = 20;
        int initialPheromone = 0;
        int vectorSize = 100;
        double pheromoneEvaporation = 0.7;
        double maximumEnergy = 30;
        double antLife = 10;
        double depositPheromone = 1;
        double takeEnergy = 1;
        double componentsDeposited = 0.5;

//        if (args.length >= 1) iterations = Integer.valueOf(args[0]);
//        if (args.length >= 2) levyLocation = Double.valueOf(args[1]);
//        if (args.length >= 3) levyScale = Double.valueOf(args[2]);
//        if (args.length >= 4) nestsNumber = Integer.valueOf(args[3]);
//        if (args.length >= 5) destroyedNests = Integer.valueOf(args[4]);
//
//        System.out.println("Parameters value : " +
//                           "<scorer calls = " + iterations + "> " +
//                           "<levy location = " + levyLocation + "> " +
//                           "<levy scale = " + levyScale + "> " +
//                           "<nests number = " + nestsNumber + "> " +
//                           "<destroyed nests = " + destroyedNests + "> ");

        long startTime = System.currentTimeMillis();

        CorpusLoader dl = new Semeval2007CorpusLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml");

        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations-apriori.xml"));

        LRLoader lrloader = new WordnetLoader(new Dictionary(new File("../data/wordnet/2.1/dict")))
                .extendedSignature(true)
                .shuffle(true)
                .filterStopWords(false)
                .stemming(false)
                .loadDefinitions(true);

        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7"), true);
        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7_stopwords"), true);
        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7_stemming"), true);
        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7_stopwords_stemming"), true);

        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7_semcor"), true);
        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7_stopwords_semcor"), true);
        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7_stemming_semcor"), true);
        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/all/dict_semeval2007task7_stopwords_stemming_semcor"), true);

        //ConfigurationScorer scorer = new SemEval2007Task7PerfectConfigurationScorer();
        //ConfigurationScorer scorer = new ACSimilarityConfigurationScorer(new ACExtendedLeskSimilarity());
        SimilarityMeasure similarityMeasure = new AnotherLeskSimilarity();
        //ConfigurationScorer scorer = new Conf(new IndexedOverlapSimilarity());
        //ConfigurationScorer scorer = new MultiThreadConfigurationScorerWithCache(new ACExtendedLeskSimilarity());
        //ConfigurationScorer scorer = new ConfigurationScorerWithCache(new AnotherLeskSimilarity());
        //ConfigurationScorer scorer = new MultiThreadConfigurationScorerWithCache(new AnotherLeskSimilarity());
        //ConfigurationScorer scorer = new MatrixConfigurationScorer(new AnotherLeskSimilarity(), new SumMatrixScorer(),Runtime.getRuntime().availableProcessors());
        //ConfigurationScorer scorer = new MultiThreadConfigurationScorerWithCache(new TverskiIndexSimilarityMeasureMatrixImplBuilder().alpha(1d).beta(0.5).gamma(0.5d).fuzzyMatching(false).computeRatio(true).matrixScorer(new SumMatrixScorer()).build());
        //ConfigurationScorer scorer = new TestScorer(new TverskyConfigurationScorer(new IndexedOverlapSimilarity(), Runtime.getRuntime().availableProcessors()));
        //ConfigurationScorer scorer = new MultiThreadConfigurationScorerWithCache(new TverskiIndexSimilarityMeasureBuilder().distance(new ScaledLevenstein()).alpha(1d).beta(0.0d).gamma(0.0d).fuzzyMatching(false).build());
        //ConfigurationScorer scorer = new MultiThreadConfigurationScorerWithCache(new TverskiIndexSimilarityMeasureBuilder().distance(new ScaledLevenstein()).alpha(1d).beta(0.0d).gamma(0.0d).fuzzyMatching(false).build());

        //CuckooSearchDisambiguator cuckooDisambiguator = new CuckooSearchDisambiguator(new StopCondition(StopCondition.Condition.SCORERCALLS, iterations), levyLocation, levyScale, nestsNumber, destroyedNests, scorer, true);
        Disambiguator disambiguator = new AntColonyAlgorithm(similarityMeasure,
                iterations,initialEnergy,initialPheromone,
                vectorSize,pheromoneEvaporation,maximumEnergy,
                antLife,depositPheromone,takeEnergy,componentsDeposited);

        GoldStandard goldStandard = new Semeval2007GoldStandard();
        Evaluation evaluation = new StandardEvaluation();

        System.out.println("Loading texts...");
        dl.load();

        for (Document d : dl)
        {
            System.out.println("Starting document " + d.getId());

            System.out.println("Loading senses...");
            lrloader.loadSenses(d);

            System.out.println("Disambiguating...");
            Configuration c = disambiguator.disambiguate(d);
            System.err.println(evaluation.evaluate(goldStandard,c));
            System.out.println("Writing results...");
            SemevalWriter sw = new SemevalWriter("../" + d.getId() + ".ans");
            sw.write(d, c.getAssignments());

            System.out.println("Done!");
        }

        disambiguator.release();

        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Cuckoo Search Algorithm is : ");
        System.out.println((endTime - startTime) + " ms.");
        System.out.println(((endTime - startTime) / 1000l) + " s.");
        System.out.println(((endTime - startTime) / 60000l) + " m.");
    }
}
