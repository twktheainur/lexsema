package org.getalp.lexsema.ontolex;

/**
 * An interface for {@code LexicalSense} instances
 */
public interface LexicalConcept extends LexicalResourceEntity {
    String getDefinition();

    void setDefinition(String definition);

    LexicalSense getLexicalizedSense();

    boolean isNull();
}
