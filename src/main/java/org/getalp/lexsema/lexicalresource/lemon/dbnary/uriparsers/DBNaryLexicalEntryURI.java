package org.getalp.lexsema.lexicalresource.lemon.dbnary.uriparsers;

import org.getalp.lexsema.lexicalresource.URIParser;
import org.getalp.lexsema.lexicalresource.lemon.LexicalEntry;
import org.getalp.lexsema.ontology.graph.Node;

/**
 * A URI PArser for a DBNary <code>LexicalEntry</code> URI
 */
public class DBNaryLexicalEntryURI implements URIParser {
    @Override
    public void parseURI(Node entry) {
        if (entry instanceof LexicalEntry) {
            LexicalEntry le = (LexicalEntry) entry;
            String[] uri = entry.getURI().split("/");
            String[] cannonicalURI = uri[uri.length - 1].split("__");
            le.setLemma(cannonicalURI[0]);
            le.setPartOfSpeech(cannonicalURI[1]);
            le.setNumber(Integer.valueOf(cannonicalURI[2]));
        }
    }
}
