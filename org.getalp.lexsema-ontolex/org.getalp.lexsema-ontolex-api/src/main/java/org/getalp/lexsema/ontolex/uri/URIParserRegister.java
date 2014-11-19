package org.getalp.lexsema.ontolex.uri;

import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.exceptions.NotRegisteredException;

/**
 * A register for URIParser implementations. Ontolex does not specify the format of URIs for lexical resources
 * structured according to Ontolex. Some resources such as DBNary encode information in the URI. The retrieval of this
 * information would normally require to make a SPARQL query and parsing the URI when possible leads to a significant speed
 * improvement.
 */
public interface URIParserRegister {
    /**
     * Register a <code>URIParser</code> implementation for a specific type of <code>LexicalResourceEntity</code>
     *
     * @param lexicalResourceEntityClass The class of the <code>LexicalResourceEntity</code> type
     * @param uriParser                  The <code>URIParser</code> instance to register
     */
    public void registerURIParser(Class<? extends LexicalResourceEntity> lexicalResourceEntityClass, URIParser uriParser);

    /**
     * Returns a registered <code>URIParser</code> implementation for a specific type of <code>LexicalResourceEntity</code>
     *
     * @param lexicalResourceEntityClass The class of the <code>LexicalResourceEntity</code> type
     * @return The registered <code>URIParser</code> instance
     * @throws NotRegisteredException <code>NotRegisteredException</code> is thrown when there is no registered URIParser for <code>lexicalResourceEntityClass</code>
     */
    public URIParser getFactory(Class<? extends LexicalResourceEntity> lexicalResourceEntityClass) throws NotRegisteredException;
}
