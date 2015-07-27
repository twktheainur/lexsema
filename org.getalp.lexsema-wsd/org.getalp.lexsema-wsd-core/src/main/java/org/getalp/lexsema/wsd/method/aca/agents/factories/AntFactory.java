package org.getalp.lexsema.wsd.method.aca.agents.factories;


import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.wsd.method.aca.agents.Ant;

public interface AntFactory {
    Ant buildAnt(double life, double maximumEnergy, double energyCarried, int position, SemanticSignature signature);
}
