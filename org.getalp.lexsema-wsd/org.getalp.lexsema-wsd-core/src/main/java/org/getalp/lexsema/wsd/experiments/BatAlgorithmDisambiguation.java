package org.getalp.lexsema.wsd.experiments;

import java.io.File;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.IndexedOverlapSimilarity;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.BatAlgorithm;
import org.getalp.lexsema.wsd.method.Disambiguator;

public class BatAlgorithmDisambiguation
{
    public static void main(String[] args)
    {    
        int iterationsNumber = 1000;
        int batsNumber = 20;
        double minFrequency = 0;
        double maxFrequency = 20;
        double minLoudness = 0;
        double maxLoudness = 10;
        double minRate = 0;
        double maxRate = 1;
        double alpha = 0.95;
        double gamma = 0.9;
        
        if (args.length >= 1) iterationsNumber = Integer.valueOf(args[0]);
        if (args.length >= 2) batsNumber = Integer.valueOf(args[1]);
        if (args.length >= 3) minFrequency = Double.valueOf(args[2]);
        if (args.length >= 4) maxFrequency = Double.valueOf(args[3]);
        if (args.length >= 5) minLoudness = Double.valueOf(args[4]);
        if (args.length >= 6) maxLoudness = Double.valueOf(args[5]);
        if (args.length >= 7) minRate = Double.valueOf(args[6]);
        if (args.length >= 8) maxRate = Double.valueOf(args[7]);
        if (args.length >= 9) alpha = Double.valueOf(args[8]);
        if (args.length >= 10) gamma = Double.valueOf(args[9]);
        
        System.out.print("Parameters value : " + 
                         "<iterations = " + iterationsNumber + "> " +
                         "<bats number = " + batsNumber + "> " +
                         "<min frequency = " + minFrequency + "> " +
                         "<max frequency = " + maxFrequency + "> " +
                         "<min loudness = " + minLoudness + "> " +
                         "<max loudness = " + maxLoudness + "> " +
                         "<min rate = " + minRate + "> " +
                         "<max rate = " + maxRate + "> " +
                         "<alpha = " + alpha + "> " +
                         "<gamma = " + gamma + "> "
                         );
        
        long startTime = System.currentTimeMillis();

        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml")
                .loadNonInstances(true);

        LRLoader lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));

        SimilarityMeasure sim = new IndexedOverlapSimilarity();

        Disambiguator batDisambiguator = new BatAlgorithm(iterationsNumber, batsNumber, minFrequency, 
                                                          maxFrequency, minLoudness, maxLoudness,
                                                          minRate, maxRate, alpha, gamma, sim);

        System.out.println("Loading texts...");
        dl.load();

        for (Document d : dl)
        {    
            System.out.println("Starting document " + d.getId());
            
            System.out.println("Loading senses...");
            lrloader.loadSenses(d);

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
