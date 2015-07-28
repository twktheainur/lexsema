package org.getalp.lexsema.wsd.method.aca.agents.updates;

import org.getalp.lexsema.wsd.method.aca.agents.Ant;
import org.getalp.lexsema.wsd.method.aca.environment.Environment;

public interface AntUpdater {
    void update(Ant ant, Environment environment);
}
