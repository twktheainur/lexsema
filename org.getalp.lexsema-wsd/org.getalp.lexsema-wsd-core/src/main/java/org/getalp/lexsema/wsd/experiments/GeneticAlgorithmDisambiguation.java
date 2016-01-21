package org.getalp.lexsema.wsd.experiments;

import java.io.*;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.method.genetic.GeneticAlgorithmDisambiguator;
import org.getalp.lexsema.wsd.score.*;

public class GeneticAlgorithmDisambiguation
{
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException
    {
        int iterations = 2000;
        int population = 20;
        double crossoverRate = 0.7;
        double mutationRate = 0.9;
        
        if (args.length >= 1) iterations = Integer.valueOf(args[0]);
        if (args.length >= 2) population = Integer.valueOf(args[1]);
        if (args.length >= 3) crossoverRate = Double.valueOf(args[2]);
        if (args.length >= 4) mutationRate = Double.valueOf(args[3]);
        
        System.out.println("Parameters value : " +
                           "<scorer calls = " + iterations + "> " +
                           "<population = " + population + "> " +
                           "<crossover rate = " + crossoverRate + "> " +
                           "<mutation rate = " + mutationRate + "> ");
        
        long startTime = System.currentTimeMillis();

        CorpusLoader dl = new Semeval2007CorpusLoader(new FileInputStream("../data/senseval2007_task7/test/eng-coarse-all-words.xml"));

        LRLoader lrloader = new DictionaryLRLoader(new FileInputStream("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));
        
        ConfigurationScorer scorer = new SemEval2007Task7PerfectConfigurationScorer();
        //ConfigurationScorer scorer = new ACSimilarityConfigurationScorer(new ACExtendedLeskSimilarity());
        //ConfigurationScorer scorer = new ACSimilarityConfigurationScorer(new IndexedOverlapSimilarity());
        //ConfigurationScorer scorer = new TverskyConfigurationScorer(new ACExtendedLeskSimilarity(), Runtime.getRuntime().availableProcessors());
        //ConfigurationScorer scorer = new ConfigurationScorerWithCache(new ACExtendedLeskSimilarity());
        //ConfigurationScorer scorer = new TestScorer(new TverskyConfigurationScorer(new IndexedOverlapSimilarity(), Runtime.getRuntime().availableProcessors()));
        
        GeneticAlgorithmDisambiguator geneticDisambiguator = new GeneticAlgorithmDisambiguator(new StopCondition(StopCondition.Condition.SCORERCALLS, iterations), population, crossoverRate, mutationRate, scorer);

        System.out.println("Loading texts...");
        dl.load();

        for (Document d : dl)
        {
            System.out.println("Starting document " + d.getId());
            
            System.out.println("Loading senses...");
            lrloader.loadSenses(d);

            geneticDisambiguator.plotWriter = new PrintWriter("../genetic_plot_" + d.getId() + ".dat");
            System.out.println("Disambiguating...");
            Configuration c = geneticDisambiguator.disambiguate(d);

            System.out.println("Writing results...");
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            sw.write(d, c.getAssignments());
            
            System.out.println("Done!");
        }
        
        geneticDisambiguator.release();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Genetic Algorithm is : ");
        System.out.println((endTime - startTime) + " ms.");
        System.out.println(((endTime - startTime) / 1000l) + " s.");
        System.out.println(((endTime - startTime) / 60000l) + " m.");
    }
}
