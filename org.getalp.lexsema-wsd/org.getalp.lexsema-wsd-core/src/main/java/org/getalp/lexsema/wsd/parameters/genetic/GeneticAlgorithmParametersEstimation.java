package org.getalp.lexsema.wsd.parameters.genetic;

import java.io.File;
import java.io.PrintWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.method.TimeStopCondition;
import org.getalp.lexsema.wsd.parameters.method.CuckooSearchParameterEstimator;
import org.getalp.lexsema.wsd.parameters.method.Parameters;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.SemEval2007Task7PerfectConfigurationScorer;

public class GeneticAlgorithmParametersEstimation
{
    public static void main(String[] args) throws Exception
    {
        int iterations = 1000;
        double levyLocation = 1;
        double levyScale = 0.5;
        int msInside = 20;
        
        if (args.length >= 1) iterations = Integer.valueOf(args[0]);
        if (args.length >= 2) levyLocation = Double.valueOf(args[1]);
        if (args.length >= 3) levyScale = Double.valueOf(args[2]);
        if (args.length >= 4) msInside = Integer.valueOf(args[3]);
        
        System.out.println("Parameters value : " +
                           "<iterations = " + iterations + "> " +
                           "<levy location = " + levyLocation + "> " +
                           "<levy scale = " + levyScale + "> " +
                           "<milliseconds inside = " + msInside + "> ");

        long startTime = System.currentTimeMillis();

        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words-t1.xml");
        dl.loadNonInstances(false);
        dl.load();
        
        LRLoader lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));
        for (Document d : dl) lrloader.loadSenses(d);
        
        ConfigurationScorer configScorer = new SemEval2007Task7PerfectConfigurationScorer();
        
        GeneticParametersScorer scorer = new GeneticParametersScorer(configScorer, dl, 100, new TimeStopCondition(msInside)); 
        
        GeneticParametersFactory configFactory = new GeneticParametersFactory();
        
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(iterations, levyLocation, levyScale, scorer, configFactory, true);
        
        Parameters params = cuckoo.run();
        
        PrintWriter writer = new PrintWriter("genetic_parameters.txt");
        writer.println(params.toString());
        writer.close();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Cuckoo Search Parameters Algorithm is : ");
        System.out.println((endTime - startTime) + " ms.");
        System.out.println(((endTime - startTime) / 1000l) + " s.");
        System.out.println(((endTime - startTime) / 60000l) + " m.");
    }
}
