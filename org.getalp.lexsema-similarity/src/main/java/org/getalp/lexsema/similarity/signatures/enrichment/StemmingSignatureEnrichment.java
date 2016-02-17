package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.Language;
import org.tartarus.snowball.ext.EnglishStemmer;

public class StemmingSignatureEnrichment implements SignatureEnrichment {

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature signature) {
        SemanticSignature newSignature = new SemanticSignatureImpl();
        EnglishStemmer stemmer = new EnglishStemmer();
        for (SemanticSymbol symbol : signature) {
            stemmer.setCurrent(symbol.getSymbol());
            stemmer.stem();
            newSignature.addSymbol(stemmer.getCurrent());
        }
        return newSignature;
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature signature, Language language) {
        return enrichSemanticSignature(signature);
    }

    @Override
    public void close() {

    }
    
}
