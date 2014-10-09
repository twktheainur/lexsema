package org.getalp.lexsema.lexicalresource.lemon;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.getalp.lexsema.lexicalresource.AbstractLexicalResourceEntity;
import org.getalp.lexsema.lexicalresource.LexicalResource;
import org.getalp.lexsema.lexicalresource.LexicalResourceEntity;
import org.getalp.lexsema.ontology.graph.queries.ARQQuery;
import org.getalp.lexsema.ontology.graph.queries.ARQSelectQuery;
import org.getalp.lexsema.ontology.graph.queries.TripleFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A Lemon LexicalEntry Java Wrapper Class
 */
@Data
@EqualsAndHashCode
public class LexicalEntry extends AbstractLexicalResourceEntity {
    /**
     * --GETTER
     * @return Returns the lemma of the <code>LexicalEntry</code>
     * --SETTER
     * @param lemma Sets the lemma of the <code>LexicalEntry</code>
     */
    private String lemma;
    /**
     * --GETTER
     *
     * @return Returns the part of speech tag of the <code>LexicalEntry</code>
     * --SETTER
     */
    private String partOfSpeech;
    private int number;

    /**
     * Constructor
     *
     * @param r   The lexical resource in which the LexicalEntry is situated
     * @param uri The uri of the LexicalEntry
     */
    public LexicalEntry(LexicalResource r, String uri, LexicalResourceEntity parent, String lemma, String partOfSpeech, int number) {
        super(r, uri, parent);
        this.number = number;
        this.lemma = lemma;
        this.partOfSpeech = partOfSpeech;
    }

    public List<LexicalSense> getSenses() {
        List<LexicalSense> senses = new ArrayList<>();

        TripleFactory tf = new TripleFactory(getOntologyModel());


        ARQQuery q = new ARQSelectQuery();

        q.addToFromStatement(getLexicalResource().getGraph());
        LemonLexicalResource llr = (LemonLexicalResource) getLexicalResource();
        q.addToWhereStatement(tf.isAnyOfType("ls", llr.getLemonURI("LexicalSense")));
        q.addToWhereStatement(tf.isURIRelatedToAny(getURI(), llr.getLemonURI("sense"), "ls"));
        q.addResult("ls");

        ResultSet rs = q.runQuery();

        while (rs.hasNext()) {

            QuerySolution qs = rs.next();
            RDFNode resultUri = qs.get("ls");
            String[] le = String.valueOf(resultUri).split("/");
            senses.add(llr.instanciateLexicalSense(le[le.length - 1], this));
        }

        return senses;
    }
}
