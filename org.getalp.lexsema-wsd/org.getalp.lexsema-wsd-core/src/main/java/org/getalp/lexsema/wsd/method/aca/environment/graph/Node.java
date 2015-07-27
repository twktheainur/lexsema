package org.getalp.lexsema.wsd.method.aca.environment.graph;

import org.getalp.lexsema.wsd.method.aca.agents.AntVisitor;

/**
 * Information pertaining to a generic note in the environment of the ACA
 */
public interface Node {
    int getPosition();

    String getId();

    double getEnergy();

    void visit(AntVisitor visitor);
}
