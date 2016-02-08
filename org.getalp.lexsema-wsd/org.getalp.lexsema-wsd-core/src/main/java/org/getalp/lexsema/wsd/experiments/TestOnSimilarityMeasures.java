package org.getalp.lexsema.wsd.experiments;

import java.io.FileInputStream;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.lesk.IndexedDiceLeskSimilarity;
import org.getalp.lexsema.similarity.measures.lesk.IndexedLeskSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.*;
import org.getalp.lexsema.wsd.score.*;
import com.google.common.math.DoubleMath;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class TestOnSimilarityMeasures
{
    private static class Result
    {
        public Result(double[] scores, long[] times)
        {
            this.scores = scores;
            this.meanScore = getMean(this.scores);
            this.standardDeviationScore = new StandardDeviation().evaluate(scores, meanScore);
            this.times = times;
            this.meanTime = getMean(this.times);
        }
        public double[] scores;
        public double meanScore;
        public double standardDeviationScore;
        public long[] times;
        public long meanTime;
    }

    private static MannWhitneyUTest mannTest = new MannWhitneyUTest();

    public static void main(String[] args) throws Exception
    {
        Result res = getScores("../data/lesk_dict/semeval2007task7/7/150", false);

        System.out.println("Test 7/150 with Dice");
        System.out.println("Mean Scores : " + res.meanScore);
        System.out.println("Standard Deviation Scores : " + res.standardDeviationScore);
        System.out.println("Mean Times : " + res.meanTime);

        Result res2 = getScores("../data/lesk_dict/semeval2007task7/7/150", true);

        System.out.println("Test 7/150 without Dice");
        System.out.println("Mean Scores : " + res2.meanScore);
        System.out.println("Standard Deviation Scores : " + res2.standardDeviationScore);
        System.out.println("Mean Times : " + res2.meanTime);
        
        System.out.println("MWUTest 1 / 2 : " + mannTest.mannWhitneyUTest(res.scores, res2.scores));
    }

    private static Result getScores(String dict, boolean patate) throws Exception
    {
        int n = 30;
        double[] scores = new double[n];
        long[] times = new long[n];
        LRLoader lrloader = new DictionaryLRLoader(new FileInputStream(dict), true);

        CorpusLoader dl = new Semeval2007CorpusLoader(new FileInputStream("../data/senseval2007_task7/test/eng-coarse-all-words.xml"));
        dl.load();
        for (Document d : dl) lrloader.loadSenses(d);

        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new IndexedDiceLeskSimilarity());
        if (patate) scorer = new ConfigurationScorerWithCache(new IndexedLeskSimilarity());
            
        SemEval2007Task7PerfectConfigurationScorer perfectScorer = new SemEval2007Task7PerfectConfigurationScorer();

        int iterations = 100000;
        double minLevyLocation = 1;
        double maxLevyLocation = 5;
        double minLevyScale = 0.5;
        double maxLevyScale = 1.5;

        MultiThreadCuckooSearch cuckooDisambiguator = new MultiThreadCuckooSearch(iterations, minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, scorer, false);

        System.out.println("Dictionary " + dict);
        
        for (int i = 0 ; i < n ; i++)
        {
            System.out.print("" + i + "/" + n + " ");
            System.out.flush();
            scores[i] = 0;
            int j = 0;
            long startTime = System.currentTimeMillis();
            for (Document d : dl)
            {
                System.out.print("(" + d.getId() + ") ");
                System.out.flush();
                Configuration c = cuckooDisambiguator.disambiguate(d);
                scores[i] += perfectScorer.computeScore(d, c);
                j++;
            }
            long endTime = System.currentTimeMillis();
            times[i] = (endTime - startTime);
            scores[i] /= ((double) j);
            System.out.println("score : " + scores[i] + " ; time : " + times[i]);
        }
        System.out.println();
        cuckooDisambiguator.release();
        return new Result(scores, times);
    }

    private static double getMean(double[] array)
    {
        return DoubleMath.mean(array);
    }

    private static long getMean(long[] array)
    {
        return (long) DoubleMath.mean(array);
    }
}
