package org.getalp.lexsema.ontolex;

/**
 * An interface for <code>LexicalSense</code> instances
 */
public interface LexicalSense extends LexicalResourceEntity {
    String getDefinition();

    void setDefinition(String definition);

    String getSenseNumber();

    void setSenseNumber(String senseNumber);
}
