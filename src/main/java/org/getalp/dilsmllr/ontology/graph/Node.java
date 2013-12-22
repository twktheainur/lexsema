/**
 * 
 */
package org.getalp.dilsmllr.ontology.graph;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.util.ArrayList;
import java.util.List;


public class Node {
	private RDFNode node;
	private List<Relation> propertyCache;
	private OntClass nodeClass;
    private Graph graph;
	
	public Node(RDFNode n){
		node = n;
	}
	
	private void loadProperties(){
		if(propertyCache ==null){
			propertyCache = new ArrayList<Relation>();
		}
		if(propertyCache.size() == 0){
			
		}
	}
	
	public List<Relation> getRelated(){
		if(propertyCache ==null)
			loadProperties();
		return propertyCache;
	}
	
	private void queryNode(){
		
	}

    public Graph getGraph() {
        return graph;
    }

    protected void setGraph(Graph graph) {
        this.graph = graph;
    }
}
