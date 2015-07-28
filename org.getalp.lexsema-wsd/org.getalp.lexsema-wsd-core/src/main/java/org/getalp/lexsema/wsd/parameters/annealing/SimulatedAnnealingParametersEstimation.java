package org.getalp.lexsema.wsd.parameters.annealing;

import java.io.File;
import java.io.PrintWriter;

import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.parameters.method.CuckooSearchParameterEstimator;
import org.getalp.lexsema.wsd.parameters.method.Parameters;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.SemEval2007Task7PerfectConfigurationScorer;

public class SimulatedAnnealingParametersEstimation
{
    public static void main(String[] args) throws Exception
    {
        String condition = "sc";
        long value = 2000;
        int iterations = 1000;
        double levyLocation = 1;
        double levyScale = 1;

        if (args.length >= 1) condition = args[0];
        if (args.length >= 2) value = Long.valueOf(args[1]);
        if (args.length >= 3) iterations = Integer.valueOf(args[2]);
        if (args.length >= 4) levyLocation = Double.valueOf(args[3]);
        if (args.length >= 5) levyScale = Double.valueOf(args[4]);
        
        System.out.println("Parameters value : " +
                           "<condition = " + condition + " (ms/it/sc)> " +
                           "<condition value = " + value + "> " +
                           "<iterations = " + iterations + "> " +
                           "<levy location = " + levyLocation + "> " +
                           "<levy scale = " + levyScale + "> ");

        StopCondition stopCondition;
        if (condition.equals("ms")) stopCondition = new StopCondition(StopCondition.Condition.MILLISECONDS, value);
        else if (condition.equals("it")) stopCondition = new StopCondition(StopCondition.Condition.ITERATIONS, value);
        else if (condition.equals("sc")) stopCondition = new StopCondition(StopCondition.Condition.SCORERCALLS, value);
        else stopCondition = new StopCondition(StopCondition.Condition.MILLISECONDS, value);
        
        long startTime = System.currentTimeMillis();

        CorpusLoader dl = new Semeval2007CorpusLoader("../data/senseval2007_task7/test/training.xml");
        dl.loadNonInstances(false);
        dl.load();
        
        LRLoader lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));
        for (Document d : dl) lrloader.loadSenses(d);
        
        ConfigurationScorer configScorer = new SemEval2007Task7PerfectConfigurationScorer();
        
        AnnealingParametersScorer scorer = new AnnealingParametersScorer(configScorer, dl, 100, stopCondition); 
        
        AnnealingParametersFactory configFactory = new AnnealingParametersFactory();
        
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(iterations, levyLocation, levyScale, scorer, configFactory, true);
        
        Parameters params = cuckoo.run();
        
        PrintWriter writer = new PrintWriter("../annealing_parameters.txt");
        writer.println(params.toString());
        writer.close();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Cuckoo Search Parameters Algorithm is : ");
        System.out.println((endTime - startTime) + " ms.");
        System.out.println(((endTime - startTime) / 1000l) + " s.");
        System.out.println(((endTime - startTime) / 60000l) + " m.");
    }
}
