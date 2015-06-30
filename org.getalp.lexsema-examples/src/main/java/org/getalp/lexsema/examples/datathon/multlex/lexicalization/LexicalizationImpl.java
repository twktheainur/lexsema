package org.getalp.lexsema.examples.datathon.multlex.lexicalization;

public class LexicalizationImpl implements Lexicalization {
    String lexicalization;
    Double confidence;

    public LexicalizationImpl(String lexicalization, Double confidence) {
        this.lexicalization = lexicalization;
        this.confidence = confidence;
    }
    @Override
    public String getLexicalization() {
        return lexicalization;
    }
    @Override
    public Double getConfidence() {
        return confidence;
    }

    @Override
    public int compareTo(Lexicalization o) {
        return confidence.compareTo(o.getConfidence());
    }
}
