package org.getalp.lexsema.wsd.method.aca.environment.graph;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.wsd.method.aca.agents.updates.AntVisitor;

public class NestNode extends AbstractNode {

    private final SemanticSignature semanticSignature;

    public NestNode(int position, String id, double energy, SemanticSignature semanticSignature) {
        super(position, id, energy);
        this.semanticSignature = semanticSignature;
    }

    @Override
    public SemanticSignature getSemanticSignature() {
        return semanticSignature;
    }

    @Override
    public void visit(AntVisitor visitor) {
        visitor.visit(this);
    }
}
