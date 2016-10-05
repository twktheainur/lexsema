package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.StopList;

public class StopwordsRemovingSignatureEnrichment extends SignatureEnrichmentAbstract {

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature signature) {
        SemanticSignature newSignature = new SemanticSignatureImpl();
        for (SemanticSymbol symbol : signature) {
            if (!StopList.isStopWord(symbol.getSymbol())) {
                newSignature.addSymbol(symbol);
            }
        }
        return newSignature;
    }

}
