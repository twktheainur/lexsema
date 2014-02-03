package org.getalp.lexsema.lexicalresource.lemon.dbnary;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.getalp.lexsema.lexicalresource.AbstractLexicalResourceEntity;
import org.getalp.lexsema.lexicalresource.LexicalResource;
import org.getalp.lexsema.lexicalresource.LexicalResourceEntity;
import org.getalp.lexsema.lexicalresource.lemon.LexicalEntry;
import org.getalp.lexsema.ontology.graph.queries.ARQQuery;
import org.getalp.lexsema.ontology.graph.queries.ARQSelectQuery;
import org.getalp.lexsema.ontology.graph.queries.TripleFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tchechem on 29/01/14.
 */
public class Translation extends AbstractLexicalResourceEntity {

    private String gloss;
    private Integer translationNumber;

    /**
     * Constructor for a DBNary Translation
     *
     * @param r      The lexical resource to which the entity belongs
     * @param uri    The uri of the entity
     * @param parent
     */
    public Translation(LexicalResource r, String uri, LexicalResourceEntity parent) {
        super(r, uri, parent);
    }

    protected void fetchProperties() {
        TripleFactory tf = new TripleFactory(getOntologyModel());

        List<LexicalEntry> entries = new ArrayList<>();

        ARQQuery q = new ARQSelectQuery();

        q.addToFromStatement(getLexicalResource().getGraph());
        LexicalResource lr = getLexicalResource();
        q.addToWhereStatement(tf.isURIRelatedToAny(getURI(), lr.getResourceURI("gloss"), "d"));
        q.addToWhereStatement(tf.isURIRelatedToAny(getURI(), lr.getResourceURI("translationNumber"), "n"));

        q.addResult("d");
        q.addResult("n");

        ResultSet rs = q.runQuery();

        if (rs.hasNext()) {
            QuerySolution qs = rs.next();
            gloss = qs.get("d").toString();
            String num = qs.get("n").toString().split("\\^\\^")[0];
            translationNumber = Integer.valueOf(num);
        }
    }

    public String getGloss() {
        if (gloss == null) {
            fetchProperties();
        }
        return gloss;
    }

    public Integer getTranslationNumber() {
        if (translationNumber == null) {
            fetchProperties();
        }
        return translationNumber;
    }
}
