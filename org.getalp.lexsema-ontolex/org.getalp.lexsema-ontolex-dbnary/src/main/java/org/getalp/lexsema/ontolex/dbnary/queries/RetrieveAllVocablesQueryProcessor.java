package org.getalp.lexsema.ontolex.dbnary.queries;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.graph.Graph;
import org.getalp.lexsema.ontolex.queries.ARQSelectQuery;
import org.getalp.lexsema.ontolex.queries.ARQSelectQueryImpl;
import org.getalp.lexsema.ontolex.queries.AbstractQueryProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * This query processor implements a query that retrieves all <code>LexicalSense</code>s for a
 * given <code>LexicalEntry</code>.
 */
public final class RetrieveAllVocablesQueryProcessor extends AbstractQueryProcessor<Vocable> {

    private final static String VOCABLE_URI = "v";
    private final LexicalResourceEntityFactory lexicalResourceEntityFactory;

    public RetrieveAllVocablesQueryProcessor(Graph graph,
                                             LexicalResourceEntityFactory lexicalResourceEntityFactory) {
        super(graph);
        this.lexicalResourceEntityFactory = lexicalResourceEntityFactory;
        initialize();
    }

    @Override
    protected final void defineQuery() {
        ARQSelectQuery q = new ARQSelectQueryImpl();
        q.setDistinct(true);
        setQuery(q);
        addTriple(Var.alloc(VOCABLE_URI),
                getNode("rdf:type"),
                getNode("dbnary:Vocable"));
        addResultVar(VOCABLE_URI);
    }


    @Override
    public List<Vocable> processResults() {
        List<Vocable> vocables = new ArrayList<>();
        while (hasNextResult()) {
            QuerySolution qs = nextSolution();
            String[] uri = qs.get(VOCABLE_URI).asResource().getURI().split("/");
            vocables.add((Vocable) lexicalResourceEntityFactory.getEntity(Vocable.class, uri[uri.length - 1], null));
        }
        return vocables;
    }
}
