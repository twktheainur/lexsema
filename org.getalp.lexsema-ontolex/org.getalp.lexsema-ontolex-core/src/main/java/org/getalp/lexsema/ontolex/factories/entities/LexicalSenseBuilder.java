package org.getalp.lexsema.ontolex.factories.entities;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.LexicalSenseImpl;
import org.getalp.lexsema.ontolex.queries.QueryProcessor;
import org.getalp.lexsema.ontolex.queries.properties.LexicalSenseDefinitionQueryProcessor;

import java.util.List;
import java.util.Map;

public class LexicalSenseBuilder extends AbstractLexicalResourceEntityBuilder<LexicalSense> {

    public LexicalSenseBuilder() {
        super();
    }

    @Override
    public LexicalSense buildEntity(String uri, LexicalResourceEntity parent, Map<String, String> parameters) {

        String senseNumber = "";

        retrieveURIParser(LexicalEntry.class);
        Map<String, String> values = parseURI(getResourceGraphURI() + uri);

        if (values != null) {
            senseNumber = values.get("senseNumber");
            if (senseNumber == null) {
                senseNumber = "";
            }
        }
        LexicalSense instance = new LexicalSenseImpl(getLexicalResource(), uri, parent, senseNumber);
        fetchDefinition(instance);

        return instance;
    }

    protected void fetchDefinition(LexicalSense lexicalSense) {
        QueryProcessor<String> definitionQuery =
                new LexicalSenseDefinitionQueryProcessor(getGraph(), lexicalSense.getNode().toString());
        definitionQuery.runQuery();
        List<String> definitionResults = definitionQuery.processResults();
        if (!definitionResults.isEmpty()) {
            lexicalSense.setDefinition(definitionResults.get(0));
        }
    }

    @Override
    public LexicalResourceEntityBuilder<LexicalSense> clone() throws CloneNotSupportedException {
        super.clone();
        LexicalResourceEntityBuilder<LexicalSense> clone = new LexicalSenseBuilder();
        clone.setLexicalResource(getLexicalResource());
        return clone;
    }

}
