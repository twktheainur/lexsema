package org.getalp.lexsema.io.uima;

import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.getalp.lexsema.similarity.Text;

/**
 * Created by tchechem on 20/01/15.
 */
public interface TokenConsumer extends AnalysisComponent {
    void process(CAS cas) throws AnalysisEngineProcessException;

    Text getText();
}
