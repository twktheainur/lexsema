package org.getalp.lexsema.io.uima;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.CasConsumer_ImplBase;
import org.apache.uima.fit.util.CasUtil;
import org.getalp.lexsema.similarity.*;

import java.util.Collection;


public class TokenAnnotationConsumer extends CasConsumer_ImplBase implements TokenConsumer {

    private Text text;


    public TokenAnnotationConsumer() {

    }


    @Override
    public void process(CAS cas) throws AnalysisEngineProcessException {

        Type TOKEN_ANNOTATION = cas.getTypeSystem().getType("de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token");
        Collection<AnnotationFS> tokens = CasUtil.select(cas, TOKEN_ANNOTATION);

        text = new TextImpl();
        for (AnnotationFS tokenAnnot : tokens) {
            Token token = (Token) tokenAnnot;
            String lemma = token.getLemma().getValue();
            String pos = token.getPos().getPosValue();
            String sform = token.getCoveredText();
            Word lexEnt = new WordImpl("", lemma, sform, pos);
            text.addWord(lexEnt);
        }
    }


    @Override
    public Text getText() {
        return text;
    }

}
