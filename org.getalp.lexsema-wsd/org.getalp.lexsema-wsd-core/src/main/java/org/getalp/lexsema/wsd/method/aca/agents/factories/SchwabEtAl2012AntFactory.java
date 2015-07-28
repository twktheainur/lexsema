package org.getalp.lexsema.wsd.method.aca.agents.factories;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.wsd.method.aca.agents.Ant;
import org.getalp.lexsema.wsd.method.aca.agents.AntImpl;


public class SchwabEtAl2012AntFactory implements AntFactory{
    @Override
    public Ant buildAnt(double life, double maximumEnergy, double energyCarried, int position, SemanticSignature signature) {
        return new AntImpl(life,maximumEnergy,energyCarried,position,signature);
    }
}
