package org.getalp.lexsema.ontolex.dbnary.queries;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.queries.ARQSelectQueryImpl;
import org.getalp.lexsema.ontolex.queries.AbstractQueryProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This query processor implements a query that retrieves all {@code LexicalSense}s for a
 * given {@code LexicalEntry}.
 */
public final class LexicalEntriesForVocableQueryProcessor extends AbstractQueryProcessor<LexicalEntry> {

    private static final String ENTRY_RESULT_VAR = "le";
    private final Vocable vocable;

    LexicalResourceEntityFactory lexicalResourceEntityFactory;


    public LexicalEntriesForVocableQueryProcessor(LexicalResource lexicalResource,
                                                  Vocable vocable) {
        super(lexicalResource.getGraph());
        lexicalResourceEntityFactory = lexicalResource.getLexicalResourceEntityFactory();
        this.vocable = vocable;
        initialize();
    }

    @Override
    protected final void defineQuery() {
        setQuery(new ARQSelectQueryImpl());
        addTriple(Var.alloc(ENTRY_RESULT_VAR),
                getNode("rdf:type"),
                getNode("lemon:LexicalEntry"));
        addTriple(vocable.getNode(),
                getNode("dbnary:refersTo"),
                Var.alloc(ENTRY_RESULT_VAR));
        addResultVar(ENTRY_RESULT_VAR);
    }

    private LexicalEntry getEntity(String uri, LexicalResourceEntity parent, Map<String, String> parameters) {
        return (LexicalEntry) lexicalResourceEntityFactory.getEntity(LexicalEntry.class, uri, parent, parameters);
    }

    @Override
    public List<LexicalEntry> processResults() {
        List<LexicalEntry> entries = new ArrayList<>();
        while (hasNextResult()) {
            QuerySolution qs = nextSolution();
            RDFNode resultUri = qs.get(ENTRY_RESULT_VAR);
            entries.add(getEntity(resultUri.toString(), vocable, null));
        }
        return entries;
    }
}
