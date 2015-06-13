package org.getalp.lexsema.ontolex.dbnary.queries;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Regex;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.graph.Graph;
import org.getalp.lexsema.ontolex.queries.ARQSelectQuery;
import org.getalp.lexsema.ontolex.queries.ARQSelectQueryImpl;
import org.getalp.lexsema.ontolex.queries.AbstractQueryProcessor;
import org.getalp.lexsema.util.Language;

import java.util.ArrayList;
import java.util.List;

/**
 * This query processor implements a query that retrieves all <code>LexicalSense</code>s for a
 * given <code>LexicalEntry</code>.
 */
public final class RetrieveAllVocablesQueryProcessor extends AbstractQueryProcessor<Vocable> {

    private final static String VOCABLE_URI = "v";
    private final LexicalResourceEntityFactory lexicalResourceEntityFactory;
    private Language language;

    public RetrieveAllVocablesQueryProcessor(Graph graph,
                                             LexicalResourceEntityFactory lexicalResourceEntityFactory, Language language) {
        super(graph);
        this.lexicalResourceEntityFactory = lexicalResourceEntityFactory;
        this.language = language;
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
            Expr lemmaRegexMatch = new E_Regex(new ExprVar(VOCABLE_URI),"/"+language.getISO3Code()+"/","");
        addFilter(lemmaRegexMatch);
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
