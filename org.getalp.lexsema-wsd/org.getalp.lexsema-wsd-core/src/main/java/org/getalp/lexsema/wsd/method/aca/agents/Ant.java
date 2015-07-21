package org.getalp.lexsema.wsd.method.aca.agents;


import org.getalp.lexsema.similarity.signatures.SemanticSignature;

import java.lang.ref.WeakReference;

public class Ant {
    private int life;
    private int maximumEnergy;
    private int energyTaken;
    private int position;
    private WeakReference<SemanticSignature> semanticSignature;

    public Ant(int life, int maximumEnergy, int energyTaken, int position) {
        this.life = life;
        this.maximumEnergy = maximumEnergy;
        this.energyTaken = energyTaken;
        this.position = position;
    }

    public int getLife() {
        return life;
    }

    public int getMaximumEnergy() {
        return maximumEnergy;
    }

    public int getEnergyTaken() {
        return energyTaken;
    }

    public int getPosition() {
        return position;
    }

    public WeakReference<SemanticSignature> getSemanticSignature() {
        return semanticSignature;
    }
}
