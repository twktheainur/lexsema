package org.getalp.lexsema.dbnary.uriparsers;

import org.getalp.lexsema.dbnary.Vocable;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.graph.Node;
import org.getalp.lexsema.ontolex.uri.URIParser;
import org.getalp.lexsema.ontolex.uri.URIParserFactory;

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
