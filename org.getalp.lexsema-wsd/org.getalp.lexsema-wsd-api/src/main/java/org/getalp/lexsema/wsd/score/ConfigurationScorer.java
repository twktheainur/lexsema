package org.getalp.lexsema.wsd.score;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;

public interface ConfigurationScorer {
    double computeScore(Document document, Configuration configuration);

    void release();
}
