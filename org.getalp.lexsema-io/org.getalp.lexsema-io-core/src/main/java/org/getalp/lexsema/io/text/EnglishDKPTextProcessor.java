package org.getalp.lexsema.io.text;


import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.getalp.lexsema.util.Language;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class EnglishDKPTextProcessor extends AbstractDKPTextProcessor {
    public EnglishDKPTextProcessor() {
        super(Language.ENGLISH);
    }

    @Override
    protected AnalysisEngineDescription[] defineAnalysisEngine() throws ResourceInitializationException {
        AnalysisEngineDescription[] descriptors = new AnalysisEngineDescription[3];
        descriptors[0] = createEngineDescription(OpenNlpSegmenter.class);
        descriptors[1] = createEngineDescription(StanfordPosTagger.class);
        descriptors[2] = createEngineDescription(StanfordLemmatizer.class);
        return descriptors;
    }
}
