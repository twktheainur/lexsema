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
    private final MersenneTwister mersenneTwister;
    private final AntFactory antFactory;
    private final AntUpdater antUpdater;

    private final double initialAntLife;
    private final double maximumEnergy;
    private final double initialEnergy;

    private static final NodeScoreFunction ANT_CREATION_PROBABILITY =
            node -> Math.atan(node.getEnergy()) / Math.PI + CENTERING_CONSTANT;


    public SchwabEtAl2012EnvironmentUpdater(double initialEnergy, double maximumEnergy, double initialAntLife, AntFactory antFactory, AntUpdater antUpdater, MersenneTwister mersenneTwister) {
        this.initialEnergy = initialEnergy;
        this.maximumEnergy = maximumEnergy;
        this.initialAntLife = initialAntLife;
        this.antFactory = antFactory;
        this.mersenneTwister = mersenneTwister;
        this.antUpdater = antUpdater;
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

    private void pathUpdate(Environment environment, int start, int end){
        //if(environment.isPath())
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

    @Override
    public void update(Environment environment) {
        environment.ants().parallelStream().forEach(ant -> antUpdater.update(ant,environment));
        environment.nodes().parallelStream().forEach(node -> nodeUpdate(node, environment));
    }

}
