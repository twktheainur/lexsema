package org.getalp.lexsema.wsd.experiments;

import com.google.common.math.DoubleMath;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2013Task12CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.lesk.IndexedLeskSimilarity;
import org.getalp.lexsema.similarity.measures.lesk.SimpleLeskSimilarity;
import org.getalp.lexsema.similarity.measures.lesk.VectorizedLeskSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.MultiThreadCuckooSearch;
import org.getalp.lexsema.wsd.score.*;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TestOnSimilarityMeasures
{
	static CorpusLoader semeval2007task7corpus = new Semeval2007CorpusLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml");

	static CorpusLoader semeval20013task12corpus = new Semeval2013Task12CorpusLoader("../data/semeval2013_task12/data/multilingual-all-words.en.xml");

	static PerfectConfigurationScorer semeval2007task7scorer = new SemEval2007Task7PerfectConfigurationScorer();

	static PerfectConfigurationScorer semeval2013task12scorer = new SemEval2013Task12PerfectConfigurationScorer("../data/semeval2013_task12/scorer/scorer2", "../data/semeval2013_task12/keys/gold/wordnet/wordnet.en.key");
	
	static class Input
	{
		public Input(String dict, boolean indexed)
		{
			this(dict, indexed, false, -1);
		}
		public Input(String dict, boolean indexed, boolean vectorized)
		{
			this(dict, indexed, vectorized, -1);
		}
		public Input(String dict, boolean indexed, boolean vectorized, double vectorThreshold)
		{
			this(dict, indexed, vectorized, vectorThreshold, semeval2007task7corpus, semeval2007task7scorer);
		}
		public Input(String dict, boolean indexed, boolean vectorized, double vectorThreshold, CorpusLoader corpus, PerfectConfigurationScorer scorer)
		{
			this.dict = dict;
			if (indexed && vectorized) throw new RuntimeException();
			this.indexed = indexed;
			this.vectorized = vectorized;
			this.vectorThreshold = vectorThreshold;
			this.corpus = corpus;
			this.scorer = scorer;
		}
		public String dict;
		public boolean indexed;
		public boolean vectorized;
		public double vectorThreshold;
		public CorpusLoader corpus; 
		public PerfectConfigurationScorer scorer;
	}
	
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
    	List<Input> dicts_list = new ArrayList<>();
    	//dicts_list.add(new Input("../data/lesk_dict/all/sem13_baseline", true, false, -1, semeval20013task12corpus, semeval2013task12scorer));
    	//dicts_list.add(new Input("../data/lesk_dict/all/sem13_5_250", true, false, -1, semeval20013task12corpus, semeval2013task12scorer));
    	//dicts_list.add(new Input("../data/lesk_dict/all/sem13_7_100", true, false, -1, semeval20013task12corpus, semeval2013task12scorer));

    	//dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/vectorized1", false, true, -0.5));
    	//dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/vectorized1", false, true, 0));
    	//dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/vectorized1", false, true, 0.5));
    	
    	//dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/vectorized2", false, true));
    	
    	//dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/vectorized3_-0.5", false, true));
    	//dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/vectorized3_0", false, true));
    	//dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/vectorized3_0.5", false, true));
    	/*
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended1_1", true, false));
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended1_3", true, false));
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended1_5", true, false));
    	
    	
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended1_10", true, false));
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended1_20", true, false));
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended1_50", true, false));
/*
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended2_10", true, false));
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended2_50", true, false));
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended2_100", true, false));

    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended3_10_-0.5", true, false));
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended3_50_-0.5", true, false));
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended3_100_-0.5", true, false));

    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended3_10_0", true, false));
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended3_50_0", true, false));
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended3_100_0", true, false));

    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended3_10_0.5", true, false));
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended3_50_0.5", true, false));
    	dicts_list.add(new Input("../data/lesk_dict/semeval2007task7/w2v/extended3_100_0.5", true, false));
    	*/
    	//Input[] dicts = dicts_list.toArray(new Input[dicts_list.size()]);
        //compareDicts(dicts);
        
        dicts_list = new ArrayList<>();
        for (int i = 1 ; i <= 15 ; i++) 
        {
            for (int j = 50 ; j <= 300 ; j += 50) 
            {
                dicts_list.add(new Input("../data/lesk_dict/all/fine_def/" + i + "_" + j, true, false, -1, semeval20013task12corpus, semeval2013task12scorer));
            }
        }
        compareDicts(dicts_list.toArray(new Input[dicts_list.size()]));
        
    	/*
        List<String> dicts_list = new ArrayList<>();
        //dicts_list.add("../data/lesk_dict/semeval2007task7/5/250");
        dicts_list.add("../data/lesk_dict/semeval2007task7/clust/5/200"); 
        compareDicts(dicts_list.toArray(new String[dicts_list.size()]));
        */
    }
    
    private static void compareDicts(Input[] dicts) throws Exception
    {
        Result[] res = new Result[dicts.length];
        for (int i = 0 ; i < res.length ; i++)
        {
            res[i] = getScores(dicts[i], 30);
            System.out.println("Test " + i + " (" + dicts[i].dict + ")");
            System.out.println("Mean Scores : " + res[i].meanScore);
            System.out.println("Standard Deviation Scores : " + res[i].standardDeviationScore);
            System.out.println("Mean Times : " + res[i].meanTime);
            System.out.println();
        }
        System.out.println("Recap:");
        for (int i = 0 ; i < res.length ; i++)
        {
            System.out.println("Test " + i + " (" + dicts[i].dict + ")");
            System.out.println("Mean Scores : " + res[i].meanScore);
            System.out.println("Standard Deviation Scores : " + res[i].standardDeviationScore);
            System.out.println("Mean Times : " + res[i].meanTime);
            System.out.println();
        }
        for (int i = 0 ; i < res.length ; i++)
        {
            for (int j = 0 ; j < res.length ; j++)
            {
                System.out.println("MWUTest " + i + " (" + dicts[i].dict + ") / " + j + " (" + dicts[j].dict + ") : " + mannTest.mannWhitneyUTest(res[i].scores, res[j].scores));
            }
        }
    }
    
    private static Result getScores(Input input, int n) throws Exception
    {
        double[] scores = new double[n];
        long[] times = new long[n];
        LRLoader lrloader = new DictionaryLRLoader(new FileInputStream(input.dict), input.indexed, input.vectorized);
        input.corpus.load();
        for (Document d : input.corpus) lrloader.loadSenses(d);

        SimilarityMeasure sim = new SimpleLeskSimilarity();
        if (input.indexed) sim = new IndexedLeskSimilarity();
        if (input.vectorized) sim = new VectorizedLeskSimilarity(input.vectorThreshold);
        
        ConfigurationScorer scorer = new ConfigurationScorerWithCache(sim);
        
        PerfectConfigurationScorer perfectScorer = input.scorer;

        int iterations = 100000;
        double minLevyLocation = 1;
        double maxLevyLocation = 5;
        double minLevyScale = 0.5;
        double maxLevyScale = 1.5;

        MultiThreadCuckooSearch cuckooDisambiguator = new MultiThreadCuckooSearch(iterations, minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, scorer, false);               
        Disambiguator disambiguator = cuckooDisambiguator;
        
        System.out.println("Dictionary " + input.dict);

        List<Document> documents = new ArrayList<>();
        for (Document d : input.corpus) documents.add(d);
        
        for (int i = 0 ; i < n ; i++)
        {
            System.out.print("" + (i+1) + "/" + n + " ");
            System.out.flush();
            List<Configuration> configurations = new ArrayList<>();

            //String resultName = input.dict;
            //resultName = resultName.replaceAll("\\.", "");
            //resultName = resultName.replaceAll("\\/", "");
            //resultName = resultName + "_" + i + ".ans";
            //PrintStream ps = null;
            //try { ps = new PrintStream(resultName); } catch (FileNotFoundException e) { throw new RuntimeException(e); }
            
            long startTime = System.currentTimeMillis();
            for (Document d : input.corpus)
            {
                System.out.print("(" + d.getId() + ") ");
                System.out.flush();
                Configuration c = disambiguator.disambiguate(d);
                configurations.add(c);
                /*
                for (int j = 0 ; j < d.size() ; j++) 
                {
                    if (d.getWord(j).getId() != null && !d.getWord(j).getId().isEmpty()) 
                    {
                        if (c.getAssignment(j) >= 0 && !d.getSenses(j).isEmpty())
                        {
                            ps.printf("%s %s %s", d.getId(), d.getWord(j).getId(), d.getSenses(j).get(c.getAssignment(j)).getId());
                        }
                    }
                }
                */
                
                double tmp_score = perfectScorer.computeScore(d, c);
                System.out.print("[" + new DecimalFormat("##.##").format(tmp_score * 100) + "] ");
                System.out.flush();
            }
            //ps.close();
            long endTime = System.currentTimeMillis();
            times[i] = (endTime - startTime);
            scores[i] = perfectScorer.computeTotalScore(documents, configurations);
            System.out.println("score : " + scores[i] + " ; time : " + times[i]);
        }
        System.out.println();
        disambiguator.release();
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
