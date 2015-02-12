package org.getalp.lexsema.io.uima;

import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.getalp.lexsema.similarity.Sentence;

/**
 * Created by tchechem on 20/01/15.
 */
public interface SentenceLevelConsumer extends AnalysisComponent {
    void process(CAS cas) throws AnalysisEngineProcessException;

    Sentence getSentence();
}
