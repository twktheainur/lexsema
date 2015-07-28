package org.getalp.lexsema.ontolex;

/**
 * An interface for {@code LexicalSense} instances
 */
public interface LexicalSense extends LexicalResourceEntity {
    String getDefinition();

    void setDefinition(String definition);

    String getSenseNumber();

    void setSenseNumber(String senseNumber);

    boolean isNull();
}
