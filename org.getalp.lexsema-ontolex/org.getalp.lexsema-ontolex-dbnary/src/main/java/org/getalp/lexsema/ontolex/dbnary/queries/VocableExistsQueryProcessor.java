package org.getalp.lexsema.ontolex.dbnary.queries;

import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.queries.ARQSelectQuery;
import org.getalp.lexsema.ontolex.queries.ARQSelectQueryImpl;
import org.getalp.lexsema.ontolex.queries.AbstractQueryProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * This query processor implements a query that retrieves all {@code LexicalSense}s for a
 * given {@code LexicalEntry}.
 */
public final class VocableExistsQueryProcessor extends AbstractQueryProcessor<Vocable> {

    private static final String VOCABLE_TYPE = "v";
    private final LexicalResource lexicalResource;
    private final String vocable;
    private final LexicalResourceEntityFactory lexicalResourceEntityFactory;

    public VocableExistsQueryProcessor(LexicalResource lexicalResource,
                                       String vocable) {
        super(lexicalResource.getGraph());
        this.lexicalResource = lexicalResource;
        this.vocable = vocable;
        lexicalResourceEntityFactory = lexicalResource.getLexicalResourceEntityFactory();
        initialize();
    }

    @Override
    protected final void defineQuery() {
        ARQSelectQuery q = new ARQSelectQueryImpl();
        q.setDistinct(true);
        setQuery(q);

            /*addTriple(getNode(URLEncoder.encode(lexicalResource.getResourceGraphURI() + vocable, "UTF-8")),
                    getNode("rdf:type"),
                    Var.alloc(VOCABLE_TYPE));*/
        addTriple(getNode(String.format("%s%s", lexicalResource.getResourceGraphURI(), vocable)),
                getNode("rdf:type"),
                Var.alloc(VOCABLE_TYPE));

        addResultVar(VOCABLE_TYPE);
    }


    @Override
    public List<Vocable> processResults() {
        List<Vocable> vocables = new ArrayList<>();
        if (hasNextResult()) {
            vocables.add((Vocable) lexicalResourceEntityFactory.getEntity(Vocable.class, vocable, null));
        }
        return vocables;
    }
}
