package org.getalp.lexsema.ontolex.queries;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.E_Str;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueString;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.util.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This query processor implements a query that retrieves all {@code LexicalSense}s for a
 * given {@code LexicalEntry}.
 */
public final class LexicalEntriesFromLemmaPosQueryProcessor extends AbstractQueryProcessor<LexicalEntry> {

    private static final String ENTRY_RESULT_VAR = "le";
    private static final String WRITTEN_REP_VAR = "wf";
    private final LexicalResourceEntityFactory lexicalResourceEntityFactory;

    private String lemma = "";
    private String pos = "";
    private final Language language;

    @SuppressWarnings("FeatureEnvy")
    public LexicalEntriesFromLemmaPosQueryProcessor(final LexicalResource lexicalResource,
                                                    final String lemma, final String pos) {
        super(lexicalResource.getGraph());
        lexicalResourceEntityFactory = lexicalResource.getLexicalResourceEntityFactory();
        this.lemma = lemma;
        this.pos = pos;
        language = lexicalResource.getLanguage();
        initialize();
    }

    @Override
    protected void defineQuery() {

        setQuery(new ARQSelectQueryImpl());
        addTriple(Var.alloc(ENTRY_RESULT_VAR),
                getNode("rdf:type"),
                getNode("ontolex:LexicalEntry"));
        final String LEMMA_CF_VAR = "cf";
        addTriple(Var.alloc(ENTRY_RESULT_VAR),
                getNode("ontolex:canonicalForm"),
                Var.alloc(LEMMA_CF_VAR));
        if (language != null) {
            addTriple(Var.alloc(LEMMA_CF_VAR),
                    getNode("ontolex:writtenRep"),
                    NodeFactory.createLiteral(lemma, language.getISO2Code(), null));
        } else {
            addTriple(Var.alloc(LEMMA_CF_VAR),
                    getNode("ontolex:writtenRep"),
                    Var.alloc(WRITTEN_REP_VAR));
            final Expr lemmaRegexMatch = new E_Equals(new E_Str(new ExprVar(WRITTEN_REP_VAR)), new NodeValueString(lemma));
            addFilter(lemmaRegexMatch);
        }

        addTriple(Var.alloc(ENTRY_RESULT_VAR),
                getNode("ontolex:partOfSpeech"),
                getNode(pos));
        addResultVar(ENTRY_RESULT_VAR);
        //addResultVar(WRITTEN_REP_VAR);
    }

    private LexicalEntry getEntity(final String uri, final LexicalResourceEntity parent, final Map<String, String> parameters) {
        return (LexicalEntry) lexicalResourceEntityFactory.getEntity(LexicalEntry.class, uri, parent, parameters);
    }

    @Override
    public List<LexicalEntry> processResults() {
        final List<LexicalEntry> entries = new ArrayList<>();
        while (hasNextResult()) {
            final QuerySolution qs = nextSolution();
            final RDFNode resultUri = qs.get(ENTRY_RESULT_VAR);
            final String[] le = String.valueOf(resultUri).split("/");

            final Map<String, String> properties = new HashMap<>();
            properties.put("canonicalFormWrittenRep", lemma);
            properties.put("partOfSpeech", pos);

            entries.add(getEntity(le[le.length - 1], null, properties));
        }
        return entries;
    }
}
