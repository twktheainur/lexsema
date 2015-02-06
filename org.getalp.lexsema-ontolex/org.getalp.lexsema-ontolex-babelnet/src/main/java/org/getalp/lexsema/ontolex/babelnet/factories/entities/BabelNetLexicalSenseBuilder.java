package org.getalp.lexsema.ontolex.babelnet.factories.entities;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.LexicalSenseImpl;
import org.getalp.lexsema.ontolex.babelnet.queries.LexicalSenseBabelNetPropertiesQueryProcessor;
import org.getalp.lexsema.ontolex.factories.entities.AbstractLexicalResourceEntityBuilder;
import org.getalp.lexsema.ontolex.queries.QueryProcessor;

import java.util.List;
import java.util.Map;

public class BabelNetLexicalSenseBuilder extends AbstractLexicalResourceEntityBuilder<LexicalSense> {

    public BabelNetLexicalSenseBuilder() {
        super();
    }

    @Override
    public LexicalSense buildEntity(String uri, LexicalResourceEntity parent, Map<String, String> parameters) {

        String senseNumber = "";

        retrieveURIParser(LexicalEntry.class);
        Map<String, String> values = parseURI(uri);

        QueryProcessor<Map<String, String>> queryProcessor = new LexicalSenseBabelNetPropertiesQueryProcessor(getGraph(), uri);
        queryProcessor.runQuery();
        List<Map<String, String>> results = queryProcessor.processResults();

        LexicalSense instance = new LexicalSenseImpl(getLexicalResource(), uri, parent, senseNumber);
        instance.setDefinition(results.get(0).get("definition"));
        return instance;
    }

}
