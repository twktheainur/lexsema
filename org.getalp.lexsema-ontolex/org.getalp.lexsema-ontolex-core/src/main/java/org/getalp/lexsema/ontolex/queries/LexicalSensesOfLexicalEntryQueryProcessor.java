package org.getalp.lexsema.ontolex.queries;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.ontolex.*;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This query processor implements a query that retrieves all {@code LexicalSense}s for a
 * given {@code LexicalEntry}.
 */
public final class LexicalSensesOfLexicalEntryQueryProcessor extends AbstractQueryProcessor<LexicalSense> {

    private final LexicalEntry lexicalEntry;
    private final LexicalResourceEntityFactory lexicalResourceEntityFactory;

    public LexicalSensesOfLexicalEntryQueryProcessor(final LexicalResource lexicalResource,
                                                     final LexicalEntry lexicalEntry) {
        super(lexicalResource.getGraph());
        lexicalResourceEntityFactory = lexicalResource.getLexicalResourceEntityFactory();
        this.lexicalEntry = lexicalEntry;
        initialize();
    }

    @Override
    protected void defineQuery() {
        setQuery(new ARQSelectQueryImpl());
        final String resultVar = "ls";
        addTriple(Var.alloc(resultVar),
                getNode("rdf:type"),
                getNode("ontolex:LexicalSense"));
        addTriple(lexicalEntry.getNode(),
                getNode("ontolex:sense"),
                Var.alloc(resultVar));
        addResultVar(resultVar);
    }

    private LexicalResourceEntity getEntity(final Class<? extends LexicalResourceEntity> productType, final String uri, final LexicalResourceEntity parent) {
        return lexicalResourceEntityFactory.getEntity(productType, uri, parent);
    }

    @Override
    public List<LexicalSense> processResults() {
        final List<LexicalSense> senses = new ArrayList<>();
        while (hasNextResult()) {
            final QuerySolution qs = nextSolution();
            final RDFNode resultUri = qs.get("ls");
            senses.add((LexicalSense) getEntity(LexicalSense.class, resultUri.toString(), lexicalEntry));
        }
        return senses;
    }
}
