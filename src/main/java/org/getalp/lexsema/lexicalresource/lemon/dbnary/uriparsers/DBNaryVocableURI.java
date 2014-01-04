package org.getalp.lexsema.lexicalresource.lemon.dbnary.uriparsers;

import org.getalp.lexsema.lexicalresource.URIParser;
import org.getalp.lexsema.lexicalresource.lemon.dbnary.Vocable;
import org.getalp.lexsema.ontology.graph.Node;

/**
 * URI PArser for a DBNary <code>Vocable</code>
 */
public class DBNaryVocableURI implements URIParser {
    @Override
    public void parseURI(Node entry) {
        if (entry instanceof Vocable) {
            String[] uri = entry.getURI().split("/");
            ((Vocable) entry).setVocable(uri[uri.length - 1]);
        }
    }
}
