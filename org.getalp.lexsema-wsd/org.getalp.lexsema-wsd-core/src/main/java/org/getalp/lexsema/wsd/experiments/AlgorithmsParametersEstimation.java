package org.getalp.lexsema.wsd.experiments;

import java.io.File;
import java.io.PrintWriter;

import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.parameters.annealing.AnnealingParameters;
import org.getalp.lexsema.wsd.parameters.annealing.AnnealingParametersFactory;
import org.getalp.lexsema.wsd.parameters.annealing.AnnealingParametersScorer;
import org.getalp.lexsema.wsd.parameters.bat.BatParameters;
import org.getalp.lexsema.wsd.parameters.bat.BatParametersFactory;
import org.getalp.lexsema.wsd.parameters.bat.BatParametersScorer;
import org.getalp.lexsema.wsd.parameters.cuckoo.CuckooParameters;
import org.getalp.lexsema.wsd.parameters.cuckoo.CuckooParametersFactory;
import org.getalp.lexsema.wsd.parameters.cuckoo.CuckooParametersScorer;
import org.getalp.lexsema.wsd.parameters.genetic.GeneticParameters;
import org.getalp.lexsema.wsd.parameters.genetic.GeneticParametersFactory;
import org.getalp.lexsema.wsd.parameters.genetic.GeneticParametersScorer;
import org.getalp.lexsema.wsd.parameters.method.CuckooSearchParameterEstimator;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.SemEval2007Task7PerfectConfigurationScorer;

public class AlgorithmsParametersEstimation
{
    private static StopCondition stopConditionTraining;
    private static LRLoader lrloader;
    private static CorpusLoader dlTraining;
    private static ConfigurationScorer configScorer;
    private static int n = 100;
    private static int cuckooIterations = 1000;
    private static double cuckooLevyLocation = 1;
    private static double cuckooLevyScale = 1;
    
    public static void main(String[] args) throws Exception
    {
        String condition = "sc";
        long value = 200;
        if (args.length >= 1) condition = args[0];
        if (args.length >= 2) value = Long.valueOf(args[1]);
        
        System.out.println("Parameters value : " +
                           "<condition = " + condition + " (ms/it/sc)> " +
                           "<condition value = " + value + "> ");
        
        if (condition.equals("ms")) stopConditionTraining = new StopCondition(StopCondition.Condition.MILLISECONDS, value);
        else if (condition.equals("it")) stopConditionTraining = new StopCondition(StopCondition.Condition.ITERATIONS, value);
        else if (condition.equals("sc")) stopConditionTraining = new StopCondition(StopCondition.Condition.SCORERCALLS, value);
        else stopConditionTraining = new StopCondition(StopCondition.Condition.MILLISECONDS, value);
        
        lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));

        dlTraining = new Semeval2007CorpusLoader("../data/senseval2007_task7/test/eng-coarse-all-words-t1.xml");
        dlTraining.loadNonInstances(false);
        dlTraining.load();
        for (Document d : dlTraining) lrloader.loadSenses(d);

        configScorer = new SemEval2007Task7PerfectConfigurationScorer();

        CuckooParameters csaParams = getOptimalCuckooParameters();
        BatParameters baParams = getOptimalBatParameters();
        GeneticParameters gaParams = getOptimalGeneticParameters();
        AnnealingParameters saParams = getOptimalAnnealingParameters();
        
        System.out.println("Cuckoo Search Parameters : " + csaParams);
        System.out.println("Bat Parameters : " + baParams);
        System.out.println("Genetic Parameters : " + gaParams);
        System.out.println("Annealing Parameters : " + saParams);
        

        PrintWriter writer = new PrintWriter("../parameters.txt");
        writer.println(csaParams.levyLocation.currentValue + " " + csaParams.levyScale.currentValue + " " + (int) csaParams.nestsNumber.currentValue + " " + (int) (csaParams.destroyedNests.currentValue - 1));
        writer.println((int) baParams.batsNumber.currentValue + " " + baParams.minFrequency.currentValue + " " + baParams.maxFrequency.currentValue + " " + baParams.minLoudness.currentValue + " " + (baParams.maxLoudness.currentValue + 1) + " " + baParams.alpha.currentValue + " " + baParams.gamma.currentValue);
        writer.println((int) gaParams.population.currentValue + " " + gaParams.crossoverRate.currentValue + " " + gaParams.mutationRate.currentValue);
        writer.println(saParams.coolingRate.currentValue + " " + (int) saParams.iterationsNumber.currentValue);
        writer.close();
        
    }
    
    private static CuckooParameters getOptimalCuckooParameters()
    {
        CuckooParametersScorer csaParamsscorer = new CuckooParametersScorer(configScorer, dlTraining, n, stopConditionTraining); 
        CuckooParametersFactory configFactory = new CuckooParametersFactory();
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(cuckooIterations, cuckooLevyLocation, cuckooLevyScale, csaParamsscorer, configFactory, true);
        return (CuckooParameters) cuckoo.run();
    }
    
    private static BatParameters getOptimalBatParameters()
    {
        BatParametersScorer csaParamsscorer = new BatParametersScorer(configScorer, dlTraining, n, stopConditionTraining); 
        BatParametersFactory configFactory = new BatParametersFactory();
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(cuckooIterations, cuckooLevyLocation, cuckooLevyScale, csaParamsscorer, configFactory, true);
        return (BatParameters) cuckoo.run();
    }
    
    private static GeneticParameters getOptimalGeneticParameters()
    {
        GeneticParametersScorer csaParamsscorer = new GeneticParametersScorer(configScorer, dlTraining, n, stopConditionTraining); 
        GeneticParametersFactory configFactory = new GeneticParametersFactory();
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(cuckooIterations, cuckooLevyLocation, cuckooLevyScale, csaParamsscorer, configFactory, true);
        return (GeneticParameters) cuckoo.run();
    }
    
    private static AnnealingParameters getOptimalAnnealingParameters()
    {
        AnnealingParametersScorer csaParamsscorer = new AnnealingParametersScorer(configScorer, dlTraining, n, stopConditionTraining); 
        AnnealingParametersFactory configFactory = new AnnealingParametersFactory();
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(cuckooIterations, cuckooLevyLocation, cuckooLevyScale, csaParamsscorer, configFactory, true);
        return (AnnealingParameters) cuckoo.run();
    }
}
