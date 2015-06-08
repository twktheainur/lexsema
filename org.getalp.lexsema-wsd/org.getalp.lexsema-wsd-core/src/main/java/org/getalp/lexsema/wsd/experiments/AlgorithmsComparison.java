package org.getalp.lexsema.wsd.experiments;

import java.io.File;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.BatAlgorithmDisambiguator;
import org.getalp.lexsema.wsd.method.CuckooSearchDisambiguator;
import org.getalp.lexsema.wsd.method.SimulatedAnnealing2;
import org.getalp.lexsema.wsd.method.TimeStopCondition;
import org.getalp.lexsema.wsd.method.genetic.GeneticAlgorithmDisambiguator;
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

public class AlgorithmsComparison
{
    private static TextLoader dl;
    private static TextLoader dl2;
    private static LRLoader lrloader;
    private static ConfigurationScorer configScorer;
    private static TimeStopCondition stopCondition;
    private static TimeStopCondition stopCondition2;
    
    public static void main(String[] args)
    {
        dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words-t1.xml");
        dl.loadNonInstances(false);
        dl.load();

        dl2 = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words-t1s.xml");
        dl2.loadNonInstances(false);
        dl2.load();
        
        lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));
        for (Document d : dl) lrloader.loadSenses(d);
        for (Document d : dl2) lrloader.loadSenses(d);
        
        configScorer = new SemEval2007Task7PerfectConfigurationScorer();

        stopCondition = new TimeStopCondition(1000);

        stopCondition2 = new TimeStopCondition(30);
        
        CuckooParameters csaParams = getOptimalCuckooParameters();
        CuckooSearchDisambiguator csa = new CuckooSearchDisambiguator(stopCondition, csaParams.levyLocation.currentValue, csaParams.levyScale.currentValue, (int) csaParams.nestsNumber.currentValue, (int) csaParams.destroyedNests.currentValue - 1, configScorer, false);
        
        BatParameters baParams = getOptimalBatParameters();
        BatAlgorithmDisambiguator ba = new BatAlgorithmDisambiguator(stopCondition, (int)baParams.batsNumber.currentValue, baParams.minFrequency.currentValue, baParams.maxFrequency.currentValue, baParams.minLoudness.currentValue, baParams.maxLoudness.currentValue, baParams.alpha.currentValue, baParams.gamma.currentValue, configScorer, false);

        GeneticParameters gaParams = getOptimalGeneticParameters();
        GeneticAlgorithmDisambiguator ga = new GeneticAlgorithmDisambiguator(stopCondition, (int) gaParams.population.currentValue, gaParams.mutationRate.currentValue, gaParams.crossoverRate.currentValue, configScorer);

        AnnealingParameters saParams = getOptimalAnnealingParameters();
        SimulatedAnnealing2 sa = new SimulatedAnnealing2(stopCondition, 200, saParams.coolingRate.currentValue, (int) saParams.iterationsNumber.currentValue, configScorer, false);
        
        System.out.println("Cuckoo Search Parameters : " + csaParams);
        System.out.println("Bat Parameters" + baParams);
        System.out.println("Genetic Parameters" + gaParams);
        System.out.println("Annealing Parameters" + saParams);
        for (Document d : dl)
        {
            Configuration c = csa.disambiguate(d);
            System.out.println("Cuckoo Search Score : " + configScorer.computeScore(d, c));
            
            c = ba.disambiguate(d);
            System.out.println("Bat Score : " + configScorer.computeScore(d, c));
            
            c = ga.disambiguate(d);
            System.out.println("Genetic Score : " + configScorer.computeScore(d, c));
            
            c = sa.disambiguate(d);
            System.out.println("Annealing Score : " + configScorer.computeScore(d, c));
        }
    }
    
    private static CuckooParameters getOptimalCuckooParameters()
    {
        CuckooParametersScorer csaParamsscorer = new CuckooParametersScorer(configScorer, dl2, 10, stopCondition2); 
        CuckooParametersFactory configFactory = new CuckooParametersFactory();
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(1000, 1, 1, csaParamsscorer, configFactory, true);
        return (CuckooParameters) cuckoo.run();
    }
    
    private static BatParameters getOptimalBatParameters()
    {
        BatParametersScorer csaParamsscorer = new BatParametersScorer(configScorer, dl2, 10, stopCondition2); 
        BatParametersFactory configFactory = new BatParametersFactory();
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(1000, 1, 1, csaParamsscorer, configFactory, true);
        return (BatParameters) cuckoo.run();
    }
    
    private static GeneticParameters getOptimalGeneticParameters()
    {
        GeneticParametersScorer csaParamsscorer = new GeneticParametersScorer(configScorer, dl2, 10, stopCondition2); 
        GeneticParametersFactory configFactory = new GeneticParametersFactory();
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(1000, 1, 1, csaParamsscorer, configFactory, true);
        return (GeneticParameters) cuckoo.run();
    }
    
    private static AnnealingParameters getOptimalAnnealingParameters()
    {
        AnnealingParametersScorer csaParamsscorer = new AnnealingParametersScorer(configScorer, dl2, 10, stopCondition2); 
        AnnealingParametersFactory configFactory = new AnnealingParametersFactory();
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(1000, 1, 1, csaParamsscorer, configFactory, true);
        return (AnnealingParameters) cuckoo.run();
    }
}
