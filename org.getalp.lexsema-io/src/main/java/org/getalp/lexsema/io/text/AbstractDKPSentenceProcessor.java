package org.getalp.lexsema.io.text;

import de.tudarmstadt.ukp.dkpro.core.io.text.StringReader;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.CasCreationUtils;
import org.getalp.lexsema.io.uima.SentenceLevelConsumer;
import org.getalp.lexsema.io.uima.TokenAnnotationConsumer;
import org.getalp.lexsema.similarity.Sentence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

public abstract class AbstractDKPSentenceProcessor implements SentenceProcessor{

    private Logger logger = LoggerFactory.getLogger(AbstractDKPSentenceProcessor.class);

    private Sentence processSentence(final ResourceSpecifier readerDesc,
                                     AnalysisEngineDescription... engineDescriptions) throws UIMAException, IOException {
        ResourceManager resMgr = UIMAFramework.newDefaultResourceManager();

        // Create the components
        final CollectionReader reader = UIMAFramework.produceCollectionReader(readerDesc, resMgr, null);

        // Create AAE
        final AnalysisEngineDescription aaeDesc = createEngineDescription(engineDescriptions);

        // Instantiate AAE
        final AnalysisEngine aae = UIMAFramework.produceAnalysisEngine(aaeDesc, resMgr, null);
        // Create CAS from merged metadata
        final CAS cas = CasCreationUtils.createCas(asList(reader.getMetaData(), aae.getMetaData()));
        reader.typeSystemInit(cas.getTypeSystem());

        try {
            // Process
            SentenceLevelConsumer sac = new TokenAnnotationConsumer();
            while (reader.hasNext()) {
                reader.getNext(cas);
                aae.process(cas);
                sac.process(cas);
                cas.reset();
            }
            // Signal end of processing
            aae.collectionProcessComplete();
            return sac.getSentence();
        } finally {
            // Destroy
            aae.destroy();
        }
    }

    protected abstract AnalysisEngineDescription[] defineAnalysisEngine() throws ResourceInitializationException;

    @SuppressWarnings("all")
    @Override
    public Sentence process(String sentenceText, String documentId, String language) {
        try {
            CollectionReaderDescription cr = createReaderDescription(
                    StringReader.class,
                    StringReader.PARAM_DOCUMENT_ID, documentId,
                    StringReader.PARAM_DOCUMENT_TEXT, sentenceText,
                    StringReader.PARAM_LANGUAGE, language);
            return processSentence(cr,defineAnalysisEngine());
        } catch (UIMAException |IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }
}
