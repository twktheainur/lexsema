package org.getalp.lexsema.ontolex.dbnary;

import org.getalp.lexsema.ontolex.LexicalResourceEntity;

/**
 * DBNary API Interface for {@code Vocable}s
 */
public interface Vocable extends LexicalResourceEntity {
    /**
     * Get the written representation of the vocable
     *
     * @return the written representation of the vocable
     */
    String getVocable();

    /**
     * Set the string representation of the vocable
     *
     * @param vocable the vocable string representation to set
     */
    void setVocable(String vocable);

}
