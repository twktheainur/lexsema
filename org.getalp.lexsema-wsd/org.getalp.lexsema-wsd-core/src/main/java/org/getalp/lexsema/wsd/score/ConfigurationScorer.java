package org.getalp.lexsema.wsd.score;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;

public interface ConfigurationScorer {
    public double computeScore(Document d, Configuration c);

    public void release();
}
