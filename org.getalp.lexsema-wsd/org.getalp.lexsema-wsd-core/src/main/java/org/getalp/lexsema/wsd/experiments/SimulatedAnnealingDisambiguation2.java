package org.getalp.lexsema.wsd.experiments;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.lesk.ACExtendedLeskSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.SimulatedAnnealing2;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorerWithCache;

import java.io.File;
import java.io.PrintWriter;

public class SimulatedAnnealingDisambiguation2
{
    public static void main(String[] args) throws Exception
    {    
        int cycles = 2000;
        double coolingRate = 0.10;
        int iterationsNumber = 4;
        
        if (args.length >= 1) cycles = Integer.valueOf(args[0]);
        if (args.length >= 2) coolingRate = Double.valueOf(args[1]);
        if (args.length >= 3) iterationsNumber = Integer.valueOf(args[2]);
        
        System.out.println("Parameters value : " + 
                         "<scorer calls = " + cycles + "> " +
                         "<cooling rate = " + coolingRate + "> " +
                         "<iterations = " + iterationsNumber + "> " 
                         );
        
        long startTime = System.currentTimeMillis();

        CorpusLoader dl = new Semeval2007CorpusLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml")
                .loadNonInstances(false);

        LRLoader lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));

        //ConfigurationScorer scorer = new SemEval2007Task7PerfectConfigurationScorer();
        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new ACExtendedLeskSimilarity());

        SimulatedAnnealing2 saDisambiguator = new SimulatedAnnealing2(new StopCondition(StopCondition.Condition.SCORERCALLS, cycles), 200, coolingRate, iterationsNumber, scorer, true);

        System.out.println("Loading texts...");
        dl.load();

        for (Document d : dl)
        {    
            System.out.println("Starting document " + d.getId());
            
            System.out.println("Loading senses...");
            lrloader.loadSenses(d);

            saDisambiguator.plotWriter = new PrintWriter("../annealing_plot_" + d.getId() + ".dat");
            System.out.println("Disambiguating...");
            Configuration c = saDisambiguator.disambiguate(d);
            
            System.out.println("Writing results...");
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            sw.write(d, c.getAssignments());
            
            System.out.println("Done!");
        }
        
        saDisambiguator.release();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Simulated Annealing is : ");
        System.out.println((endTime - startTime) + " ms.");
        System.out.println(((endTime - startTime) / 1000l) + " s.");
        System.out.println(((endTime - startTime) / 60000l) + " m.");
    }
}
