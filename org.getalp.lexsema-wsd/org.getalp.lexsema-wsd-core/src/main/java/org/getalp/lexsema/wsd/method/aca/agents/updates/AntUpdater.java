package org.getalp.lexsema.wsd.method.aca.agents.updates;

import cern.jet.random.engine.MersenneTwister;
import org.getalp.lexsema.wsd.method.aca.agents.Ant;
import org.getalp.lexsema.wsd.method.aca.environment.Environment;

public class AntUpdater {

    private final MersenneTwister mersenneTwister;

    public AntUpdater(MersenneTwister mersenneTwister) {
        this.mersenneTwister = mersenneTwister;
    }

    public void update(Ant ant, Environment environment) {
        environment.visit(ant,1);
    }
}
