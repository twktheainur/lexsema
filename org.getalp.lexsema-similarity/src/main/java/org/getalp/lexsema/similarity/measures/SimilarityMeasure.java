package org.getalp.lexsema.similarity.measures;


import org.getalp.lexsema.similarity.signatures.SemanticSignature;

import java.util.Map;

/**
 * Interface representing a Similarity Measure
 */
public interface SimilarityMeasure {
    /**
     * Compute the semantic similarity between
     * semantic signatures <code>a</code> and <code>b</code>
     * with related signature <code>relatedSignaturesA</code>
     * and <code>relatedSignaturesA</code>.
     *
     * @param sigA               The first semantic signature.
     * @param sigB               The second semantic signature.
     * @param relatedSignaturesA The related signatures for <code>a</code>, can be null.
     * @param relatedSignaturesB The related signatures for <code>b</code>, can be null.
     * @return The semantic similarity values between <code>a</code> and <code>b</code>.
     */
    public double compute(SemanticSignature sigA, SemanticSignature sigB,
                          Map<String, SemanticSignature> relatedSignaturesA,
                          Map<String, SemanticSignature> relatedSignaturesB);

    /**
     * Compute the semantic similarity between
     * semantic signatures <code>a</code> and <code>b</code>
     * with related signature <code>relatedSignaturesA</code>
     * and <code>relatedSignaturesA</code>.
     *
     * @param sigA               The first semantic signature.
     * @param sigB               The second semantic signature.
     * @return The semantic similarity values between <code>a</code> and <code>b</code>.
     */
    public double compute(SemanticSignature sigA, SemanticSignature sigB);
}
