package org.getalp.lexsema.ontology.graph;

import org.getalp.lexsema.lexicalresource.LexicalResourceEntity;

/**
 * Created by tchechem on 28/01/14.
 */
public interface RelationType {
    RelationType getType();
    RelationType getInverseType();
    public String getFilter();
}
