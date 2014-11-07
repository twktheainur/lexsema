package org.getalp.lexsema.io;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sense {
    private String id;
    private List<String> signature;
    private List<Double> weights;
    private Map<String, List<String>> relatedSignatures;

    public Sense(String id, List<String> signature, List<Double> weights) {
        this.id = id;
        this.signature = signature;
        this.weights = weights;
        relatedSignatures = new HashMap<>();
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

    public Map<String, List<String>> getRelatedSignatures() {
        return relatedSignatures;
    }

    public void setSignature(List<String> signature) {
        this.signature = signature;
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

        if (id.equals(sense.id)) return true;

        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
