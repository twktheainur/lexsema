package org.getalp.lexsema.ontology.graph.queries;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.ontology.OntologyModel;
import org.getalp.lexsema.ontology.uri.RDFURICollection;

/**
 * A factory for triples that can be used as cunditions in a WHERE statement
 */
public final class TripleFactory {
    private final RDFURICollection rdfURI;

    /**
     * Constructor
     *
     * @param model The ontology model in which the triples will be used
     */
    public TripleFactory(OntologyModel model) {

        rdfURI = new RDFURICollection(model);
    }

    /**
     * Statement of the form: ?variable a <classURI>
     *
     * @param variable the variable name
     * @param classURI the type URI
     * @return The corresponding triple
     */
    public Triple isAnyOfType(String variable, String classURI) {
        return Triple.create(
                Var.alloc(variable),
                NodeFactory.createURI(rdfURI.forName("type")),
                NodeFactory.createURI(classURI));
    }

    /**
     * Statement of the form: <uri>  a <classURI>
     *
     * @param uri      the subject URI
     * @param classURI the type URI
     * @return The corresponding triple
     */
    public Triple isOfType(String uri, String classURI) {
        return Triple.create(
                NodeFactory.createURI(uri),
                NodeFactory.createURI(rdfURI.forName("type")),
                NodeFactory.createURI(classURI));
    }


    /**
     * Statement of the form: ?subject ?relation ?object
     *
     * @param subject  the subject variable name
     * @param relation the      relation variable name
     * @param object   the object variable name
     * @return The corresponding triple
     */
    public Triple isAny(String subject, String relation, String object) {
        return Triple.create(
                Var.alloc(subject),
                Var.alloc(relation),
                Var.alloc(object));
    }

    public Triple isAnyToURI(String subject, String relation, String uri) {
        return Triple.create(
                Var.alloc(subject),
                Var.alloc(relation),
                NodeFactory.createURI(uri));
    }

    public Triple isURIToAny(String uri, String relation, String object) {
        return Triple.create(
                NodeFactory.createURI(uri),
                Var.alloc(relation),
                Var.alloc(object));
    }


    public Triple isAnyRelatedToAny(String subject, String relation, String object) {
        return Triple.create(
                Var.alloc(subject),
                NodeFactory.createURI(relation),
                Var.alloc(object));
    }

    public Triple isAnyRelatedToURI(String subject, String relationURI, String objectURI) {
        return Triple.create(
                Var.alloc(subject),
                NodeFactory.createURI(relationURI),
                NodeFactory.createURI(objectURI));
    }

    public Triple isURIRelatedToAny(String subjectURI, String relationURI, String object) {
        return Triple.create(
                NodeFactory.createURI(subjectURI),
                NodeFactory.createURI(relationURI),
                Var.alloc(object));
    }


}
