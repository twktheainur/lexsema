package org.getalp.lexsema.wsd.method.aca;

import cern.jet.random.engine.MersenneTwister;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.ConfidenceConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.aca.agents.factories.AntFactory;
import org.getalp.lexsema.wsd.method.aca.agents.factories.SchwabEtAl2012AntFactory;
import org.getalp.lexsema.wsd.method.aca.agents.updates.AntUpdater;
import org.getalp.lexsema.wsd.method.aca.agents.updates.SchwabEtAl2012AntUpdater;
import org.getalp.lexsema.wsd.method.aca.environment.factories.DocumentEnvironmentFactory;
import org.getalp.lexsema.wsd.method.aca.environment.Environment;
import org.getalp.lexsema.wsd.method.aca.environment.factories.EnvironmentFactory;
import org.getalp.lexsema.wsd.method.aca.environment.factories.TextEnvironmentFactory;
import org.getalp.lexsema.wsd.method.aca.environment.updates.EnvironmentUpdater;
import org.getalp.lexsema.wsd.method.aca.environment.updates.SchwabEtAl2012EnvironmentUpdater;


public class AntColonyAlgorithm implements Disambiguator {


    private final int maxIterations;
    private final double initialEnergy;
    private final double initialPheromone;
    private final int vectorSize;

    private final EnvironmentUpdater environmentUpdater;


    public AntColonyAlgorithm(SimilarityMeasure similarityMeasure, int maxIterations, double initialEnergy, double initialPheromone, int vectorSize, double pheromoneEvaporation, double maximumEnergy, double antLife, double depositPheromone, double takeEnergy, double componentsDeposited) {
        this.maxIterations = maxIterations;
        this.initialEnergy = initialEnergy;
        this.initialPheromone = initialPheromone;
        this.vectorSize = vectorSize;
        MersenneTwister mersenneTwister = new MersenneTwister();
        AntUpdater antUpdater = new SchwabEtAl2012AntUpdater(mersenneTwister, similarityMeasure, depositPheromone, takeEnergy, componentsDeposited);
        AntFactory antFactory = new SchwabEtAl2012AntFactory();
        environmentUpdater = new SchwabEtAl2012EnvironmentUpdater(initialEnergy,maximumEnergy,antLife, pheromoneEvaporation, antFactory, antUpdater, mersenneTwister);
    }

    @Override
    public Configuration disambiguate(Document document) {
        Environment environment = buildEnvironment(document);
        mainLoop(environment);
        return new ConfidenceConfiguration(document);
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

    private void mainLoop(Environment environment){
        int iteration = 0;
        while (iteration < maxIterations) {
            environmentUpdater.update(environment);
            iteration++;
        }
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c) {
        return null;
    }

    @Override
    public void release() {

    }
}
