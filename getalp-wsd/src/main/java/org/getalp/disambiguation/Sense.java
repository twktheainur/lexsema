package org.getalp.disambiguation;

import java.util.List;

public class Sense {
    private String id;
    private List<String> signature;
    private List<Double> weights;

    public Sense(String id, List<String> signature, List<Double> weights) {
        this.id = id;
        this.signature = signature;
        this.weights = weights;
    }

    public String getId() {
        return id;
    }

    public List<String> getSignature() {
        return signature;
    }

    public List<Double> getWeights() {
        return weights;
    }

    @Override
    public String toString() {
        return "Sense{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sense)) return false;

        Sense sense = (Sense) o;

        if (!id.equals(sense.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
