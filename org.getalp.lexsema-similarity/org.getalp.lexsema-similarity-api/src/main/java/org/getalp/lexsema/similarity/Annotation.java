package org.getalp.lexsema.similarity;


import java.io.Serializable;

public interface Annotation extends Serializable {
    String annotation();

    String type();

    String source();

    default boolean isNull() {
        return false;
    }
}
