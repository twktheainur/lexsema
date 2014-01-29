package org.getalp.lexsema.lexicalresource.lemon;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import lombok.Data;
import org.getalp.lexsema.lexicalresource.AbstractLexicalResourceEntity;
import org.getalp.lexsema.lexicalresource.LexicalResource;
import org.getalp.lexsema.lexicalresource.LexicalResourceEntity;
import org.getalp.lexsema.ontology.graph.queries.ARQQuery;
import org.getalp.lexsema.ontology.graph.queries.ARQSelectQuery;
import org.getalp.lexsema.ontology.graph.queries.TripleFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a lemon LexicalSense entity
 */
@Data
public class LexicalSense extends AbstractLexicalResourceEntity {

    private String definition;
    private int senseNumber;

    /**
     * Constructor for LexicalSense
     *
     * @param r   the lemon lexical resource
     * @param uri the uri of the LexicalSense
     */
    public LexicalSense(LexicalResource r, String uri, LexicalResourceEntity parent, int senseNumber) {
        super(r, uri, parent);
        this.senseNumber = senseNumber;
    }

    protected void fetchDefinition() {
        TripleFactory tf = new TripleFactory(getOntologyModel());

        List<LexicalEntry> entries = new ArrayList<>();

        ARQQuery q = new ARQSelectQuery();

        q.addToFromStatement(getLexicalResource().getGraph());
        LemonLexicalResource llr = (LemonLexicalResource) getLexicalResource();
        q.addToWhereStatement(tf.isURIRelatedToAny(getURI(), llr.getLemonURI("definition"), "id"));
        q.addToWhereStatement(tf.isAnyRelatedToAny("id", llr.getLemonURI("value"), "d"));


        q.addResult("d");

        ResultSet rs = q.runQuery();

        if (rs.hasNext()) {
            QuerySolution qs = rs.next();
            String def = qs.get("d").toString().split("@")[0];
            definition = def;
        }
    }

    public String getDefinition() {
        if (definition == null || definition.isEmpty()) {
            fetchDefinition();
        }
        return definition;
    }

}
