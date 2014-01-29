package org.getalp.lexsema.lexicalresource.lemon.dbnary;

import org.getalp.lexsema.ontology.uri.DBnaryURICollection;
import lombok.ToString;
import org.getalp.lexsema.lexicalresource.LexicalResourceEntity;
import org.getalp.lexsema.lexicalresource.lemon.LemonLexicalResource;
import org.getalp.lexsema.lexicalresource.lemon.LexicalEntry;
import org.getalp.lexsema.lexicalresource.lemon.LexicalSense;
import org.getalp.lexsema.ontology.OntologyModel;
import org.getalp.lexsema.ontology.graph.queries.ARQQuery;
import org.getalp.lexsema.ontology.graph.queries.ARQSelectQuery;
import org.getalp.lexsema.ontology.uri.URICollection;
import org.getalp.lexsema.util.exceptions.dbnary.NoSuchVocableException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;



/**
 * A handler for the DBNary Lexical Resource
 */
@ToString
public final class DBNary extends LemonLexicalResource {

    private final Locale language;
    private final URICollection dbnaryURI;

    /**
     * Constructor for DBNary
     *
     * @param model    The ontology model where dbnary is stored
     * @param language The language of the dbnary to access
     */
    public DBNary(OntologyModel model, Locale language) {
        super(model, model.getNode("dbnary:").getURI().split("#")[0] + "/" + language.getISO3Language() /*+ "/"*/);
        this.language = language;

        dbnaryURI = new DBnaryURICollection(model);
    }

    /**
     * Check for the existence of a vocable in the ontology model
     *
     * @param vocable the vocable to look for
     * @return <code>true</code> if the vocable exists, <code>false</code> otherwise
     */
    private boolean vocableExists(String vocable) {
        ARQSelectQuery q = new ARQSelectQuery();
        q.setDistinct(true);
        q.addToWhereStatement(getTripleFactory().isURIToAny(getURI() + "/" + vocable, "r", "v"));
        q.addToFromStatement(getGraph());
        q.addResult("v");

        ResultSet rs = q.runQuery();
        return rs.hasNext();
    }

    /**
     * Retrieve the vocable instance that corresponds to the <code>vocable</code> string
     *
     * @param vocable the vocable string for which the instance needs to be retrieved
     * @return The <code>Vocable</code> instance
     * @throws NoSuchVocableException Thrown when the vocable does not exist in the ontology
     */
    public Vocable getVocable(String vocable) throws NoSuchVocableException {
        return getVocable(vocable, true);
    }

    public Vocable getVocable(String vocable, boolean checkExistance) throws NoSuchVocableException {
        if (checkExistance && !vocableExists(vocable)) {
            vocable = vocable.toLowerCase();
            vocable = vocable.substring(0, 1).toUpperCase() + vocable.substring(1);
            if (checkExistance && !vocableExists(vocable)) {
                throw new NoSuchVocableException(vocable, language.getDisplayName());
            }
        }
        System.err.println(vocable);
        return createVocable(vocable, null);
    }

    /**
     * Returns a list of all vocables present in the resource
     *
     * @return a list of all the vocable present in the resource
     */
    public List<Vocable> getVocables() {
        List<Vocable> vocables = new ArrayList<>();

        //Building query
        ARQQuery q = new ARQSelectQuery();
        q.addToFromStatement(getGraph());

        //SELECT ?s ...
        q.addResult("s");

        //WHERE { ?s a dbnary:Vocable.}
        q.addToWhereStatement(getTripleFactory().isAnyOfType("s", dbnaryURI.forName("Vocable")));


        //Running the query and retrieving the results
        ResultSet rs = q.runQuery();
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            if (qs.get("s").isResource()) {
                String s[] = qs.get("s").asResource().getURI().split("/");
                try {

                    //Encapsulating in a <code>Vocable</code> instance
                    vocables.add(getVocable(s[s.length - 1], false));
                } catch (NoSuchVocableException e) {
                    System.err.println(e.getMessage()); //TODO: Add logging call
                }
            }
        }
        return vocables;
    }

    /**
     * Returns the full URI of a dbnary OWL Class or ObjectProperty
     *
     * @param s the un-prefixed name of the Class or ObjectProperty
     * @return
     */
    public String getDBNaryURI(String s) {
        return dbnaryURI.forName(s);
    }

    public Vocable createVocable(String uri, LexicalEntry parent) {
        String vocable;
        if (uri.contains("/")) {
            vocable = uri.split("/")[1];
        } else {
            vocable = uri;
        }
        return new Vocable(this, uri, parent, vocable);
    }

    @Override
    public LexicalEntry createLexicalEntry(String uri, LexicalResourceEntity parent) {
        String[] splitURI;
        String[] canonicalURI;
        if (uri.contains("/")) {
            splitURI = uri.split("/");
            canonicalURI = splitURI[splitURI.length - 1].split("__");
        } else {
            canonicalURI = uri.split("__");
            ;
        }
        return new LexicalEntry(this, uri, parent, canonicalURI[0], canonicalURI[1], Integer.valueOf(canonicalURI[2]));
    }

    @Override
    public LexicalSense createLexicalSense(String uri, LexicalResourceEntity parent) {

        String[] splitUri;
        String canonicalURI;
        if (uri.contains("/")) {
            splitUri = uri.split("/");
            canonicalURI = splitUri[splitUri.length - 1].split("__")[1];
        } else {
            canonicalURI = uri.split("__")[1];
        }
        return new LexicalSense(this, uri, parent, Integer.valueOf(canonicalURI.split("_")[1]));
    }
}
