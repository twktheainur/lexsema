package org.getalp.similarity.semantic;


import org.getalp.io.Sense;

import java.util.List;

public interface SimilarityMeasure {
    public double compute(List<String> a, List<String> b);

    public double compute(Sense a, List<String> b);

    public double compute(List<String> a, Sense b);

    public double compute(Sense a, Sense b);
}
