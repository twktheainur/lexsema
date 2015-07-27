package org.getalp.lexsema.wsd.method.aca.environment.graph;


public abstract class AbstractNode implements Node {
    private final int position;
    private final String id;
    private double energy;

    protected AbstractNode(int position, String id, double energy) {
        this.position = position;
        this.id = id;
        this.energy = energy;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(double amount){
        energy = amount;
    }
}
