package org.getalp.lexsema.acceptali.crosslingual;

import org.getalp.lexsema.acceptali.crosslingual.translation.Translator;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslatorCrossLingualSimilarity implements CrossLingualSimilarity {

    private static Logger logger = LoggerFactory.getLogger(TranslatorCrossLingualSimilarity.class);

    private final SimilarityMeasure similarityMeasure;
    private Translator translator;

    public TranslatorCrossLingualSimilarity(final SimilarityMeasure similarityMeasure, final Translator translator) {
        this.similarityMeasure = similarityMeasure;
        this.translator = translator;
    }

    @Override
    public double compute(Sense a, Sense b) {

        String definitionB = b.getDefinition();
        String translatedDefinitionB = translator.translate(definitionB, b.getLanguage(), a.getLanguage());

        SemanticSignature translatedSignature = new SemanticSignatureImpl();
        translatedSignature.addSymbolString(translatedDefinitionB);
        logger.info(String.format("%s || %s", a.getDefinition(), translatedDefinitionB));
        return similarityMeasure.compute(a.getSemanticSignature(), translatedSignature, null, null);
    }

    private double computeMonolingualSimilarity(Sense a, Sense b) {
        return similarityMeasure.compute(a.getSemanticSignature(), b.getSemanticSignature(),
                null, null);
    }

    @Override
    public String toString() {
        return "Translation based cross-lingual similarity with " + similarityMeasure.toString() + " and " + translator.toString();
    }
}
