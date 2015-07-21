package org.getalp.lexsema.wsd.method.aca.agents;

import org.getalp.lexsema.wsd.method.aca.model.graph.AbstractNode;
import org.getalp.lexsema.wsd.method.aca.model.graph.NestAbstractNode;

public interface AntVisitor {
    void visit(AbstractNode node);
    void visit(NestAbstractNode node);
}
