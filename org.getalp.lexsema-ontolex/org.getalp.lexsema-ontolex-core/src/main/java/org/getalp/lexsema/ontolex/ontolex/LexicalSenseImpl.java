package org.getalp.lexsema.ontolex.ontolex;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import lombok.Data;
import org.getalp.lexsema.ontolex.AbstractLexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.OntolexLexicalResource;
import org.getalp.lexsema.ontolex.graph.queries.ARQQuery;
import org.getalp.lexsema.ontolex.graph.queries.ARQSelectQuery;
import org.getalp.lexsema.ontolex.graph.queries.TripleFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a ontolex LexicalSense entity
 */
@Data
public class LexicalSenseImpl extends AbstractLexicalResourceEntity implements LexicalSense {

    private String definition;
    private int senseNumber;

    /**
     * Constructor for LexicalSense
     *
     * @param r   the ontolex lexical resource
     * @param uri the uri of the LexicalSense
     */
    public LexicalSenseImpl(LexicalResource r, String uri, LexicalResourceEntity parent, int senseNumber) {
        super(r, uri, parent);
        this.senseNumber = senseNumber;
    }

    protected void fetchDefinition() {
        TripleFactory tf = new TripleFactory(getOntologyModel());

        List<LexicalEntry> entries = new ArrayList<>();

        ARQQuery q = new ARQSelectQuery();

        q.addToFromStatement(getLexicalResource().getGraph());
        OntolexLexicalResource llr = (OntolexLexicalResource) getLexicalResource();
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

    @Override
    public String getDefinition() {
        if (definition == null || definition.isEmpty()) {
            fetchDefinition();
        }
        return definition;
    }

}
