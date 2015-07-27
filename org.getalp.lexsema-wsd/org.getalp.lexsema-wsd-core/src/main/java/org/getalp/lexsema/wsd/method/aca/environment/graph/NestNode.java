package org.getalp.lexsema.wsd.method.aca.environment.graph;

import cern.jet.random.engine.MersenneTwister;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;

import java.util.List;

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
    public void depositSignature(List<SemanticSymbol> semanticSymbols, MersenneTwister mersenneTwister) {
    }

    @Override
    public boolean isNest() {
        return true;
    }
}
