package org.getalp.lexsema.lexicalresource.dbnary;

import org.getalp.lexsema.lexicalresource.URIParser;
import org.getalp.lexsema.ontology.graph.Node;
import org.getalp.lexsema.ontology.lemon.LexicalEntry;

public class DBNaryLexicalEntryURI implements URIParser {
    @Override
    public void extractInformation(Node entry) {
        if (entry instanceof LexicalEntry) {
            String[] uri = entry.getURI().split("_");
        }
    }
}
