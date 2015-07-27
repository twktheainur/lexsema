package org.getalp.lexsema.wsd.method.aca.environment.graph;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;

import java.util.List;

/**
 * Information pertaining to a generic note in the environment of the ACA
 */
public interface Node {
    int getPosition();

    String getId();

    double getEnergy();
    void setEnergy(double amount);

    SemanticSignature getSemanticSignature();

    public void depositSignature(List<SemanticSymbol> semanticSymbols);

    boolean isNest();

}
