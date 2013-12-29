package org.getalp.lexsema.ontology.graph;

import org.getalp.lexsema.ontology.OntologyModel;
import org.getalp.lexsema.ontology.lemon.LexicalEntry;

import java.util.List;

public interface Graph {
    public OntologyModel getModel();

    public List<LexicalEntry> getEntry(String lemma);

    public com.hp.hpl.jena.graph.Node getUri();
}
