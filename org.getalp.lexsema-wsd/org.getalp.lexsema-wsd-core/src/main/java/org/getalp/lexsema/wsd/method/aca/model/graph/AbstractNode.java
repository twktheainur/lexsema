package org.getalp.lexsema.wsd.method.aca.model.graph;

public abstract class AbstractNode {
    private final int position;
    private final String id;
    private final double energy;

    public AbstractNode(int position, String id, double energy) {
        this.position = position;
        this.id = id;
        this.energy = energy;
    }

    public int getPosition() {
        return position;
    }

    public String getId() {
        return id;
    }

    public double getEnergy() {
        return energy;
    }
}
