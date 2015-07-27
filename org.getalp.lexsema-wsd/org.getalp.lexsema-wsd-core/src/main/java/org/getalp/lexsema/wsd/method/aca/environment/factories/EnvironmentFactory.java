package org.getalp.lexsema.wsd.method.aca.environment.factories;


import org.getalp.lexsema.wsd.method.aca.environment.Environment;

/**
 * Interface for Environment factories that create the environment for the ant colony algorithm
 */
public interface EnvironmentFactory {
    /**
     * Builds the environment for the Ant Colony Algorithm
     * @return Environment the environment that was built
     */
    Environment build();
}
