package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.DefaultSemanticSignatureFactory;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.tartarus.snowball.ext.EnglishStemmer;

public class StemmingSignatureEnrichment extends SignatureEnrichmentAbstract {

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature signature) {
        SemanticSignature newSignature = DefaultSemanticSignatureFactory.DEFAULT.createSemanticSignature();
        EnglishStemmer stemmer = new EnglishStemmer();
        for (SemanticSymbol symbol : signature) {
            stemmer.setCurrent(symbol.getSymbol());
            stemmer.stem();
            newSignature.addSymbol(stemmer.getCurrent());
        }
        return newSignature;
    }

}
