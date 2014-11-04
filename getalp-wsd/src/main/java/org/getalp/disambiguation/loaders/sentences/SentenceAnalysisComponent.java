package org.getalp.disambiguation.loaders.sentences;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.CasConsumer_ImplBase;
import org.apache.uima.fit.util.CasUtil;
import org.getalp.disambiguation.LexicalEntry;
import org.getalp.disambiguation.Sentence;

import java.util.Collection;

/**
 * Created by tchechem on 10/31/14.
 */
public class SentenceAnalysisComponent extends CasConsumer_ImplBase {

    private Sentence sentence;


    private Type LEMMA_ANNOTATION;
    private Type POS_ANNOTATION;
    private Type TOKEN_ANNOTATION;
    private Type DocID_ANNOTATION;

    public SentenceAnalysisComponent() {

    }


    @Override
    public void process(CAS cas) throws AnalysisEngineProcessException {

        POS_ANNOTATION = cas.getTypeSystem().getType("de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS");
        LEMMA_ANNOTATION = cas.getTypeSystem().getType("de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma");
        TOKEN_ANNOTATION = cas.getTypeSystem().getType("de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token");
        TOKEN_ANNOTATION = cas.getTypeSystem().getType("de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token");
        DocID_ANNOTATION = cas.getTypeSystem().getType("de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData:documentId");

        Collection<AnnotationFS> tokens = CasUtil.select(cas, TOKEN_ANNOTATION);

        sentence = new Sentence("");
        for (AnnotationFS tokenAnnot : tokens) {
            Token token = (Token) tokenAnnot;
            String lemma = token.getLemma().getValue();
            String pos = token.getPos().getPosValue();
            String sform = token.getCoveredText();
            LexicalEntry lexEnt = new LexicalEntry("", lemma, sform, pos);
            sentence.getLexicalEntries().add(lexEnt);
        }
    }


    public Sentence getSentence() {
        return sentence;
    }

}
