/**
 * 
 */
package org.getalp.dilsmllr.ontology.graph;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import org.getalp.dilsmllr.ontology.OntologyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tchechem
 *
 */
public class Graph {
	private Node uri;
	private OntologyModel model;
	
	public Graph(String uri,OntologyModel model){
		this.uri =NodeFactory.createURI(uri);
        this.model = model;
	}

    public OntologyModel getModel() {
        return model;
    }

    public List<LexicalEntry> getEntry(String lemma){
        List<LexicalEntry> le = new ArrayList<LexicalEntry>();
        return le;
    }

    public Node getUri() {
        return uri;
    }
}
