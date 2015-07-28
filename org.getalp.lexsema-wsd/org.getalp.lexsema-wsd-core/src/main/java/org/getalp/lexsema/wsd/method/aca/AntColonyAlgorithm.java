package org.getalp.lexsema.wsd.method.aca;

import cern.jet.random.engine.MersenneTwister;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.ConfidenceConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.org.getalp.lexsema.wsd.evaluation.Evaluation;
import org.getalp.lexsema.wsd.configuration.org.getalp.lexsema.wsd.evaluation.GoldStandard;
import org.getalp.lexsema.wsd.configuration.org.getalp.lexsema.wsd.evaluation.Semeval2007GoldStandard;
import org.getalp.lexsema.wsd.configuration.org.getalp.lexsema.wsd.evaluation.StandardEvaluation;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.aca.agents.factories.AntFactory;
import org.getalp.lexsema.wsd.method.aca.agents.factories.SchwabEtAl2012AntFactory;
import org.getalp.lexsema.wsd.method.aca.agents.updates.AntUpdater;
import org.getalp.lexsema.wsd.method.aca.agents.updates.SchwabEtAl2012AntUpdater;
import org.getalp.lexsema.wsd.method.aca.environment.factories.DocumentEnvironmentFactory;
import org.getalp.lexsema.wsd.method.aca.environment.Environment;
import org.getalp.lexsema.wsd.method.aca.environment.factories.EnvironmentFactory;
import org.getalp.lexsema.wsd.method.aca.environment.factories.TextEnvironmentFactory;
import org.getalp.lexsema.wsd.method.aca.environment.solution.EnergySolutionGenerator;
import org.getalp.lexsema.wsd.method.aca.environment.solution.SolutionGenerator;
import org.getalp.lexsema.wsd.method.aca.environment.updates.EnvironmentUpdater;
import org.getalp.lexsema.wsd.method.aca.environment.updates.SchwabEtAl2012EnvironmentUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;


public class AntColonyAlgorithm implements Disambiguator {

    private static final Logger logger = LoggerFactory.getLogger(AntColonyAlgorithm.class);

    private final int maxIterations;
    private final double initialEnergy;
    private final double initialPheromone;
    private final int vectorSize;

    private final EnvironmentUpdater environmentUpdater;

    private final SolutionGenerator solutionGenerator;

    private final GoldStandard goldStandard = new Semeval2007GoldStandard();
    private final Evaluation evaluation = new StandardEvaluation();


    public AntColonyAlgorithm(SimilarityMeasure similarityMeasure, int maxIterations, double initialEnergy, double initialPheromone, int vectorSize, double pheromoneEvaporation, double maximumEnergy, double antLife, double depositPheromone, double takeEnergy, double componentsDeposited) {
        this.maxIterations = maxIterations;
        this.initialEnergy = initialEnergy;
        this.initialPheromone = initialPheromone;
        this.vectorSize = vectorSize;
        MersenneTwister mersenneTwister = new MersenneTwister();
        AntUpdater antUpdater = new SchwabEtAl2012AntUpdater(mersenneTwister, similarityMeasure, depositPheromone, takeEnergy, componentsDeposited);
        AntFactory antFactory = new SchwabEtAl2012AntFactory();
        environmentUpdater = new SchwabEtAl2012EnvironmentUpdater(initialEnergy,maximumEnergy,antLife, pheromoneEvaporation, antFactory, antUpdater, mersenneTwister);
        solutionGenerator = new EnergySolutionGenerator();
    }

    @Override
    public Configuration disambiguate(Document document) {
        Environment environment = buildEnvironment(document);
        mainLoop(environment,document);
        return  solutionGenerator.generateSolution(environment,document);
    }

    private Environment buildEnvironment(Document document){
        EnvironmentFactory environmentFactory;
        if (document instanceof Text) {
            environmentFactory = new TextEnvironmentFactory((Text) document, initialEnergy, initialPheromone, vectorSize);
        } else {
            environmentFactory = new DocumentEnvironmentFactory(document, 10, 10, 100);
        }
        return environmentFactory.build();
    }

    private void mainLoop(Environment environment, Document document){
        int iteration = 0;
        while (iteration < maxIterations) {
            environmentUpdater.update(environment);
            Configuration configuration = solutionGenerator.generateSolution(environment,document);
            double p = evaluation.evaluate(goldStandard, configuration).getPrecision();
            logger.info(MessageFormat.format("ACA Progress [{0}% | i={1} | bridges = {2} | P={3}]", getPercentage(iteration, maxIterations),iteration,environment.numberOfBridges(),p));
            iteration++;
        }
    }

    private double getPercentage(int iteration, int maxIterations){
        return ((double)iteration/(double)maxIterations)*100d;
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c) {
        return null;
    }

    @Override
    public void release() {

    }
}
