package org.getalp.lexsema.ontolex.dbnary.queries;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.dbnary.Translation;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.Graph;
import org.getalp.lexsema.ontolex.queries.ARQSelectQueryImpl;
import org.getalp.lexsema.ontolex.queries.AbstractQueryProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This query processor implements a query that retrieves all {@code LexicalSense}s for a
 * given {@code LexicalEntry}.
 */
public final class TranslationsForLexicalResourceEntityQueryProcessor extends AbstractQueryProcessor<Translation> {

    private static final String ENTRY_RESULT_VAR = "trans";
    private final LexicalResourceEntity entity;

    LexicalResourceEntityFactory lexicalResourceEntityFactory;
    private final Collection<Language> languages = new ArrayList<>();


    public TranslationsForLexicalResourceEntityQueryProcessor(LexicalResource lexicalResource,
                                                              LexicalResourceEntity entity, Language language) {
        super(lexicalResource.getGraph());
        lexicalResourceEntityFactory = lexicalResource.getLexicalResourceEntityFactory();
        this.entity = entity;
        languages.add(language);
        initialize();
    }

    public TranslationsForLexicalResourceEntityQueryProcessor(LexicalResource lexicalResource,
                                                              LexicalResourceEntity entity, Language... languages) {
        super(lexicalResource.getGraph());
        lexicalResourceEntityFactory = lexicalResource.getLexicalResourceEntityFactory();
        this.entity = entity;
        if (languages != null) {
            for (Language language : languages) {
                if (language != null) {
                    this.languages.add(language);
                }
            }
        }
        initialize();
    }

    @Override
    protected final void defineQuery() {
        setQuery(new ARQSelectQueryImpl());
        addTriple(Var.alloc(ENTRY_RESULT_VAR),
                getNode("dbnary:isTranslationOf"),
                entity.getNode());
        if (languages != null) {
            for (Language lang : languages) {
                addOptionalTriple(Var.alloc(ENTRY_RESULT_VAR),
                        getNode("dbnary:targetLanguage"),
                        getNode(String.format("lexvo:%s", lang.getISO3Code())));
            }
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
