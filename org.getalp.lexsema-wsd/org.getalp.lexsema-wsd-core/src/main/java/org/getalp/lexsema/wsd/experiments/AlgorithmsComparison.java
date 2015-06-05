package org.getalp.lexsema.wsd.experiments;

import java.io.File;

import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.BatAlgorithm;
import org.getalp.lexsema.wsd.method.SimulatedAnnealing2;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.method.TimeStopCondition;
import org.getalp.lexsema.wsd.method.cuckoo.CuckooSearchDisambiguator;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSearch;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.method.genetic.GeneticAlgorithmDisambiguator;
import org.getalp.lexsema.wsd.parameters.bat.BatParameters;
import org.getalp.lexsema.wsd.parameters.bat.BatParametersFactory;
import org.getalp.lexsema.wsd.parameters.bat.BatParametersScorer;
import org.getalp.lexsema.wsd.parameters.cuckoo.CuckooParameters;
import org.getalp.lexsema.wsd.parameters.cuckoo.CuckooParametersFactory;
import org.getalp.lexsema.wsd.parameters.cuckoo.CuckooParametersScorer;
import org.getalp.lexsema.wsd.parameters.genetic.GeneticParameters;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.SemEval2007Task7PerfectConfigurationScorer;

public class AlgorithmsComparison
{
    private static TextLoader dl;
    private static LRLoader lrloader;
    private static ConfigurationScorer configScorer;
    private static TimeStopCondition stopCondition;
    
    public static void main(String[] args)
    {
        dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words-t1s.xml");
        dl.loadNonInstances(false);
        dl.load();
        
        lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));
        for (Document d : dl) lrloader.loadSenses(d);
        
        configScorer = new SemEval2007Task7PerfectConfigurationScorer();

        stopCondition = new TimeStopCondition(1000);
        
        CuckooParameters csaParams = getOptimalCuckooParameters();
        CuckooSearchDisambiguator csa = new CuckooSearchDisambiguator(stopCondition, csaParams.levyScale.currentValue, (int) csaParams.nestsNumber.currentValue, (int) csaParams.destroyedNests.currentValue, configScorer, false);
        
        BatParameters baParams = getOptimalBatParameters();
        BatAlgorithm ba = new BatAlgorithm(stopCondition, (int)baParams.batsNumber.currentValue, baParams.minFrequency.currentValue, baParams.maxFrequency.currentValue, baParams.minLoudness.currentValue, baParams.maxLoudness.currentValue, baParams.alpha.currentValue, baParams.gamma.currentValue, configScorer, false);
        
        //GeneticParameters gaParams;
        //AnnealingParameters saParams;

        System.out.println("Cuckoo Search Parameters : " + csaParams);
        System.out.println("Bat Parameters" + baParams);
        for (Document d : dl)
        {
            Configuration c = csa.disambiguate(d);
            System.out.println("Cuckoo Search Score : " + configScorer.computeScore(d, c));
            
            c = ba.disambiguate(d);
            System.out.println("Bat Score : " + configScorer.computeScore(d, c));
        }
        
        //BatAlgorithm ba;
        //GeneticAlgorithmDisambiguator ga;
        //SimulatedAnnealing2 sa;
        
    }
    
    private static CuckooParameters getOptimalCuckooParameters()
    {
        CuckooParametersScorer csaParamsscorer = new CuckooParametersScorer(configScorer, dl, 5, stopCondition); 
        CuckooParametersFactory configFactory = new CuckooParametersFactory();
        CuckooSearch cuckoo = new CuckooSearch(10000, 1, 1, 0, csaParamsscorer, configFactory, false);
        return (CuckooParameters) cuckoo.run();
    }
    
    private static BatParameters getOptimalBatParameters()
    {
        BatParametersScorer csaParamsscorer = new BatParametersScorer(configScorer, dl, 5, stopCondition); 
        BatParametersFactory configFactory = new BatParametersFactory();
        CuckooSearch cuckoo = new CuckooSearch(10000, 1, 1, 0, csaParamsscorer, configFactory, false);
        return (BatParameters) cuckoo.run();
    }
}
