package org.getalp.lexsema.wsd.method.aca.agents;


import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.wsd.method.aca.agents.updates.AntVisitor;
import org.getalp.lexsema.wsd.method.aca.environment.graph.Node;

import java.lang.ref.WeakReference;

public class AntImpl implements Ant {
    private final double maximumEnergy;

    private double energyCarried;
    private int position;
    private final int home;
    private double life;
    private boolean returning;

    private final WeakReference<SemanticSignature> semanticSignature;

    public AntImpl(double life, double maximumEnergy, double energyCarried, int position, SemanticSignature signature) {
        this.life = life;
        this.maximumEnergy = maximumEnergy;
        this.energyCarried = energyCarried;
        home = this.position = position;
        semanticSignature = new WeakReference<>(signature);
        returning = false;
    }

    @Override
    public double getLife() {
        return life;
    }

    @Override
    public double getMaximumEnergy() {
        return maximumEnergy;
    }

    @Override
    public double getEnergyCarried() {
        return energyCarried;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public SemanticSignature getSemanticSignature() {
        return semanticSignature.get();
    }

    @Override
    public void moveTo(int position) {
        this.position = position;
    }

    @Override
    public double takeEnergy(double amountTaken, double amountAvailable){
        double actualAmountTaken = amountTaken;
        if(amountAvailable<amountTaken){
            actualAmountTaken = amountAvailable;
        }
        double diff = maximumEnergy - (energyCarried+actualAmountTaken);
      if(diff>0){
          energyCarried+=actualAmountTaken;
          return actualAmountTaken;
      } else{
          energyCarried+=maximumEnergy-energyCarried;
          return maximumEnergy-energyCarried;
      }
    }

    @Override
    public void decrementLives(){
        life--;
    }

    @Override
    public int compareTo(Double o) {
        final Double d = life;
        return d.compareTo(o);
    }

    @Override
    public boolean isReturning() {
        return returning;
    }

    @Override
    public void initiateReturn(){
        returning =true;
    }

    @Override
    public int getHome() {
        return home;
    }
}
