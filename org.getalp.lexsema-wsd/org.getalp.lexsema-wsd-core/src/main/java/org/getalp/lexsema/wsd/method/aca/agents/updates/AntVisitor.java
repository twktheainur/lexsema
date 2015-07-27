package org.getalp.lexsema.wsd.method.aca.agents.updates;

import org.getalp.lexsema.wsd.method.aca.environment.graph.EnvironmentNode;
import org.getalp.lexsema.wsd.method.aca.environment.graph.NestNode;

public interface AntVisitor {
    void visit(EnvironmentNode node);
    void visit(NestNode node);
}
