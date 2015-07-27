package org.getalp.lexsema.wsd.method.aca.environment.graph;

import cern.jet.random.engine.MersenneTwister;
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

    void depositSignature(List<SemanticSymbol> semanticSymbols, MersenneTwister mersenneTwister);

    boolean isNest();

}
