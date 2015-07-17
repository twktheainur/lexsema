package org.getalp.lexsema.wsd.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.lesk.*;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.CuckooSearchDisambiguator;
import org.getalp.lexsema.wsd.method.MultiThreadCuckooSearch;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorerWithCache;
import org.getalp.lexsema.wsd.score.MultiThreadConfigurationScorerWithCache;
import org.getalp.lexsema.wsd.score.SemEval2007Task7PerfectConfigurationScorer;

public class CuckooSearchDisambiguation
{
    public static void main(String[] args) throws Exception
    {
        int iterations = 10000;
        double levyLocation = 2;
        double levyScale = 0.7;
        int nestsNumber = 1;
        int destroyedNests = 0;
         
        if (args.length >= 1) iterations = Integer.valueOf(args[0]);
        if (args.length >= 2) levyLocation = Double.valueOf(args[1]);
        if (args.length >= 3) levyScale = Double.valueOf(args[2]);
        if (args.length >= 4) nestsNumber = Integer.valueOf(args[3]);
        if (args.length >= 5) destroyedNests = Integer.valueOf(args[4]);
        
        System.out.println("Parameters value : " +
                           "<scorer calls = " + iterations + "> " +
                           "<levy location = " + levyLocation + "> " +
                           "<levy scale = " + levyScale + "> " +
                           "<nests number = " + nestsNumber + "> " +
                           "<destroyed nests = " + destroyedNests + "> ");
        
        long startTime = System.currentTimeMillis();

        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml");

        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict-adapted-all-relations.xml"));
        
        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7"), true);
        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7_stopwords"), true);
        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7_stemming"), true);
        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7_stopwords_stemming"), true);
        
        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7_semcor"), true);
        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7_stopwords_semcor"), true);
        //LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/dict_semeval2007task7_stemming_semcor"), true);
        LRLoader lrloader = new DictionaryLRLoader(new File("../data/lesk_dict/all/dict_semeval2007task7_stopwords_stemming_semcor"), true);
        
        //ConfigurationScorer scorer = new SemEval2007Task7PerfectConfigurationScorer();
        //ConfigurationScorer scorer = new ACSimilarityConfigurationScorer(new ACExtendedLeskSimilarity());
        //ConfigurationScorer scorer = new ACSimilarityConfigurationScorer(new IndexedOverlapSimilarity());
        //ConfigurationScorer scorer = new TverskyConfigurationScorer(new ACExtendedLeskSimilarity(), Runtime.getRuntime().availableProcessors());
        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new AnotherLeskSimilarity());
        //ConfigurationScorer scorer = new MultiThreadConfigurationScorerWithCache(new AnotherLeskSimilarity());
        //ConfigurationScorer scorer = new TestScorer(new TverskyConfigurationScorer(new IndexedOverlapSimilarity(), Runtime.getRuntime().availableProcessors()));
        //ConfigurationScorer scorer = new TverskyConfigurationScorer(new TverskiIndexSimilarityMeasureBuilder().distance(new ScaledLevenstein()).alpha(1d).beta(0.0d).gamma(0.0d).fuzzyMatching(true).build(), Runtime.getRuntime().availableProcessors());
        
        //CuckooSearchDisambiguator cuckooDisambiguator = new CuckooSearchDisambiguator(new StopCondition(StopCondition.Condition.SCORERCALLS, iterations), levyLocation, levyScale, nestsNumber, destroyedNests, scorer, true);
        MultiThreadCuckooSearch cuckooDisambiguator = new MultiThreadCuckooSearch(iterations, levyLocation, levyScale, scorer, true);
        
        System.out.println("Loading texts...");
        dl.load();

        for (Document d : dl)
        {
            System.out.println("Starting document " + d.getId());
            
            System.out.println("Loading senses...");
            lrloader.loadSenses(d);

            cuckooDisambiguator.scorePlotWriter = new PrintWriter("../cuckoo_score_plot_" + d.getId() + ".dat");
            cuckooDisambiguator.perfectScorePlotWriter = new PrintWriter("../cuckoo_perfect_score_plot_" + d.getId() + ".dat");
            cuckooDisambiguator.perfectScorer = new SemEval2007Task7PerfectConfigurationScorer();
            System.out.println("Disambiguating...");
            Configuration c = cuckooDisambiguator.disambiguate(d);

            System.out.println("Writing results...");
            SemevalWriter sw = new SemevalWriter("../" + d.getId() + ".ans");
            sw.write(d, c.getAssignments());
            
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
