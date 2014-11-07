package org.getalp.lexsema.ontolex.graph;

/**
 * Created by tchechem on 28/01/14.
 */
public interface RelationType {
    RelationType getType();

    RelationType getInverseType();

    public String getFilter();
}
