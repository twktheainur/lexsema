package org.getalp.lexsema.io.uima;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.CasConsumer_ImplBase;
import org.apache.uima.fit.util.CasUtil;
import org.getalp.lexsema.similarity.*;
import org.getalp.lexsema.util.Language;

import java.util.Collection;
import java.util.Iterator;


public class TokenAnnotationConsumer extends CasConsumer_ImplBase implements TokenConsumer {

    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.DEFAULT_DOCUMENT_FACTORY;

    private Text text;
    private final Language language;

    public TokenAnnotationConsumer(Language language) {
        text = DOCUMENT_FACTORY.nullText();
        this.language = language;
    }

    @Override
    public void process(CAS aCAS) throws AnalysisEngineProcessException {
        Type SENTENCE_ANNOTATION = aCAS.getTypeSystem().getType("de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence");
        Collection<AnnotationFS> sentences = CasUtil.select(aCAS, SENTENCE_ANNOTATION);
        Iterator<AnnotationFS> sentenceIterator = sentences.iterator();
        AnnotationFS sentenceAnnotation = sentenceIterator.next();
        int sentenceNumber = 0;


        Type TOKEN_ANNOTATION = aCAS.getTypeSystem().getType("de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token");
        Collection<AnnotationFS> tokens = CasUtil.select(aCAS, TOKEN_ANNOTATION);

        text = DOCUMENT_FACTORY.createText(language);
        Sentence sentence = DOCUMENT_FACTORY.createSentence(String.format("s%d",sentenceNumber),language);
        for (AnnotationFS tokenAnnot : tokens) {
            Token token = (Token) tokenAnnot;
            String lemma = token.getLemma().getValue();
            String pos = token.getPos().getPosValue();
            String sform = token.getCoveredText();
            int begin = token.getBegin();
            int end = token.getEnd();
            Word lexEnt = DOCUMENT_FACTORY.createWord("", lemma, sform, pos, language, begin, end);
            //text.addWord(lexEnt);
            sentence.addWord(lexEnt);
            lexEnt.setEnclosingSentence(sentence);
            if(end >= sentenceAnnotation.getEnd()){
                text.addSentence(sentence);
                sentenceNumber++;
                sentence = DOCUMENT_FACTORY.createSentence(String.format("s%d",sentenceNumber),language);
                if(sentenceIterator.hasNext()) {
                    sentenceAnnotation = sentenceIterator.next();
                }
            }
        }

    }


    @Override
    public Text getText() {
        return text;
    }

}
