package org.getalp.lexsema.ontolex;

import org.getalp.lexsema.ontolex.graph.Node;

/**
 * Created by tchechem on 11/6/14.
 */
public interface LexicalSense extends Node, LexicalResourceEntity {
    String getDefinition();

    int getSenseNumber();

    void setDefinition(String definition);

    void setSenseNumber(int senseNumber);
}
