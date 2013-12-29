package org.getalp.lexsema.ontology.graph;

import org.getalp.lexsema.lexicalresource.LexicalResource;

import java.util.List;

/**
 * Interface representing a Node of the resource
 */
public interface Node {
    List<Relation> getRelated();

    LexicalResource getLexicalResource();

    String getURI();
}
