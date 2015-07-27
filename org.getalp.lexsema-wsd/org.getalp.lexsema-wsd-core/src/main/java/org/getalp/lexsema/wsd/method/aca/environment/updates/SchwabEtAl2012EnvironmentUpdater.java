package org.getalp.lexsema.wsd.method.aca.environment.updates;

import cern.jet.random.engine.MersenneTwister;
import org.getalp.lexsema.wsd.method.aca.agents.Ant;
import org.getalp.lexsema.wsd.method.aca.agents.factories.AntFactory;
import org.getalp.lexsema.wsd.method.aca.agents.updates.AntUpdater;
import org.getalp.lexsema.wsd.method.aca.environment.Environment;
import org.getalp.lexsema.wsd.method.aca.environment.graph.Node;

import java.util.List;

public class SchwabEtAl2012EnvironmentUpdater implements EnvironmentUpdater {

    private static final double CENTERING_CONSTANT = .5d;
    private static final double ZERO_EPSILON = 0.000000001d;
    private final MersenneTwister mersenneTwister;
    private final AntFactory antFactory;
    private final AntUpdater antUpdater;

    private final double initialAntLife;
    private final double maximumEnergy;
    private final double initialEnergy;
    private final double pheromoneEvaporationRate;

    private static final NodeScoreFunction ANT_CREATION_PROBABILITY =
            node -> Math.atan(node.getEnergy()) / Math.PI + CENTERING_CONSTANT;


    public SchwabEtAl2012EnvironmentUpdater(double initialEnergy, double maximumEnergy, double initialAntLife, double pheromoneEvaporationRate, AntFactory antFactory, AntUpdater antUpdater, MersenneTwister mersenneTwister) {
        this.initialEnergy = initialEnergy;
        this.maximumEnergy = maximumEnergy;
        this.initialAntLife = initialAntLife;
        this.antFactory = antFactory;
        this.mersenneTwister = mersenneTwister;
        this.antUpdater = antUpdater;
        this.pheromoneEvaporationRate = pheromoneEvaporationRate;
    }

    @SuppressWarnings("FeatureEnvy")
    private void nodeUpdate(Node node, Environment environment) {
        int position = node.getPosition();
        double energy = node.getEnergy();
        if (node.isNest() && energy >0) {
            randomlyCreateAnt(environment, node, ANT_CREATION_PROBABILITY);
            node.setEnergy(energy-1);
        }
        List<Integer> outgoingPaths = environment.getOutgoingNodes(position);
        outgoingPaths.parallelStream().forEach(target-> pathUpdate(environment, position,target));
    }

    @SuppressWarnings("FeatureEnvy")
    private void pathUpdate(Environment environment, int start, int end){
        double pheromone = environment.getPheromone(start,end);
        pheromone *= pheromone*(1-pheromoneEvaporationRate);
        if(Math.abs(0-pheromone)< ZERO_EPSILON){
            pheromone = 0;
            if(environment.isBridge(start,end)){
                pheromone=-1;
            }
        }
        environment.setPheromone(start,end,pheromone);
    }

    private void randomlyCreateAnt(Environment environment, Node node, NodeScoreFunction scoreFunction) {
        /**
         * Pseudo randomly deciding whether to create an ant
         */
        double rand = mersenneTwister.raw();
        if (rand > scoreFunction.score(node)) {
            Ant ant = antFactory.buildAnt(initialAntLife, maximumEnergy, initialEnergy,
                    node.getPosition(), node.getSemanticSignature());
            environment.addAnt(ant);
        }
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public void update(Environment environment) {
        environment.ants().parallelStream().forEach(ant -> antUpdater.update(ant,environment));
        environment.nodes().parallelStream().forEach(node -> nodeUpdate(node, environment));
        environment.removeDeadAnts();
        environment.cleanupBridges();
    }

}
