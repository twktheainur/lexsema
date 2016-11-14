package org.getalp.lexsema.similarity.annotation;

import org.getalp.lexsema.similarity.Annotation;

class DefaultAnnotation implements Annotation {

    private final String annotation;
    private final String type;
    private final String source;

    DefaultAnnotation(String annotation, String type) {
        this(annotation, type, "");
    }

    DefaultAnnotation(String annotation, String type, String source) {
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

    @Override
    public String toString() {
        return "Annotation{" +
                "annotation='" + annotation + '\'' +
                ", type='" + type + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
