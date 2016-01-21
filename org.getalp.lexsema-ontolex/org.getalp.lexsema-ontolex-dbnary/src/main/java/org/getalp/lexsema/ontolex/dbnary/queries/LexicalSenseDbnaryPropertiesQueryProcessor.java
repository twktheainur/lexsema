package org.getalp.lexsema.ontolex.dbnary.queries;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.ontolex.Graph;
import org.getalp.lexsema.ontolex.queries.ARQSelectQueryImpl;
import org.getalp.lexsema.ontolex.queries.AbstractQueryProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This query processor implements a query that retrieves all <code>LexicalSense</code>s for a
 * given <code>LexicalEntry</code>.
 */
public final class LexicalSenseDbnaryPropertiesQueryProcessor extends AbstractQueryProcessor<Map<String, String>> {

    private final String DEFINITION_RESULT_VAR = "definition";
    private final String SENSE_NUMBER_RESULT_VAR = "senseNumber";
    private String uri;


    public LexicalSenseDbnaryPropertiesQueryProcessor(Graph graph,
                                                      String uri) {
        super(graph);
        this.uri = uri;
        initialize();
    }

    @Override
    protected final void defineQuery() {
        setQuery(new ARQSelectQueryImpl());
        addTriple(getNode(uri),
                getNode("lemon:definition"),
                Var.alloc("id"));
        addTriple(Var.alloc("id"),
                getNode("lemon:value"),
                Var.alloc(DEFINITION_RESULT_VAR));
        addTriple(getNode(uri),
                getNode("dbnary:senseNumber"),
                Var.alloc(SENSE_NUMBER_RESULT_VAR));
        addResultVar(DEFINITION_RESULT_VAR);
        addResultVar(SENSE_NUMBER_RESULT_VAR);
    }

    @Override
    public List<Map<String, String>> processResults() {
        List<Map<String, String>> props = new ArrayList<>();
        while (hasNextResult()) {
            Map<String, String> prop = new HashMap<>();
            QuerySolution qs = nextSolution();
            prop.put(DEFINITION_RESULT_VAR, qs.get(DEFINITION_RESULT_VAR).toString().split("@")[0]);
            prop.put(SENSE_NUMBER_RESULT_VAR, qs.get(SENSE_NUMBER_RESULT_VAR).toString().split("\\^\\^")[0]);
            props.add(prop);
        }
        return props;
    }
}
