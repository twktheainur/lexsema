package org.getalp.lexsema.similarity.measures.crosslingual;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.enrichment.SignatureEnrichment;
import org.getalp.lexsema.translation.Translator;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

public class TranslatorCrossLingualSimilarity implements SimilarityMeasure {

    private static final Logger logger = LoggerFactory.getLogger(TranslatorCrossLingualSimilarity.class);

    private final SimilarityMeasure similarityMeasure;
    private final Translator translator;
    private final SignatureEnrichment enrichment;

    public TranslatorCrossLingualSimilarity(final SimilarityMeasure similarityMeasure, final Translator translator, final SignatureEnrichment enrichment) {
        this.similarityMeasure = similarityMeasure;
        this.translator = translator;
        this.enrichment = enrichment;
    }

    public TranslatorCrossLingualSimilarity(final SimilarityMeasure similarityMeasure, final Translator translator) {
        this(similarityMeasure, translator, null);
    }

    @Override
    public String toString() {
        return "Translation based cross-lingual similarity with " + similarityMeasure.toString() + " and " + translator.toString();
    }

    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB, Map<String, SemanticSignature> relatedSignaturesA, Map<String, SemanticSignature> relatedSignaturesB) {
        if (sigA.getLanguage() == sigB.getLanguage()) {
            return similarityMeasure.compute(sigA, sigB, null, null);
        } else {
            String definitionB = sigB.toString();
            String translatedDefinitionB = translator.translate(definitionB, signatureLanguage(sigB), signatureLanguage(sigA));

            SemanticSignature translatedSignature = new SemanticSignatureImpl();
            translatedSignature.addSymbolString(Arrays.asList(translatedDefinitionB.split(" ")));

            SemanticSignature enrichedA = sigA;
            SemanticSignature enrichedTranslated = translatedSignature;

            if (enrichment != null) {
                enrichedA = enrichment.enrichSemanticSignature(sigA, signatureLanguage(sigA));
                enrichedTranslated = enrichment.enrichSemanticSignature(translatedSignature, signatureLanguage(sigA));
            }

            //logger.info(String.format("%s || %s", sigA.toString(), translatedDefinitionB));
            return similarityMeasure.compute(enrichedA, enrichedTranslated, null, null);
        }
    }

    private Language signatureLanguage(SemanticSignature signature){
        return signature.getLanguage();
    }


    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB) {
        return compute(sigA,sigB,null,null);
    }

}
