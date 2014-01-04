package org.getalp.lexsema.lexicalresource.lemon;

import org.getalp.lexsema.lexicalresource.AbstractLexicalResourceEntity;
import org.getalp.lexsema.lexicalresource.LexicalResource;

/**
 * Class representing a lemon LexicalSense entity
 */
public class LexicalSense extends AbstractLexicalResourceEntity {

    private String definition;

    /**
     * Constructor for LexicalSense
     *
     * @param r   the lemon lexical resource
     * @param uri the uri of the LexicalSense
     */
    protected LexicalSense(LexicalResource r, String uri) {
        super(r, uri);
    }
}
