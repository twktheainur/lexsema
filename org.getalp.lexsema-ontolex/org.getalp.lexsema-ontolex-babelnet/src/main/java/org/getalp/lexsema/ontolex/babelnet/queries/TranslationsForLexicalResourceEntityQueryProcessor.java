package org.getalp.lexsema.ontolex.babelnet.queries;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.babelnet.Translation;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.graph.Graph;
import org.getalp.lexsema.ontolex.queries.ARQSelectQueryImpl;
import org.getalp.lexsema.ontolex.queries.AbstractQueryProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This query processor implements a query that retrieves all <code>LexicalSense</code>s for a
 * given <code>LexicalEntry</code>.
 */
public final class TranslationsForLexicalResourceEntityQueryProcessor extends AbstractQueryProcessor<Translation> {

    private static final String ENTRY_RESULT_VAR = "trans";
    private final LexicalResourceEntity entity;

    LexicalResourceEntityFactory lexicalResourceEntityFactory;
    private Language language;


    public TranslationsForLexicalResourceEntityQueryProcessor(Graph graph,
                                                              LexicalResourceEntityFactory lexicalResourceEntityFactory,
                                                              LexicalResourceEntity entity, Language language) {
        super(graph);
        this.lexicalResourceEntityFactory = lexicalResourceEntityFactory;
        this.entity = entity;
        this.language = language;
        initialize();
    }

    @Override
    protected final void defineQuery() {
        setQuery(new ARQSelectQueryImpl());
        addTriple(Var.alloc(ENTRY_RESULT_VAR),
                getNode("dbnary:isTranslationOf"),
                entity.getNode());
        if (language != null) {
            addTriple(Var.alloc(ENTRY_RESULT_VAR),
                    getNode("dbnary:isTranslationOf"),
                    getNode("lexvo:" + language.getISO3Code()));
        }
        addResultVar(ENTRY_RESULT_VAR);
    }

    private Translation getEntity(String uri, LexicalResourceEntity parent, Map<String, String> parameters) {
        return (Translation) lexicalResourceEntityFactory.getEntity(Translation.class, uri, parent, parameters);
    }

    @Override
    public List<Translation> processResults() {
        List<Translation> entries = new ArrayList<>();
        while (hasNextResult()) {
            QuerySolution qs = nextSolution();
            RDFNode resultUri = qs.get(ENTRY_RESULT_VAR);
            entries.add(getEntity(resultUri.toString(), null, null));
        }
        return entries;
    }
}
