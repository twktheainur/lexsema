package org.getalp.lexsema.ontolex.dbnary.queries;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.ontolex.graph.Graph;
import org.getalp.lexsema.ontolex.queries.ARQQuery;
import org.getalp.lexsema.ontolex.queries.ARQSelectQueryImpl;
import org.getalp.lexsema.ontolex.queries.AbstractQueryProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This query processor implements a query that retrieves all <code>LexicalSense</code>s for a
 * given <code>LexicalEntry</code>.
 */
public final class TranslationPropertiesQueryProcessor extends AbstractQueryProcessor<Map<String, String>> {

    private final static String GLOSS_RESULT = "gloss";
    private final static String TRANSLATION_NUMBER_RESULT = "translationNumber";
    private final static String WRITTEN_FORM_RESULT = "writtenForm";
    private final static String TARGET_LANGUAGE_RESULT = "targetLanguage";
    private final static String TARGET_LANGUAGE_CODE_RESULT = "targetLanguageCode";
    private final Node translation;

    public TranslationPropertiesQueryProcessor(Graph graph,
                                               Node translation) {
        super(graph);
        this.translation = translation;
        initialize();
    }

    @Override
    protected final void defineQuery() {
        ARQQuery q = new ARQSelectQueryImpl();
        setQuery(q);
        addOptionalTriple(translation,
                getNode("dbnary:gloss"),
                Var.alloc(GLOSS_RESULT));
        addOptionalTriple(translation,
                getNode("dbnary:translationNumber"),
                Var.alloc(TRANSLATION_NUMBER_RESULT));

        addTriple(translation,
                getNode("dbnary:writtenForm"),
                Var.alloc(WRITTEN_FORM_RESULT));

        /*                      TODO: Remove this when the bug is fixed in dbnary (bug #475)
         * Normally all translation have a targetLanguage property, however due to a bug in the dbnary extractors as of
         * 19/01/2015, the Russian, Portuguese, Spanish and Polish editions contain the targetLanguageCode property
         * instead
         */
        addOptionalTriple(translation,
                getNode("dbnary:targetLanguage"),
                Var.alloc(TARGET_LANGUAGE_RESULT));

        addOptionalTriple(translation,
                getNode("dbnary:targetLanguageCode"),
                Var.alloc(TARGET_LANGUAGE_CODE_RESULT));

        addResultVar(GLOSS_RESULT);
        addResultVar(TRANSLATION_NUMBER_RESULT);
        addResultVar(WRITTEN_FORM_RESULT);
        addResultVar(TARGET_LANGUAGE_RESULT);
        addResultVar(TARGET_LANGUAGE_CODE_RESULT);
    }


    @Override
    public List<Map<String, String>> processResults() {
        List<Map<String, String>> vocables = new ArrayList<>();
        while (hasNextResult()) {
            QuerySolution qs = nextSolution();
            Map<String, String> properties = new HashMap<>();

            RDFNode gloss = qs.get(GLOSS_RESULT);
            if (gloss != null) {
                properties.put(GLOSS_RESULT, gloss.toString());
            }
            RDFNode translationNumber = qs.get(TRANSLATION_NUMBER_RESULT);
            if (translationNumber != null) {
                properties.put(TRANSLATION_NUMBER_RESULT, translationNumber.toString().split("\\^\\^")[0]);
            }
            properties.put(WRITTEN_FORM_RESULT, qs.get(WRITTEN_FORM_RESULT).toString());
            RDFNode targetLanguage = qs.get(TARGET_LANGUAGE_RESULT);
            if (targetLanguage == null) {
                targetLanguage = qs.get(TARGET_LANGUAGE_CODE_RESULT);
            }
            if (targetLanguage != null) {
                properties.put(TARGET_LANGUAGE_RESULT, targetLanguage.toString());
            }
            vocables.add(properties);
        }
        return vocables;
    }

}
