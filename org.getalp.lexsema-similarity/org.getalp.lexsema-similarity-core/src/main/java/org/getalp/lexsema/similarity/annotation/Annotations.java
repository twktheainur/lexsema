package org.getalp.lexsema.similarity.annotation;

import org.getalp.lexsema.similarity.Annotation;

public final class Annotations {

    private Annotations() {
    }

    public static Annotation createAnnotation(String annotation, String type) {
        return new DefaultAnnotation(annotation, type);
    }

    public static Annotation createAnnotation(String annotation, String type, String source) {
        return new DefaultAnnotation(annotation, type, source);
    }

    public static Annotation createNullAnnotation() {
        return new NullAnnotation();
    }
}
