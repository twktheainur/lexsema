package org.getalp.lexsema.wsd.experiments;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.lesk.AnotherLeskSimilarity;
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
	Result res = getScores("../data/lesk_dict/semeval2007task7/0");
        System.out.println("Test 0");
        System.out.println("Mean Scores : " + res.meanScore);
        System.out.println("Standard Deviation Scores : " + res.standardDeviationScore);
        System.out.println("Mean Times : " + res.meanTime);
	/*
        Result[][] res = new Result[15][6];

        for (int i = 0 ; i < 15 ; i++)
        {
            for (int j = 0 ; j < 6 ; j++)
            {
                int k = i + 1;
                int l = (j + 1) * 50;
                res[i][j] = getScores("../data/lesk_dict/semeval2007task7/" + k + "/" + l);
                System.out.println("Test " + k + "/" + l);
                System.out.println("Mean Scores : " + res[i][j].meanScore);
                System.out.println("Standard Deviation Scores : " + res[i][j].standardDeviationScore);
                System.out.println("Mean Times : " + res[i][j].meanTime);
            }
        }

        for (int i = 0 ; i < 15 ; i++)
        {
            for (int j = 0 ; j < 6 ; j++)
            {
                for (int k = 0 ; k < 15 ; k++)
                {
                    for (int l = 0 ; l < 6 ; l++)
                    {
                        System.out.println("MWUTest " + i + "/" + j + " vs " + k + "/" + l + " : " + mannTest.mannWhitneyUTest(res[i][j].scores, res[k][l].scores));
                    }
                }
            }
        }
	*/
    }

    private static Result getScores(String dict) throws Exception
    {
        int n = 30;
        double[] scores = new double[n];
        long[] times = new long[n];
        LRLoader lrloader = new DictionaryLRLoader(new FileInputStream(dict), true);

        CorpusLoader dl = new Semeval2007CorpusLoader(new FileInputStream("../data/senseval2007_task7/test/eng-coarse-all-words.xml"));
        dl.load();
        for (Document d : dl) lrloader.loadSenses(d);

        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new AnotherLeskSimilarity());
        //ConfigurationScorer scorer = new MultiThreadConfigurationScorerWithCache(new AnotherLeskSimilarity());

        SemEval2007Task7PerfectConfigurationScorer perfectScorer = new SemEval2007Task7PerfectConfigurationScorer();

        int iterations = 50000;
        double minLevyLocation = 1;
        double maxLevyLocation = 5;
        double minLevyScale = 0.5;
        double maxLevyScale = 1.5;

        //CuckooSearchDisambiguator cuckooDisambiguator = new CuckooSearchDisambiguator(new StopCondition(StopCondition.Condition.SCORERCALLS, iterations), levyLocation, levyScale, nestsNumber, destroyedNests, scorer, false);
        MultiThreadCuckooSearch cuckooDisambiguator = new MultiThreadCuckooSearch(iterations, minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, scorer, false);

        for (int i = 0 ; i < n ; i++)
        {
            System.out.print("" + i + " ");
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
