package org.getalp.lexsema.similarity.annotation;

import org.getalp.lexsema.similarity.Annotation;

public class DefaultAnnotation implements Annotation {

    private final String annotation;
    private final String type;
    private final String source;

    public DefaultAnnotation(String annotation, String type) {
        this(annotation, type, "");
    }

    public DefaultAnnotation(String annotation, String type, String source) {
        this.annotation = annotation;
        this.type = type;
        this.source = source;
    }

    @Override
    public String annotation() {
        return annotation;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String source() {
        return source;
    }
}
