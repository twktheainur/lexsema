package org.getalp.lexsema.ontolex.babelnet.factories.entities;

import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.VocableImpl;
import org.getalp.lexsema.ontolex.factories.entities.AbstractLexicalResourceEntityBuilder;

import java.util.Map;

public class VocableBuilder extends AbstractLexicalResourceEntityBuilder<Vocable> {

    public VocableBuilder() {
        super();
    }


    @Override
    public Vocable buildEntity(String uri, LexicalResourceEntity parent, Map<String, String> parameters) {
        /**
         *  We try to get as much information as possible from the URI if any URI parsers are available for
         *  the current <code>LexicalResource</code>
         */
        retrieveURIParser(Vocable.class);
        Map<String, String> values = parseURI(uri);
        String vocable = "";

        if (values != null) {
            vocable = values.get("vocable");
            if (vocable == null) {
                vocable = "";
            }
        }
        return new VocableImpl(getLexicalResource(), uri, parent, vocable);
    }
}
