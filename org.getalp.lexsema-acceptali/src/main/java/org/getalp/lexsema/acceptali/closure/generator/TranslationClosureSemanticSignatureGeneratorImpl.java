package org.getalp.lexsema.acceptali.closure.generator;


import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosureWithSignatures;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.SenseImpl;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.util.Language;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class TranslationClosureSemanticSignatureGeneratorImpl implements TranslationClosureSemanticSignatureGenerator {
    @Override
    public LexicalResourceTranslationClosure<Sense> generateSemanticSignatures(LexicalResourceTranslationClosure<LexicalSense> rawClosure) {
        LexicalResourceTranslationClosure<Sense> outputClosure = new LexicalResourceTranslationClosureWithSignatures();
        Map<Language, Map<LexicalEntry, Set<LexicalSense>>> otherClosureData = rawClosure.senseClosureByLanguageAndEntry();
        for (Language language : otherClosureData.keySet()) {
            for (LexicalEntry localLexicalEntry : otherClosureData.get(language).keySet()) {
                for (LexicalSense ls : otherClosureData.get(language).get(localLexicalEntry)) {
                    outputClosure.addSense(language, localLexicalEntry, buildSense(ls));
                }
            }
        }
        return outputClosure;
    }

    private Sense buildSense(LexicalSense lexicalSense) {
        Sense sense = new SenseImpl(lexicalSense);
        SemanticSignature semanticSignature = new SemanticSignatureImpl();
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
