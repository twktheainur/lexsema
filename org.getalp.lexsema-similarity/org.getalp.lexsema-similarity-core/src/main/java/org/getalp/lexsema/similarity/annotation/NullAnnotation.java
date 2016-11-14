package org.getalp.lexsema.similarity.annotation;

import org.getalp.lexsema.similarity.Annotation;


class NullAnnotation implements Annotation {

    @Override
    public String annotation() {
        return "";
    }

    @Override
    public String type() {
        return "";
    }

    @Override
    public String source() {
        return "";
    }

    @Override
    public boolean isNull() {
        return true;
    }
}
