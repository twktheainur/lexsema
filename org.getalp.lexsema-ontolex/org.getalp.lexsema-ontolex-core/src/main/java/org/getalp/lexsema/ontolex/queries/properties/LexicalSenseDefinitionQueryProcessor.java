package org.getalp.lexsema.ontolex.queries.properties;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.ontolex.Graph;
import org.getalp.lexsema.ontolex.queries.ARQSelectQueryImpl;
import org.getalp.lexsema.ontolex.queries.AbstractQueryProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * This query processor implements a query that retrieves all <code>LexicalSense</code>s for a
 * given <code>LexicalEntry</code>.
 */
public final class LexicalSenseDefinitionQueryProcessor extends AbstractQueryProcessor<String> {

    private final String DEFINITION_RESULT_VAR = "d";
    private String uri;


    public LexicalSenseDefinitionQueryProcessor(Graph graph,
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
                getNode("lemon:canonicalForm"),
                Var.alloc(DEFINITION_RESULT_VAR));
        addResultVar(DEFINITION_RESULT_VAR);
    }

    @Override
    public List<String> processResults() {
        List<String> entries = new ArrayList<>();
        while (hasNextResult()) {
            QuerySolution qs = nextSolution();
            String result;
            result = qs.get(DEFINITION_RESULT_VAR).toString().split("@")[0];
            entries.add(result);
        }
        return entries;
    }
}
