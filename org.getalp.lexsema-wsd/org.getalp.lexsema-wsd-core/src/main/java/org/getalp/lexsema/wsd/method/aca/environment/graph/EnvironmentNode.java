package org.getalp.lexsema.wsd.method.aca.environment.graph;


import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.wsd.method.aca.agents.updates.AntVisitor;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentNode extends AbstractNode {

    List<SemanticSymbol> signatureVector;

    public EnvironmentNode(int position, String id, double energy, int signatureSize) {
        super(position, id, energy);
        signatureVector = new ArrayList<>(signatureSize);
        for(int i=0;i<signatureSize;i++){
            signatureVector.add(null);
        }
    }

    @Override
    public SemanticSignature getSemanticSignature(){
        SemanticSignature semanticSignature = new SemanticSignatureImpl();
        semanticSignature.addSymbols(signatureVector);
        return semanticSignature;
    }

    @Override
    public void visit(AntVisitor visitor) {
        visitor.visit(this);
    }
}
