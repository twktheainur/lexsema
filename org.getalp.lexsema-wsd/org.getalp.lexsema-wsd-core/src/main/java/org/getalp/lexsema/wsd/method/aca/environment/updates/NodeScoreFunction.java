package org.getalp.lexsema.wsd.method.aca.environment.updates;

import org.getalp.lexsema.wsd.method.aca.environment.graph.Node;

/**
 * Functional interface that calculates a score for a particular node of the environment
 */
@FunctionalInterface
public interface NodeScoreFunction {
    /**
     * Calculates a score for the given node
     * @param node The node for which to calculate the score
     * @return The calculated score
     */
    double score(Node node);
}
