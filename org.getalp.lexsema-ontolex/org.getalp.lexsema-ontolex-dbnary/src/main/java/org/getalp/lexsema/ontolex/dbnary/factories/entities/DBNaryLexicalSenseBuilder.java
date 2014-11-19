package org.getalp.lexsema.ontolex.dbnary.factories.entities;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.LexicalSenseImpl;
import org.getalp.lexsema.ontolex.dbnary.queries.LexicalSenseDbnaryPropertiesQueryProcessor;
import org.getalp.lexsema.ontolex.factories.entities.AbstractLexicalResourceEntityBuilder;
import org.getalp.lexsema.ontolex.queries.QueryProcessor;

import java.util.List;
import java.util.Map;

public class DBNaryLexicalSenseBuilder extends AbstractLexicalResourceEntityBuilder<LexicalSense> {

    public DBNaryLexicalSenseBuilder() {
        super();
    }

    @Override
    public LexicalSense buildEntity(String uri, LexicalResourceEntity parent, Map<String, String> parameters) {

        String senseNumber = "";

        retrieveURIParser(LexicalEntry.class);
        Map<String, String> values = parseURI(uri);

        if (values != null) {
            senseNumber = values.get("senseNumber");
        }
        QueryProcessor<Map<String, String>> queryProcessor = new LexicalSenseDbnaryPropertiesQueryProcessor(getGraph(), uri);
        queryProcessor.runQuery();
        List<Map<String, String>> results = queryProcessor.processResults();
        if (senseNumber == null) {
            senseNumber = results.get(0).get("senseNumber");
        }
        LexicalSense instance = new LexicalSenseImpl(getLexicalResource(), uri, parent, senseNumber);
        instance.setDefinition(results.get(0).get("definition"));
        return instance;
    }

}
