package org.getalp.lexsema.ontolex;


public interface RelationType {
    String getFilter();

    String getPrefix();

    RelationType getType();

    RelationType getInverseType();
}
