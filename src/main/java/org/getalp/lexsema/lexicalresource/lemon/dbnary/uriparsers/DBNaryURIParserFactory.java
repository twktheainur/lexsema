package org.getalp.lexsema.lexicalresource.lemon.dbnary.uriparsers;

import org.getalp.lexsema.lexicalresource.LexicalResourceEntity;
import org.getalp.lexsema.lexicalresource.URIParser;
import org.getalp.lexsema.lexicalresource.lemon.LexicalEntry;
import org.getalp.lexsema.lexicalresource.lemon.LexicalSense;
import org.getalp.lexsema.lexicalresource.lemon.dbnary.Vocable;
import org.getalp.lexsema.ontology.graph.Node;

public class DBNaryURIParserFactory extends URIParserFactory {
    @Override
    public URIParser createURIParser(Class<? extends LexicalResourceEntity> pclass) {
        URIParser ret = null;
        if (pclass.equals(LexicalEntry.class)) {
            ret = new URIParser() {
                @Override
                public void parseURI(Node entry) {
                    LexicalEntry le = (LexicalEntry) entry;
                    String[] uri = entry.getURI().split("/");
                    String[] cannonicalURI = uri[uri.length - 1].split("__");
                    le.setLemma(cannonicalURI[0]);
                    le.setPartOfSpeech(cannonicalURI[1]);
                    le.setNumber(Integer.valueOf(cannonicalURI[2]));
                }
            };

        } else if (pclass.equals(Vocable.class)) {
            ret = new URIParser() {
                @Override
                public void parseURI(Node entry) {
                    String[] uri = entry.getURI().split("/");
                    ((Vocable) entry).setVocable(uri[uri.length - 1]);
                }
            };
        } else if (pclass.equals(LexicalSense.class)) {
            ret = new URIParser() {
                @Override
                public void parseURI(Node entry) {
                    String[] uri = entry.getURI().split("/");
                    ((LexicalSense) entry).setSenseNumber(Integer.valueOf(uri[uri.length - 1].split("__")[1].split("_")[1]));

                }
            };
        }
        return ret;
    }
}
