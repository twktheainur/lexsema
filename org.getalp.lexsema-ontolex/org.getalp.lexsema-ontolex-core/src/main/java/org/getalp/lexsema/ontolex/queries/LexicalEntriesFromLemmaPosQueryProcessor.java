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
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.graph.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This query processor implements a query that retrieves all <code>LexicalSense</code>s for a
 * given <code>LexicalEntry</code>.
 */
public final class LexicalEntriesFromLemmaPosQueryProcessor extends AbstractQueryProcessor<LexicalEntry> {

    private static final String ENTRY_RESULT_VAR = "le";
    private static final String WRITTEN_REP_VAR = "wf";
    LexicalResourceEntityFactory lexicalResourceEntityFactory;
    Graph graph;

    private String lemma = "";
    private String pos = "";
    private Language language;

    public LexicalEntriesFromLemmaPosQueryProcessor(Graph graph, Language language,
                                                    LexicalResourceEntityFactory lexicalResourceEntityFactory,
                                                    String lemma, String pos) {
        super(graph);
        this.lexicalResourceEntityFactory = lexicalResourceEntityFactory;
        this.lemma = lemma;
        this.pos = pos;
        this.language = language;
        initialize();
    }

    @Override
    protected final void defineQuery() {

        String LEMMA_CF_VAR = "cf";

        setQuery(new ARQSelectQueryImpl());
        addTriple(Var.alloc(ENTRY_RESULT_VAR),
                getNode("rdf:type"),
                getNode("lemon:LexicalEntry"));
        addTriple(Var.alloc(ENTRY_RESULT_VAR),
                getNode("lemon:canonicalForm"),
                Var.alloc(LEMMA_CF_VAR));
        if (language != null) {
            addTriple(Var.alloc(LEMMA_CF_VAR),
                    getNode("lemon:writtenRep"),
                    NodeFactory.createLiteral(lemma, language.getISO2Code(), null));
        } else {
            addTriple(Var.alloc(LEMMA_CF_VAR),
                    getNode("lemon:writtenRep"),
                    Var.alloc(WRITTEN_REP_VAR));
            Expr lemmaRegexMatch = new E_Equals(new E_Str(new ExprVar(WRITTEN_REP_VAR)), new NodeValueString(lemma));
            addFilter(lemmaRegexMatch);
        }

        addTriple(Var.alloc(ENTRY_RESULT_VAR),
                getNode("lexinfo:partOfSpeech"),
                getNode(pos));
        addResultVar(ENTRY_RESULT_VAR);
        //addResultVar(WRITTEN_REP_VAR);
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
            String[] le = String.valueOf(resultUri).split("/");

            Map<String, String> properties = new HashMap<>();
            properties.put("canonicalFormWrittenRep", lemma);
            properties.put("partOfSpeech", pos);

            entries.add(getEntity(le[le.length - 1], null, properties));
        }
        return entries;
    }
}
