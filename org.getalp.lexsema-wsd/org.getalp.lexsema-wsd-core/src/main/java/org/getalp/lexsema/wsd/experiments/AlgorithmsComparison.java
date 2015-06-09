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
import org.getalp.lexsema.wsd.method.StopCondition;
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
    private static LRLoader lrloader;
    private static TextLoader dlEvaluation;
    private static TextLoader dlTraining;
    private static ConfigurationScorer configScorer;
    private static StopCondition stopConditionEvaluation;
    private static StopCondition stopConditionTraining;
    
    public static void main(String[] args)
    {
        lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));

        dlEvaluation = new Semeval2007TextLoader("../data/senseval2007_task7/test/evaluation.xml");
        dlEvaluation.loadNonInstances(false);
        dlEvaluation.load();
        for (Document d : dlEvaluation) lrloader.loadSenses(d);

        dlTraining = new Semeval2007TextLoader("../data/senseval2007_task7/test/training.xml");
        dlTraining.loadNonInstances(false);
        dlTraining.load();
        for (Document d : dlTraining) lrloader.loadSenses(d);

        configScorer = new SemEval2007Task7PerfectConfigurationScorer();

        stopConditionEvaluation = new StopCondition(StopCondition.Condition.MILLISECONDS, 1000);

        stopConditionTraining = new StopCondition(StopCondition.Condition.MILLISECONDS, 50);
        
        CuckooParameters csaParams = getOptimalCuckooParameters();
        CuckooSearchDisambiguator csa = new CuckooSearchDisambiguator(stopConditionEvaluation, csaParams.levyLocation.currentValue, csaParams.levyScale.currentValue, (int) csaParams.nestsNumber.currentValue, (int) csaParams.destroyedNests.currentValue - 1, configScorer, false);
        
        BatParameters baParams = getOptimalBatParameters();
        BatAlgorithmDisambiguator ba = new BatAlgorithmDisambiguator(stopConditionEvaluation, (int)baParams.batsNumber.currentValue, baParams.minFrequency.currentValue, baParams.maxFrequency.currentValue, baParams.minLoudness.currentValue, baParams.maxLoudness.currentValue, baParams.alpha.currentValue, baParams.gamma.currentValue, configScorer, false);

        GeneticParameters gaParams = getOptimalGeneticParameters();
        GeneticAlgorithmDisambiguator ga = new GeneticAlgorithmDisambiguator(stopConditionEvaluation, (int) gaParams.population.currentValue, gaParams.mutationRate.currentValue, gaParams.crossoverRate.currentValue, configScorer);

        AnnealingParameters saParams = getOptimalAnnealingParameters();
        SimulatedAnnealing2 sa = new SimulatedAnnealing2(stopConditionEvaluation, 200, saParams.coolingRate.currentValue, (int) saParams.iterationsNumber.currentValue, configScorer, false);
        
        System.out.println("Cuckoo Search Parameters : " + csaParams);
        System.out.println("Bat Parameters : " + baParams);
        System.out.println("Genetic Parameters : " + gaParams);
        System.out.println("Annealing Parameters : " + saParams);
        for (Document d : dlEvaluation)
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
        CuckooParametersScorer csaParamsscorer = new CuckooParametersScorer(configScorer, dlTraining, 100, stopConditionTraining); 
        CuckooParametersFactory configFactory = new CuckooParametersFactory();
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(1000, 1, 1, csaParamsscorer, configFactory, true);
        return (CuckooParameters) cuckoo.run();
    }
    
    private static BatParameters getOptimalBatParameters()
    {
        BatParametersScorer csaParamsscorer = new BatParametersScorer(configScorer, dlTraining, 100, stopConditionTraining); 
        BatParametersFactory configFactory = new BatParametersFactory();
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(1000, 1, 1, csaParamsscorer, configFactory, true);
        return (BatParameters) cuckoo.run();
    }
    
    private static GeneticParameters getOptimalGeneticParameters()
    {
        GeneticParametersScorer csaParamsscorer = new GeneticParametersScorer(configScorer, dlTraining, 100, stopConditionTraining); 
        GeneticParametersFactory configFactory = new GeneticParametersFactory();
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(1000, 1, 1, csaParamsscorer, configFactory, true);
        return (GeneticParameters) cuckoo.run();
    }
    
    private static AnnealingParameters getOptimalAnnealingParameters()
    {
        AnnealingParametersScorer csaParamsscorer = new AnnealingParametersScorer(configScorer, dlTraining, 100, stopConditionTraining); 
        AnnealingParametersFactory configFactory = new AnnealingParametersFactory();
        CuckooSearchParameterEstimator cuckoo = new CuckooSearchParameterEstimator(1000, 1, 1, csaParamsscorer, configFactory, true);
        return (AnnealingParameters) cuckoo.run();
    }
}
