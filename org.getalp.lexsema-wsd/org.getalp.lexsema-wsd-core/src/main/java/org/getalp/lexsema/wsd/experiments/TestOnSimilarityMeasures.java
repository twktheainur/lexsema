package org.getalp.lexsema.wsd.experiments;

import java.io.File;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.lesk.AnotherLeskSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.CuckooSearchDisambiguator;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorerWithCache;
import org.getalp.lexsema.wsd.score.SemEval2007Task7PerfectConfigurationScorer;

import com.google.common.math.DoubleMath;

public class TestOnSimilarityMeasures
{
    public static void main(String[] args) throws Exception
    {
        //double[] scores = getScores("../data/lesk_dict/dict_semeval2007task7");
        //double[] scoresStopWords = getScores("../data/lesk_dict/dict_semeval2007task7_stopwords");
        //double[] scoresStemming = getScores("../data/lesk_dict/dict_semeval2007task7_stemming");
        double[] scoresStopWordsStemming = getScores("../data/lesk_dict/all/dict_semeval2007task7_stopwords_stemming");

        //double[] scoresSemCor = getScores("../data/lesk_dict/dict_semeval2007task7");
        //double[] scoresStopWordsSemCor = getScores("../data/lesk_dict/dict_semeval2007task7_stopwords");
        //double[] scoresStemmingSemCor = getScores("../data/lesk_dict/dict_semeval2007task7_stemming");
        double[] scoresStopWordsStemmingSemCor = getScores("../data/lesk_dict/all/dict_semeval2007task7_stopwords_stemming_semcor");

        //double meanScores = getMean(scores);
        //double meanScoresStopWords = getMean(scoresStopWords);
        //double meanScoresStemming = getMean(scoresStemming);
        double meanScoresStopWordsStemming = getMean(scoresStopWordsStemming);

        //double meanScoresSemCor = getMean(scoresSemCor);
        //double meanScoresStopWordsSemCor = getMean(scoresStopWordsSemCor);
        //double meanScoresStemmingSemCor = getMean(scoresStemmingSemCor);
        double meanScoresStopWordsStemmingSemCor = getMean(scoresStopWordsStemmingSemCor);
        
        //System.out.println("Mean Scores : " + meanScores);
        //System.out.println("Mean Scores StopWords : " + meanScoresStopWords);
        //System.out.println("Mean Scores Stemming : " + meanScoresStemming);
        System.out.println("Mean Scores StopWords Stemming: " + meanScoresStopWordsStemming);

        //System.out.println("Mean Scores SemCor : " + meanScoresSemCor);
        //System.out.println("Mean Scores StopWords SemCor : " + meanScoresStopWordsSemCor);
        //System.out.println("Mean Scores Stemming SemCor: " + meanScoresStemmingSemCor);
        System.out.println("Mean Scores StopWords Stemming SemCor: " + meanScoresStopWordsStemmingSemCor);
        
        MannWhitneyUTest mannTest = new MannWhitneyUTest();
        //System.out.println("MWUTest between Scores And Scores StopWords : " + mannTest.mannWhitneyUTest(scores, scoresStopWords));
        //System.out.println("MWUTest between Scores And Scores Stemming : " + mannTest.mannWhitneyUTest(scores, scoresStemming));
        //System.out.println("MWUTest between Scores And Scores StopWords And Stemming : " + mannTest.mannWhitneyUTest(scores, scoresStopWordsStemming));
        //System.out.println("MWUTest between Scores StopWords And Scores Stemming : " + mannTest.mannWhitneyUTest(scoresStopWords, scoresStemming));
        //System.out.println("MWUTest between Scores StopWords And Scores StopWords And Stemming : " + mannTest.mannWhitneyUTest(scoresStopWords, scoresStopWordsStemming));
        //System.out.println("MWUTest between Scores Stemming And Scores StopWords And Stemming : " + mannTest.mannWhitneyUTest(scoresStemming, scoresStopWordsStemming));
        
        System.out.println("MWUTest between Scores StopWords Stemming And Scores StopWords Stemming SemCor : " + mannTest.mannWhitneyUTest(scoresStopWordsStemming, scoresStopWordsStemmingSemCor));
    }
    
    private static double[] getScores(String dict)
    {
        int n = 100;
        double[] scores = new double[n];
        long[] times = new long[n];
        LRLoader lrloader = new DictionaryLRLoader(new File(dict), true);

        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml");
        dl.load();
        for (Document d : dl) lrloader.loadSenses(d);

        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new AnotherLeskSimilarity());

        SemEval2007Task7PerfectConfigurationScorer perfectScorer = new SemEval2007Task7PerfectConfigurationScorer();
        
        int iterations = 10000;
        double levyLocation = 2;
        double levyScale = 0.7;
        int nestsNumber = 1;
        int destroyedNests = 0;
        
        CuckooSearchDisambiguator cuckooDisambiguator = new CuckooSearchDisambiguator(new StopCondition(StopCondition.Condition.SCORERCALLS, iterations), levyLocation, levyScale, nestsNumber, destroyedNests, scorer, false);

        for (int i = 0 ; i < scores.length ; i++)
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
            System.out.println();
            long endTime = System.currentTimeMillis();
            times[i] = (endTime - startTime);
            scores[i] /= ((double) j);
        }
        System.out.println();
        long meanTime = getMean(times);
        System.out.println("Mean Time for " + dict + " : " + meanTime + " ms (" + (meanTime / 1000l) + " s)");
        return scores;
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
