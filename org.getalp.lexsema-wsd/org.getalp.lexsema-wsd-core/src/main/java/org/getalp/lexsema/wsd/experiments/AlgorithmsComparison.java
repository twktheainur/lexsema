package org.getalp.lexsema.wsd.experiments;

import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Scanner;

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
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.SemEval2007Task7PerfectConfigurationScorer;

public class AlgorithmsComparison
{
    private static StopCondition stopConditionEvaluation;
    private static LRLoader lrloader;
    private static TextLoader dlEvaluation;
    private static ConfigurationScorer configScorer;
    
    public static void main(String[] args) throws Exception
    {
        String condition = "sc";
        long value = 2000;
        if (args.length >= 1) condition = args[0];
        if (args.length >= 2) value = Long.valueOf(args[1]);
        
        System.out.println("Parameters value : " +
                           "<condition = " + condition + " (ms/it/sc)> " +
                           "<condition value = " + value + "> ");
        
        if (condition.equals("ms")) stopConditionEvaluation = new StopCondition(StopCondition.Condition.MILLISECONDS, value);
        else if (condition.equals("it")) stopConditionEvaluation = new StopCondition(StopCondition.Condition.ITERATIONS, value);
        else if (condition.equals("sc")) stopConditionEvaluation = new StopCondition(StopCondition.Condition.SCORERCALLS, value);
        else stopConditionEvaluation = new StopCondition(StopCondition.Condition.MILLISECONDS, value);
        
        lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));

        dlEvaluation = new Semeval2007TextLoader("../data/senseval2007_task7/test/evaluation.xml");
        dlEvaluation.loadNonInstances(false);
        dlEvaluation.load();
        for (Document d : dlEvaluation) lrloader.loadSenses(d);

        configScorer = new SemEval2007Task7PerfectConfigurationScorer();

        Scanner reader = new Scanner(new File("../parameters.txt"));
        reader.useLocale(Locale.ENGLISH);
        
        double csaLevyLocation = reader.nextDouble();
        double csaLevyScale = reader.nextDouble();
        int csaNestsNumber = reader.nextInt();
        int csaDestroyedNestsNumber = reader.nextInt();
        CuckooSearchDisambiguator csa = new CuckooSearchDisambiguator(stopConditionEvaluation, csaLevyLocation, csaLevyScale, csaNestsNumber, csaDestroyedNestsNumber, configScorer, false);
        
        int baBatsNumber = reader.nextInt();
        double baMinFrequency = reader.nextDouble();
        double baMaxFrequency = reader.nextDouble();
        double baMinLoudness = reader.nextDouble();
        double baMaxLoudness = reader.nextDouble();
        double baAlpha = reader.nextDouble();
        double baGamma = reader.nextDouble();
        BatAlgorithmDisambiguator ba = new BatAlgorithmDisambiguator(stopConditionEvaluation, baBatsNumber, baMinFrequency, baMaxFrequency, baMinLoudness, baMaxLoudness, baAlpha, baGamma, configScorer, false);

        int gaPopulation = reader.nextInt();
        double gaCrossoverRate = reader.nextDouble();
        double gaMutationRate = reader.nextDouble();
        GeneticAlgorithmDisambiguator ga = new GeneticAlgorithmDisambiguator(stopConditionEvaluation, gaPopulation, gaCrossoverRate, gaMutationRate, configScorer);

        double saCoolingRate = reader.nextDouble();
        int saIterations = reader.nextInt();
        SimulatedAnnealing2 sa = new SimulatedAnnealing2(stopConditionEvaluation, 200, saCoolingRate, saIterations, configScorer, false);
        
        reader.close();
        
        for (Document d : dlEvaluation)
        {
            System.out.println();
            
            csa.scorePlotWriter = open("cuckoo", d.getId());
            Configuration c = csa.disambiguate(d);
            System.out.println("Cuckoo Search Score : " + configScorer.computeScore(d, c));

            ba.plotWriter = open("bat", d.getId());
            c = ba.disambiguate(d);
            System.out.println("Bat Score : " + configScorer.computeScore(d, c));

            ga.plotWriter = open("genetic", d.getId());
            c = ga.disambiguate(d);
            System.out.println("Genetic Score : " + configScorer.computeScore(d, c));

            sa.plotWriter = open("annealing", d.getId());
            c = sa.disambiguate(d);
            System.out.println("Annealing Score : " + configScorer.computeScore(d, c));
        }
    }
    
    private static PrintWriter open(String algoName, String documentName) throws Exception
    {
        return new PrintWriter("../" + algoName + "_plot_" + documentName + ".dat");
    }
}
