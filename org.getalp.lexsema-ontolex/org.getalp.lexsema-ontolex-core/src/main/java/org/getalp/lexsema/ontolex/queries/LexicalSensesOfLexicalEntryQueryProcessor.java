package org.getalp.lexsema.ontolex.queries;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * This query processor implements a query that retrieves all <code>LexicalSense</code>s for a
 * given <code>LexicalEntry</code>.
 */
public final class LexicalSensesOfLexicalEntryQueryProcessor extends AbstractQueryProcessor<LexicalSense> {

    LexicalEntry lexicalEntry;
    LexicalResourceEntityFactory lexicalResourceEntityFactory;
    Graph graph;

    public LexicalSensesOfLexicalEntryQueryProcessor(Graph graph,
                                                     LexicalResourceEntityFactory lexicalResourceEntityFactory,
                                                     LexicalEntry lexicalEntry) {
        super(graph);
        this.lexicalResourceEntityFactory = lexicalResourceEntityFactory;
        this.lexicalEntry = lexicalEntry;
        initialize();
    }

    @Override
    protected final void defineQuery() {
        setQuery(new ARQSelectQueryImpl());
        String resultVar = "ls";
        addTriple(Var.alloc(resultVar),
                getNode("rdf:type"),
                getNode("lemon:LexicalSense"));
        addTriple(lexicalEntry.getNode(),
                getNode("lemon:sense"),
                Var.alloc(resultVar));
        addResultVar(resultVar);
    }

    private LexicalResourceEntity getEntity(Class<? extends LexicalResourceEntity> productType, String uri, LexicalResourceEntity parent) {
        return lexicalResourceEntityFactory.getEntity(productType, uri, parent);
    }

    @Override
    public List<LexicalSense> processResults() {
        List<LexicalSense> senses = new ArrayList<>();
        while (hasNextResult()) {
            QuerySolution qs = nextSolution();
            RDFNode resultUri = qs.get("ls");
            senses.add((LexicalSense) getEntity(LexicalSense.class, resultUri.toString(), lexicalEntry));
        }
        return senses;
    }
}
