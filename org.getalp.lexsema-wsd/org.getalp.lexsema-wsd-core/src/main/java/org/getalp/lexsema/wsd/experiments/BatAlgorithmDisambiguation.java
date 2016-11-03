package org.getalp.lexsema.wsd.experiments;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.BatAlgorithmDisambiguator;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.SemEval2007Task7PerfectConfigurationScorer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class BatAlgorithmDisambiguation
{
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException
    {    
        int iterationsNumber = 2000;
        int batsNumber = 70;
        double minFrequency = 50;
        double maxFrequency = 100;
        double minLoudness = 1;
        double maxLoudness = 20;
        double alpha = 0.95;
        double gamma = 0.9;
        
        if (args.length >= 1) iterationsNumber = Integer.valueOf(args[0]);
        if (args.length >= 2) batsNumber = Integer.valueOf(args[1]);
        if (args.length >= 3) minFrequency = Double.valueOf(args[2]);
        if (args.length >= 4) maxFrequency = Double.valueOf(args[3]);
        if (args.length >= 5) minLoudness = Double.valueOf(args[4]);
        if (args.length >= 6) maxLoudness = Double.valueOf(args[5]);
        if (args.length >= 7) alpha = Double.valueOf(args[6]);
        if (args.length >= 8) gamma = Double.valueOf(args[7]);
        
        System.out.println("Parameters value : " + 
                         "<scorer calls = " + iterationsNumber + "> " +
                         "<bats number = " + batsNumber + "> " +
                         "<min frequency = " + minFrequency + "> " +
                         "<max frequency = " + maxFrequency + "> " +
                         "<min loudness = " + minLoudness + "> " +
                         "<max loudness = " + maxLoudness + "> " +
                         "<alpha = " + alpha + "> " +
                         "<gamma = " + gamma + "> "
                         );
        
        long startTime = System.currentTimeMillis();

        CorpusLoader dl = new Semeval2007CorpusLoader(new FileInputStream("../data/senseval2007_task7/test/eng-coarse-all-words.xml"))
                .loadNonInstances(false);

        LRLoader lrloader = new DictionaryLRLoader(new FileInputStream("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));

        ConfigurationScorer scorer = new SemEval2007Task7PerfectConfigurationScorer();

        BatAlgorithmDisambiguator batDisambiguator = new BatAlgorithmDisambiguator(new StopCondition(StopCondition.Condition.SCORERCALLS, iterationsNumber), batsNumber, minFrequency, 
                                                          maxFrequency, minLoudness, maxLoudness,
                                                          alpha, gamma, scorer, true);

        System.out.println("Loading texts...");
        dl.load();

        for (Document d : dl)
        {    
            System.out.println("Starting document " + d.getId());
            
            System.out.println("Loading senses...");
            lrloader.loadSenses(d);

            batDisambiguator.plotWriter = new PrintWriter("../bat_plot_" + d.getId() + ".dat");
            System.out.println("Disambiguating...");
            Configuration c = batDisambiguator.disambiguate(d);
            
            System.out.println("Writing results...");
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            sw.write(d, c.getAssignments());
            
            System.out.println("Done!");
        }
        
        batDisambiguator.release();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Bat Algorithm is : ");
        System.out.println((endTime - startTime) + " ms.");
        System.out.println(((endTime - startTime) / 1000l) + " s.");
        System.out.println(((endTime - startTime) / 60000l) + " m.");
    }
}
