package org.getalp.lexsema.acceptali.crosslingual;

import org.getalp.lexsema.similarity.Sense;

public interface CrossLingualSimilarity {
    public double compute(Sense a, Sense b);
}
