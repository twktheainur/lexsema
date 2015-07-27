package org.getalp.lexsema.wsd.method.aca.environment.graph;


import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        semanticSignature.addSymbols(signatureVector.stream().filter(symbol -> symbol!=null).collect(Collectors.toList()));
        return semanticSignature;
    }

    @Override
    public void depositSignature(List<SemanticSymbol> semanticSymbols) {
        int length = signatureVector.size();
        Collections.shuffle(semanticSymbols);
        for(int i=0;i<length;i++){
            signatureVector.set(i,semanticSymbols.get(i));
        }
    }

    @Override
    public boolean isNest() {
        return false;
    }



}
