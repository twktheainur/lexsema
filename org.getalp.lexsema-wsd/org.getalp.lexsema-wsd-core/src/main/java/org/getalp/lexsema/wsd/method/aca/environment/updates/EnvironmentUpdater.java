package org.getalp.lexsema.wsd.method.aca.environment.updates;

import cern.jet.random.engine.MersenneTwister;
import org.getalp.lexsema.wsd.method.aca.agents.Ant;
import org.getalp.lexsema.wsd.method.aca.agents.factories.AntFactory;
import org.getalp.lexsema.wsd.method.aca.environment.Environment;
import org.getalp.lexsema.wsd.method.aca.environment.graph.EnvironmentNode;
import org.getalp.lexsema.wsd.method.aca.environment.graph.NestNode;
import org.getalp.lexsema.wsd.method.aca.environment.graph.Node;

public class EnvironmentUpdater {

    private static final double CENTERING_CONSTANT = .5d;
    private final MersenneTwister mersenneTwister;
    private final AntFactory antFactory;

    private final double initialAntLife;
    private final double maximumEnergy;
    private final double initialEnergy;


    public EnvironmentUpdater(double initialEnergy, double maximumEnergy, double initialAntLife, AntFactory antFactory, MersenneTwister mersenneTwister) {
        this.initialEnergy = initialEnergy;
        this.maximumEnergy = maximumEnergy;
        this.initialAntLife = initialAntLife;
        this.antFactory = antFactory;
        this.mersenneTwister = mersenneTwister;
    }

    @SuppressWarnings("MethodParameterOfConcreteClass")
    public void update(Node node, Environment environment) {
        if(node.isNest()) {
            /**
             * Pseudo randomly deciding whether to create an ant
             */
            double rand = mersenneTwister.raw();
            double prob = Math.atan(node.getEnergy()) / Math.PI + CENTERING_CONSTANT;
            if (rand > prob) {
                Ant ant = antFactory.buildAnt(initialAntLife, maximumEnergy, initialEnergy, node.getPosition(), node.getSemanticSignature());
                environment.addAnt(ant);
            }
        }
    }
}
