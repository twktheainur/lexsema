package org.getalp.lexsema.io.uima;

import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.getalp.lexsema.similarity.Text;

public interface TokenConsumer extends AnalysisComponent {
    void process(CAS cas) throws AnalysisEngineProcessException;

    Text getText();
}
