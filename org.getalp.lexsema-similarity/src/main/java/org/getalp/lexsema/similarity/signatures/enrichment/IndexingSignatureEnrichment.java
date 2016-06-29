package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndex;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndexImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.Language;
import org.tartarus.snowball.ext.EnglishStemmer;

public class IndexingSignatureEnrichment extends SignatureEnrichment {

    private final SymbolIndex symbolIndex = new SymbolIndexImpl();

    public SemanticSignature enrichSemanticSignature(SemanticSignature signature) {
        IndexedSemanticSignature indexedSignature = new IndexedSemanticSignatureImpl(symbolIndex);
        for (SemanticSymbol symbol : signature) {
            indexedSignature.addSymbol(symbol);
        }
        indexedSignature.sort();
        return indexedSignature;
    }

}
