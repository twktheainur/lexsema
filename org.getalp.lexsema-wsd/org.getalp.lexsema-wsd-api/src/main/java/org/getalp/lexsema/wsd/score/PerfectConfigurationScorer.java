package org.getalp.lexsema.wsd.score;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;

import java.util.List;

public interface PerfectConfigurationScorer extends ConfigurationScorer
{
    double computeTotalScore(List<Document> documents, List<Configuration> configurations);
}
