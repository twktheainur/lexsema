package org.getalp.lexsema.wsd.method.aca.environment.updates;

import org.getalp.lexsema.wsd.method.aca.environment.graph.Node;

/**
 * Created by tchechem on 25/07/15.
 */
@FunctionalInterface
public interface NodeScoreFunction {
    double score(Node node);
}
