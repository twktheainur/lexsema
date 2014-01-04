package org.getalp.lexsema.lexicalresource.lemon.dbnary;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.getalp.lexsema.lexicalresource.AbstractLexicalResourceEntity;
import org.getalp.lexsema.lexicalresource.lemon.LexicalEntry;
import org.getalp.lexsema.ontology.graph.queries.ARQQuery;
import org.getalp.lexsema.ontology.graph.queries.ARQSelectQuery;
import org.getalp.lexsema.ontology.graph.queries.TripleFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A Dbnary Vocable entry handler
 */
public class Vocable extends AbstractLexicalResourceEntity {

    private String vocable;

    protected Vocable(DBNary r, String uri) {
        super(r, uri);
        getOntologyModel();
    }

    /**
     * Get the vocable associated to the Vocable entry
     *
     * @return The vocable associated to the entry
     */
    public String getVocable() {
        return vocable;
    }

    /**
     * Sets the value of the vocable
     *
     * @param vocable The value of the vocable
     */
    public void setVocable(String vocable) {
        this.vocable = vocable;
    }

    @Override
    public String toString() {
        return "Vocable{'" + vocable + '\'' +
                "In " + getLexicalResource() +
                '}';
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
            entries.add(new LexicalEntry(getLexicalResource(), le[le.length - 1]));

        }

        return entries;
    }
}
