package org.getalp.lexsema.ontolex.dbnary.queries;

import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.graph.Graph;
import org.getalp.lexsema.ontolex.queries.ARQSelectQuery;
import org.getalp.lexsema.ontolex.queries.ARQSelectQueryImpl;
import org.getalp.lexsema.ontolex.queries.AbstractQueryProcessor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * This query processor implements a query that retrieves all <code>LexicalSense</code>s for a
 * given <code>LexicalEntry</code>.
 */
public final class VocableExistsQueryProcessor extends AbstractQueryProcessor<Vocable> {

    private final static String VOCABLE_TYPE = "v";
    private final LexicalResource lexicalResource;
    private final String vocable;
    private final LexicalResourceEntityFactory lexicalResourceEntityFactory;

    public VocableExistsQueryProcessor(Graph graph,
                                       LexicalResource lexicalResource,
                                       LexicalResourceEntityFactory lexicalResourceEntityFactory,
                                       String vocable) {
        super(graph);
        this.lexicalResource = lexicalResource;
        this.vocable = vocable;
        this.lexicalResourceEntityFactory = lexicalResourceEntityFactory;
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
        addTriple(getNode(lexicalResource.getResourceGraphURI() + vocable),
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
