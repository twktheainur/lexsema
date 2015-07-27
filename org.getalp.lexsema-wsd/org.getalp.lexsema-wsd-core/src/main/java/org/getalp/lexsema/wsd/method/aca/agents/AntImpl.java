package org.getalp.lexsema.wsd.method.aca.agents;


import org.getalp.lexsema.similarity.signatures.SemanticSignature;

import java.lang.ref.WeakReference;

public class AntImpl implements Ant {
    private int life;
    private int maximumEnergy;
    private int energyTaken;
    private int position;
    private WeakReference<SemanticSignature> semanticSignature;

    public AntImpl(int life, int maximumEnergy, int energyTaken, int position) {
        this.life = life;
        this.maximumEnergy = maximumEnergy;
        this.energyTaken = energyTaken;
        this.position = position;
    }

    @Override
    public int getLife() {
        return life;
    }

    @Override
    public int getMaximumEnergy() {
        return maximumEnergy;
    }

    @Override
    public int getEnergyTaken() {
        return energyTaken;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public WeakReference<SemanticSignature> getSemanticSignature() {
        return semanticSignature;
    }
}
