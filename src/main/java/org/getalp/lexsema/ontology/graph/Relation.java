/**
 *
 */
package org.getalp.lexsema.ontology.graph;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.getalp.lexsema.lexicalresource.LexicalResource;
import org.getalp.lexsema.lexicalresource.LexicalResourceEntity;
import org.getalp.lexsema.lexicalresource.lemon.LexicalEntry;
import org.getalp.lexsema.ontology.graph.queries.ARQQuery;
import org.getalp.lexsema.ontology.graph.queries.ARQSelectQuery;
import org.getalp.lexsema.ontology.graph.queries.TripleFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tchechem
 */
public class Relation implements RelationIface {
    private String start;
    private String end;

    protected Relation() {
    }

    public static <S extends LexicalResourceEntity> List<RelationIface> findRelationsForSource(S n, RelationType t) {
        return null;
    }


    public static <E extends LexicalResourceEntity> List<RelationIface> findRelationsForTarget(E n, RelationType t) {
        TripleFactory tf = new TripleFactory(n.getOntologyModel());

        List<LexicalEntry> entries = new ArrayList<>();

        ARQQuery q = new ARQSelectQuery();

        LexicalResource llr = n.getLexicalResource();
        q.addToFromStatement(llr.getGraph());
        q.addToWhereStatement(tf.isAnyRelatedToURI("id", llr.getResourceURI(t.getType().toString()), n.getURI()));

        q.addResult("id");

        ResultSet rs = q.runQuery();
        List<RelationIface> rels = new ArrayList<>();
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            RDFNode rn = qs.get("id");
            Relation rel = new Relation();
            String stStr = rn.toString();
            stStr = stStr.substring(stStr.lastIndexOf("/") + 1, stStr.length());
            String eStr = n.getURI();
            eStr = eStr.substring(stStr.lastIndexOf("/") + 1, stStr.length());
            rel.setEnd(eStr);
            rel.setStart(stStr.substring(stStr.lastIndexOf("/") + 1, stStr.length()));
            rels.add(rel);
        }
        return rels;
    }

    protected void setStart(String start) {
        this.start = start;
    }

    protected void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String getStart() {
        return start;
    }


    @Override
    public String getEnd() {
        return end;
    }
}
