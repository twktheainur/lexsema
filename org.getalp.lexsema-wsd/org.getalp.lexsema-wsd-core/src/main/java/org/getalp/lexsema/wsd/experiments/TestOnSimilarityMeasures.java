package org.getalp.lexsema.wsd.experiments;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.lesk.IndexedLeskSimilarity;
import org.getalp.lexsema.similarity.measures.lesk.SimpleLeskSimilarity;
import org.getalp.lexsema.similarity.measures.lesk.VectorizedLeskSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.*;
import org.getalp.lexsema.wsd.score.*;

import com.google.common.collect.Iterables;
import com.google.common.math.DoubleMath;

import cern.colt.Arrays;

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
        List<String> dicts_list = new ArrayList<>();
        dicts_list.add("../data/lesk_dict/all/zebest"); 
        compareDicts(dicts_list.toArray(new String[dicts_list.size()]));
    }
    
    private static void compareDicts(String[] dicts) throws Exception
    {
        Result[] res = new Result[dicts.length];
        for (int i = 0 ; i < res.length ; i++)
        {
            res[i] = getScores(dicts[i], 30);
            System.out.println("Test " + i + " (" + dicts[i] + ")");
            System.out.println("Mean Scores : " + res[i].meanScore);
            System.out.println("Standard Deviation Scores : " + res[i].standardDeviationScore);
            System.out.println("Mean Times : " + res[i].meanTime);
            System.out.println();
        }
        System.out.println("Recap:");
        for (int i = 0 ; i < res.length ; i++)
        {
            System.out.println("Test " + i + " (" + dicts[i] + ")");
            System.out.println("Mean Scores : " + res[i].meanScore);
            System.out.println("Standard Deviation Scores : " + res[i].standardDeviationScore);
            System.out.println("Mean Times : " + res[i].meanTime);
            System.out.println();
        }
        for (int i = 0 ; i < res.length ; i++)
        {
            for (int j = 0 ; j < res.length ; j++)
            {
                System.out.println("MWUTest " + i + " (" + dicts[i] + ") / " + j + " (" + dicts[j] + ") : " + mannTest.mannWhitneyUTest(res[i].scores, res[j].scores));
            }
        }
    }
    
    private static Result getScores(String dict, int n) throws Exception
    {
        double[] scores = new double[n];
        long[] times = new long[n];
        LRLoader lrloader = new DictionaryLRLoader(new FileInputStream(dict), true, false);

        CorpusLoader dl = new Semeval2007CorpusLoader(new FileInputStream("../data/senseval2007_task7/test/eng-coarse-all-words.xml"));
        dl.load();
        for (Document d : dl) lrloader.loadSenses(d);

        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new IndexedLeskSimilarity());
            
        SemEval2007Task7PerfectConfigurationScorer perfectScorer = new SemEval2007Task7PerfectConfigurationScorer();

        int iterations = 100000;
        double minLevyLocation = 1;
        double maxLevyLocation = 5;
        double minLevyScale = 0.5;
        double maxLevyScale = 1.5;

        MultiThreadCuckooSearch cuckooDisambiguator = new MultiThreadCuckooSearch(iterations, minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, scorer, false);               
        Disambiguator disambiguator = cuckooDisambiguator;
        
        System.out.println("Dictionary " + dict);
        
        for (int i = 0 ; i < n ; i++)
        {
            System.out.print("" + (i+1) + "/" + n + " ");
            System.out.flush();
            scores[i] = 0;
            int j = 0;
            long startTime = System.currentTimeMillis();
            for (Document d : dl)
            {
                System.out.print("(" + d.getId() + ") ");
                System.out.flush();
                Configuration c = disambiguator.disambiguate(d);
                double tmp_score = perfectScorer.computeScore(d, c);
                System.out.print("[" + new DecimalFormat("##.##").format(tmp_score * 100) + "] ");
                System.out.flush();
                scores[i] += tmp_score;
                j++;
            }
            long endTime = System.currentTimeMillis();
            times[i] = (endTime - startTime);
            scores[i] /= ((double) j);
            System.out.println("score : " + scores[i] + " ; time : " + times[i]);
        }
        System.out.println();
        disambiguator.release();
        return new Result(scores, times);
    }
    
    private static Result getScoresWithVote(String[] dicts, int n) throws Exception
    {
        assert(dicts.length >= 1);
        CorpusLoader[] dls = new CorpusLoader[dicts.length];
        for (int i = 0 ; i < dicts.length ; i++)
        {
            LRLoader lrloader = new DictionaryLRLoader(new FileInputStream(dicts[i]), true);
            dls[i] = new Semeval2007CorpusLoader(new FileInputStream("../data/senseval2007_task7/test/eng-coarse-all-words.xml"));
            dls[i].load();
            for (Document d : dls[i]) lrloader.loadSenses(d);
        }

        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new VectorizedLeskSimilarity());
            
        SemEval2007Task7PerfectConfigurationScorer perfectScorer = new SemEval2007Task7PerfectConfigurationScorer();

        int iterations = 100000;
        double minLevyLocation = 1;
        double maxLevyLocation = 5;
        double minLevyScale = 0.5;
        double maxLevyScale = 1.5;

        MultiThreadCuckooSearch cuckooDisambiguator = new MultiThreadCuckooSearch(iterations, minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, scorer, false);

        VoteDisambiguator voteDisambiguator = new VoteDisambiguator(cuckooDisambiguator, n);
               
        System.out.println("Dictionaries " + Arrays.toString(dicts));
        
        double score = 0;
        long time = 0;
        long startTime = System.currentTimeMillis();
        for (int i = 0 ; i < Iterables.size(dls[0]) ; i++)
        {
            List<Document> docs = new ArrayList<Document>();
            for (int j = 0 ; j < dls.length ; j++)
            {
                docs.add(Iterables.get(dls[j], i));
            }
            Configuration c = voteDisambiguator.disambiguate(docs.toArray(new Document[docs.size()]));
            SemevalWriter sw = new SemevalWriter("../data/" + docs.get(0).getId() + ".ans");
            sw.write(docs.get(0), c.getAssignments());
            double tmp_score = perfectScorer.computeScore(docs.get(0), c);
            System.out.println("(" + docs.get(0).getId() + ") [" + tmp_score + "]");
            score += tmp_score;
        }
        long endTime = System.currentTimeMillis();
        time = (endTime - startTime);
        score /= ((double) Iterables.size(dls[0]));
        System.out.println("score : " + score + " ; time : " + time);
        System.out.println();
        voteDisambiguator.release();
        return new Result(new double[]{score}, new long[]{time});
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
