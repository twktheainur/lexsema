package org.getalp.lexsema.io.sentences;


import de.tudarmstadt.ukp.dkpro.core.io.text.StringReader;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.util.CasCreationUtils;
import org.getalp.lexsema.io.Sentence;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.segmentation.SpaceSegmenter;
import org.getalp.lexsema.io.uima.TokenAnnotationConsumer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

public class STS2013SentencePairLoader extends SentencePairLoader {

    private String fileName;
    private LRLoader senseLoader;


    public STS2013SentencePairLoader(String fileName, LRLoader loader) {
        this.fileName = fileName;
        senseLoader = loader;
    }

    public Sentence processSentence(final CollectionReaderDescription readerDesc,
                                    AnalysisEngineDescription... descs) throws UIMAException, IOException {
        ResourceManager resMgr = UIMAFramework.newDefaultResourceManager();

        // Create the components
        final CollectionReader reader = UIMAFramework.produceCollectionReader(readerDesc, resMgr, null);

        // Create AAE
        final AnalysisEngineDescription aaeDesc = createEngineDescription(descs);

        // Instantiate AAE
        final AnalysisEngine aae = UIMAFramework.produceAnalysisEngine(aaeDesc, resMgr, null);
        // Create CAS from merged metadata
        final CAS cas = CasCreationUtils.createCas(asList(reader.getMetaData(), aae.getMetaData()));
        reader.typeSystemInit(cas.getTypeSystem());

        try {
            // Process
            TokenAnnotationConsumer sac = new TokenAnnotationConsumer();
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

    private Sentence extractAndAnnotate(String sentenceString, int pairId, int sentenceId) {
        try {
            CollectionReaderDescription cr = createReaderDescription(
                    StringReader.class,
                    StringReader.PARAM_DOCUMENT_ID, ("sp" + pairId + "s" + sentenceId),
                    StringReader.PARAM_DOCUMENT_TEXT, sentenceString,
                    StringReader.PARAM_LANGUAGE, "en");


            AnalysisEngineDescription seg = createEngineDescription(BreakIteratorSegmenter.class);
            AnalysisEngineDescription tagger = createEngineDescription(OpenNlpPosTagger.class);
            AnalysisEngineDescription lemmatizer = createEngineDescription(StanfordLemmatizer.class);
            return processSentence(cr, seg, tagger, lemmatizer);

        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        } catch (UIMAException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void load() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = null;
            SpaceSegmenter s = new SpaceSegmenter();
            int sentenceId = 1;
            line = br.readLine();
            while (line != null && line.length() > 0) {
                List<Sentence> sentencePair = new ArrayList<>();
                String[] sentences = line.replace("-", "_").split("\t");


                Sentence sentence1 = extractAndAnnotate(sentences[0], sentenceId, 1);
                Sentence sentence2 = extractAndAnnotate(sentences[1], sentenceId, 2);

                sentence1.setSenses(senseLoader.getAllSenses(sentence1.getLexicalEntries()));
                sentence2.setSenses(senseLoader.getAllSenses(sentence2.getLexicalEntries()));
                sentencePair.add(sentence1);
                sentencePair.add(sentence2);
                getSentencePairs().add(sentencePair);
                line = br.readLine();
            }
        } catch (IOException e) {
            System.err.println("Filed to load file" + fileName);
        }
    }
}
