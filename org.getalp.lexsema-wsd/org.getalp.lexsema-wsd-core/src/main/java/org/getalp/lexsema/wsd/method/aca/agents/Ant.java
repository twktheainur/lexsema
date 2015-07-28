package org.getalp.lexsema.wsd.method.aca.agents;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;



public interface Ant extends Comparable<Double>{
    double getLife();

    double getMaximumEnergy();

    double getEnergyCarried();

    int getPosition();

    int getHome();

    SemanticSignature getSemanticSignature();

    void moveTo(int position);

    double takeEnergy(double amountTaken, double amountAvailable);

    void decrementLives();

    boolean isReturning();

    void initiateReturn();

    boolean isNewborn();

}
