package org.getalp.lexsema.dbnary;


import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import lombok.Data;
import lombok.ToString;
import org.getalp.ontolex.api.LexicalEntry;
import org.getalp.ontolex.api.LexicalResourceEntity;
import org.getalp.ontolex.api.graph.queries.ARQQuery;
import org.getalp.ontolexapi.core.AbstractLexicalResourceEntity;
import org.getalp.ontolexapi.core.graph.queries.ARQSelectQuery;
import org.getalp.ontolexapi.core.graph.queries.TripleFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A Dbnary Vocable entry handler
 */
@Data
@ToString(callSuper = true)
public class Vocable extends AbstractLexicalResourceEntity {

    /**
     * --GETTER
     *
     * @return The vocable associated to the entry
     * --SETTER
     * @param vocable The value of the vocable
     */
    private String vocable;

    protected Vocable(DBNary r, String uri, LexicalResourceEntity parent, String vocable) {
        super(r, uri, parent);
        getOntologyModel();
        this.vocable = vocable;
    }


    /**
     * Returns the list of LexicalEntries associated (dbnary:relatedTo) with the Vocable
     *
     * @return the list of LexicalEntries associated (dbnary:relatedTo) with the Vocable
     */
    public List<LexicalEntry> getLexicalEntries() {

        TripleFactory tf = new TripleFactory(getOntologyModel());

        List<LexicalEntry> entries = new ArrayList<>();

        ARQQuery q = new ARQSelectQuery();

        q.addToFromStatement(getLexicalResource().getGraph());
        DBNary llr = (DBNary) getLexicalResource();
        q.addToWhereStatement(tf.isAnyOfType("le", llr.getLemonURI("LexicalEntry")));
        q.addToWhereStatement(tf.isURIRelatedToAny(getURI(), llr.getDBNaryURI("refersTo"), "le"));
        q.addResult("le");

        ResultSet rs = q.runQuery();

        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            String[] le = String.valueOf(qs.get("le")).split("/");

            entries.add(getLexicalResource().instanciateLexicalEntry(le[le.length - 1], this));

        }

        return entries;
    }
}
