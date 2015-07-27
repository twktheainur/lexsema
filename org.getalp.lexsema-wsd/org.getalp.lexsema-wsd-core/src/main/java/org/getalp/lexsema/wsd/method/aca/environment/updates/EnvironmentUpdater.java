package org.getalp.lexsema.wsd.method.aca.environment.updates;

import org.getalp.lexsema.wsd.method.aca.environment.Environment;
import org.getalp.lexsema.wsd.method.aca.environment.graph.Node;

/**
 * General update interface for the environment, is used to trigger environment specific updates
 */
public interface EnvironmentUpdater {
    void update(Environment environment);

}
