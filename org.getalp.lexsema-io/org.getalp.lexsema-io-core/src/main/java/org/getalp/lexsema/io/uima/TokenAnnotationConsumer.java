package org.getalp.lexsema.io.uima;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.CasConsumer_ImplBase;
import org.apache.uima.fit.util.CasUtil;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.SentenceImpl;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.WordImpl;

import java.util.Collection;


public class TokenAnnotationConsumer extends CasConsumer_ImplBase implements SentenceLevelConsumer {

    private Sentence sentence;


    public TokenAnnotationConsumer() {

    }


    @Override
    public void process(CAS cas) throws AnalysisEngineProcessException {

        Type TOKEN_ANNOTATION = cas.getTypeSystem().getType("de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token");
        Collection<AnnotationFS> tokens = CasUtil.select(cas, TOKEN_ANNOTATION);

        sentence = new SentenceImpl("");
        for (AnnotationFS tokenAnnot : tokens) {
            Token token = (Token) tokenAnnot;
            String lemma = token.getLemma().getValue();
            String pos = token.getPos().getPosValue();
            String sform = token.getCoveredText();
            Word lexEnt = new WordImpl("", lemma, sform, pos);
            sentence.addWord(lexEnt);
        }
    }


    @Override
    public Sentence getSentence() {
        return sentence;
    }

}
