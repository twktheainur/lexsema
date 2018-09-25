package org.getalp.lexsema.ontolex;

public interface LexicalSemanticRelation {
    LexicalResourceEntity getSource();
    LexicalResourceEntity getTarget();
    RelationType getType();

    default double getWeight() {
        return 1;
    }
}

