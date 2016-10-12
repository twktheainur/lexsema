package org.getalp.lexsema.io.text;

import de.tudarmstadt.ukp.dkpro.core.io.text.StringReader;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.CasCreationUtils;
import org.getalp.lexsema.io.uima.TokenConsumer;
import org.getalp.lexsema.io.uima.TokenAnnotationConsumer;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

public abstract class AbstractDKPTextProcessor implements TextProcessor {

    private final Logger logger = LoggerFactory.getLogger(AbstractDKPTextProcessor.class);
    private final Language language;

    protected AbstractDKPTextProcessor(Language language) {
        this.language = language;
    }

    private Text processSentence(final ResourceSpecifier readerDesc,
                                     AnalysisEngineDescription... engineDescriptions) throws IOException, org.apache.uima.analysis_engine.AnalysisEngineProcessException, org.apache.uima.collection.CollectionException, ResourceInitializationException {
        ResourceManager resMgr = UIMAFramework.newDefaultResourceManager();

        // Create the components
        final CollectionReader reader = UIMAFramework.produceCollectionReader(readerDesc, resMgr, null);

        // Create AAE
        final AnalysisEngineDescription aaeDesc = AnalysisEngineFactory.createEngineDescription(engineDescriptions);

        // Instantiate AAE
        final AnalysisEngine aae = UIMAFramework.produceAnalysisEngine(aaeDesc, resMgr, null);
        // Create CAS from merged metadata
        final CAS cas = CasCreationUtils.createCas(asList(reader.getMetaData(), aae.getMetaData()));
        reader.typeSystemInit(cas.getTypeSystem());

        try {
            // Process
            TokenConsumer sac = new TokenAnnotationConsumer();
            while (reader.hasNext()) {
                reader.getNext(cas);
                aae.process(cas);
                sac.process(cas);
                cas.reset();
            }
            // Signal end of processing
            aae.collectionProcessComplete();
            return sac.getText();
        } finally {
            // Destroy
            aae.destroy();
        }
    }

    protected abstract AnalysisEngineDescription[] defineAnalysisEngine() throws ResourceInitializationException;

    @SuppressWarnings("all")
    @Override
    public Text process(String sentenceText, String documentId) {
        try {
            CollectionReaderDescription cr = CollectionReaderFactory.createReaderDescription(
                    StringReader.class,
                    StringReader.PARAM_DOCUMENT_ID, documentId,
                    StringReader.PARAM_DOCUMENT_TEXT, sentenceText,
                    StringReader.PARAM_LANGUAGE, language.getISO2Code());
            return processSentence(cr, defineAnalysisEngine());
        } catch (UIMAException | IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }
}
