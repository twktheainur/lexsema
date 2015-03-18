package org.getalp.lexsema.acceptali.crosslingual;

import org.getalp.lexsema.acceptali.crosslingual.translation.Translator;
import org.getalp.lexsema.acceptali.word2vec.MultilingualSignatureEnrichment;
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
    private MultilingualSignatureEnrichment enrichment;

    public TranslatorCrossLingualSimilarity(final SimilarityMeasure similarityMeasure, final Translator translator, final MultilingualSignatureEnrichment enrichment) {
        this.similarityMeasure = similarityMeasure;
        this.translator = translator;
        this.enrichment = enrichment;
    }

    public TranslatorCrossLingualSimilarity(final SimilarityMeasure similarityMeasure, final Translator translator) {
        this(similarityMeasure, translator, null);
    }

    @Override
    public double compute(Sense a, Sense b) {

        String definitionB = b.getDefinition();
        String translatedDefinitionB = translator.translate(definitionB, b.getLanguage(), a.getLanguage());

        SemanticSignature translatedSignature = new SemanticSignatureImpl();
        translatedSignature.addSymbolString(translatedDefinitionB);

        SemanticSignature enrichedA = a.getSemanticSignature();
        SemanticSignature enrichedTranslated = translatedSignature;

        if (enrichment != null) {
            enrichedA = enrichment.enrichSemanticSignature(a.getSemanticSignature(), a.getLanguage());
            enrichedTranslated = enrichment.enrichSemanticSignature(translatedSignature, a.getLanguage());
        }

        logger.info(String.format("%s || %s", a.getDefinition(), translatedDefinitionB));
        return similarityMeasure.compute(enrichedA, enrichedTranslated, null, null);
    }

    @Override
    public String toString() {
        return "Translation based cross-lingual similarity with " + similarityMeasure.toString() + " and " + translator.toString();
    }
}
