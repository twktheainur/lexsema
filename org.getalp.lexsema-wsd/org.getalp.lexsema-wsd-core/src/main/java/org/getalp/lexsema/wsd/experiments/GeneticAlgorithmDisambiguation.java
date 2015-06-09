package org.getalp.lexsema.wsd.experiments;

import java.io.File;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.method.genetic.GeneticAlgorithmDisambiguator;
import org.getalp.lexsema.wsd.score.*;

public class GeneticAlgorithmDisambiguation
{
    public static void main(String[] args)
    {
        int iterations = 1000;
        int population = 20;
        double crossoverRate = 0.7;
        double mutationRate = 0.9;
        
        if (args.length >= 1) iterations = Integer.valueOf(args[0]);
        if (args.length >= 2) population = Integer.valueOf(args[1]);
        if (args.length >= 3) crossoverRate = Double.valueOf(args[2]);
        if (args.length >= 4) mutationRate = Double.valueOf(args[3]);
        
        System.out.println("Parameters value : " +
                           "<iterations = " + iterations + "> " +
                           "<population = " + population + "> " +
                           "<crossover rate = " + crossoverRate + "> " +
                           "<mutation rate = " + mutationRate + "> ");
        
        long startTime = System.currentTimeMillis();

        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml");

        LRLoader lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));
        
        ConfigurationScorer scorer = new SemEval2007Task7PerfectConfigurationScorer();
        //ConfigurationScorer scorer = new ACSimilarityConfigurationScorer(new ACExtendedLeskSimilarity());
        //ConfigurationScorer scorer = new ACSimilarityConfigurationScorer(new IndexedOverlapSimilarity());
        //ConfigurationScorer scorer = new TverskyConfigurationScorer(new ACExtendedLeskSimilarity(), Runtime.getRuntime().availableProcessors());
        //ConfigurationScorer scorer = new ConfigurationScorerWithCache(new ACExtendedLeskSimilarity());
        //ConfigurationScorer scorer = new TestScorer(new TverskyConfigurationScorer(new IndexedOverlapSimilarity(), Runtime.getRuntime().availableProcessors()));
        
        Disambiguator geneticDisambiguator = new GeneticAlgorithmDisambiguator(new StopCondition(StopCondition.Condition.ITERATIONS, iterations), population, crossoverRate, mutationRate, scorer);

        System.out.println("Loading texts...");
        dl.load();

        for (Document d : dl)
        {
            System.out.println("Starting document " + d.getId());
            
            System.out.println("Loading senses...");
            lrloader.loadSenses(d);

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
