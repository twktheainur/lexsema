package org.getalp.lexsema.examples.datathon.multlex.lexicalization;

public interface Lexicalization extends Comparable<Lexicalization> {
    public String getLexicalization();
    public Double getConfidence();
}
