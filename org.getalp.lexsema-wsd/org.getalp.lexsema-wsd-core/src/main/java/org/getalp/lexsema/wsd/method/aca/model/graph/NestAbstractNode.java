package org.getalp.lexsema.wsd.method.aca.model.graph;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;

public class NestAbstractNode extends AbstractNode {

    private final SemanticSignature semanticSignature;

    public NestAbstractNode(int position, String id, double energy, int signatureSize, SemanticSignature semanticSignature) {
        super(position, id, energy);
        this.semanticSignature = semanticSignature;
    }

    public SemanticSignature getSemanticSignature() {
        return semanticSignature;
    }
}
