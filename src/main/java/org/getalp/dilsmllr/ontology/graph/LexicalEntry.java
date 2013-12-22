/**
 *
 */
package org.getalp.dilsmllr.ontology.graph;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import org.getalp.dilsmllr.ontology.storage.VirtuosoTripleStore;

/**
 * @author tchechem
 */
public class LexicalEntry extends Node {


    public LexicalEntry(String uri, Graph g) {
        super(load(uri,g));
        setGraph(g);
    }

    private static final RDFNode load(String uri, Graph g) {
        Triple match = Triple.create(Var.alloc("l"),
                NodeFactory.createURI("rdf:type"),NodeFactory.createURI("lemon:LexicalEntry"));


        Triple match2 = Triple.create(Var.alloc("l"),
                Var.alloc("r"),
                Var.alloc("t"));
        ElementTriplesBlock block = new ElementTriplesBlock(); // Make a BGP
        block.addTriple(match);
        block.addTriple(match2);

        ElementGroup body = new ElementGroup();

        body.addElement(block);


        Query q = QueryFactory.create("SELECT ?s FROM <"+g.getUri().toString()+"> WHERE {?s ?p ?o . ?s a <http://www.lemon-model.net/lemon#LexicalEntry>} LIMIT 10");

        //q.setQueryPattern(body);
        //q.setQuerySelectType();
        //q.addResultVar("l");

        VirtuosoTripleStore vts = VirtuosoTripleStore.getInstance();
        vts.runQuery(q, g.getModel().getModel());


        return null;
    }
}
