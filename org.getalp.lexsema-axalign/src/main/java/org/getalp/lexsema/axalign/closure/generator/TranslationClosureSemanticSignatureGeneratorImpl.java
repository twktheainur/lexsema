package org.getalp.lexsema.axalign.closure.generator;


import org.getalp.lexsema.axalign.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.axalign.closure.LexicalResourceTranslationClosureWithSignatures;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.similarity.DefaultDocumentFactory;
import org.getalp.lexsema.similarity.DocumentFactory;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.signatures.DefaultSemanticSignatureFactory;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.util.Language;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class TranslationClosureSemanticSignatureGeneratorImpl implements TranslationClosureSemanticSignatureGenerator {
    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.DEFAULT_DOCUMENT_FACTORY;

    @Override
    public LexicalResourceTranslationClosure<Sense> generateSemanticSignatures(LexicalResourceTranslationClosure<LexicalSense> rawClosure) {
        LexicalResourceTranslationClosure<Sense> outputClosure = new LexicalResourceTranslationClosureWithSignatures();
        Map<Language, Map<LexicalEntry, Set<LexicalSense>>> otherClosureData = rawClosure.senseClosureByLanguageAndEntry();
        for (Map.Entry<Language, Map<LexicalEntry, Set<LexicalSense>>> languageMapEntry : otherClosureData.entrySet()) {
            for (LexicalEntry localLexicalEntry : languageMapEntry.getValue().keySet()) {
                for (LexicalSense ls : languageMapEntry.getValue().get(localLexicalEntry)) {
                    outputClosure.addSense(languageMapEntry.getKey(), localLexicalEntry, buildSense(ls));
                }
            }
        }
        return outputClosure;
    }

    private Sense buildSense(LexicalSense lexicalSense) {
        Sense sense = DOCUMENT_FACTORY.createSense(lexicalSense);
        SemanticSignature semanticSignature = DefaultSemanticSignatureFactory.DEFAULT.createSemanticSignature();
        String definition = lexicalSense.getDefinition();
        addToSignature(semanticSignature, definition);
        sense.setSemanticSignature(semanticSignature);
        return sense;
    }

    private void addToSignature(SemanticSignature signature, String def) {
        StringTokenizer st = new StringTokenizer(def, " ", false);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            signature.addSymbol(token, 1.0);
        }
    }

}
