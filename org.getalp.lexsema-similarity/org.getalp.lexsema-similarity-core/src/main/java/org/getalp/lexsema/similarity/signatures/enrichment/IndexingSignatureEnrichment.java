package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.DefaultSemanticSignatureFactory;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndex;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndexImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;

public class IndexingSignatureEnrichment extends SignatureEnrichmentAbstract {

    private final SymbolIndex symbolIndex = new SymbolIndexImpl();

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
        IndexedSemanticSignature indexedSignature = DefaultSemanticSignatureFactory.DEFAULT.createIndexedSemanticSignature(symbolIndex);
        for (SemanticSymbol symbol : semanticSignature) {
            indexedSignature.addSymbol(symbol);
        }
        indexedSignature.sort();
        return indexedSignature;
    }

}
