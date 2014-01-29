package org.getalp.lexsema.lexicalresource.lemon;

import org.getalp.lexsema.lexicalresource.LexicalResourceEntity;

/**
 * Factory Interface for Lemon Entities
 */
public interface LemonEntityFactory {
    /**
     * @param uri    The uri of the entry
     * @param parent the parent de the entry
     * @return The <code>LexicalEntry</code> instance
     */

    LexicalEntry createLexicalEntry(String uri, LexicalResourceEntity parent);

    /**
     * @param uri    The uri of the entry
     * @param parent the parent de the entry
     * @return The <code>LexicalSense</code> instance
     */
    LexicalSense createLexicalSense(String uri, LexicalResourceEntity parent);
}
