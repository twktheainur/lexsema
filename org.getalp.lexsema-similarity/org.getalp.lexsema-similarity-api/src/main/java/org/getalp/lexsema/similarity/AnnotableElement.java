package org.getalp.lexsema.similarity;


import java.io.Serializable;

public interface AnnotableElement extends Serializable {
    Annotation getAnnotation(int index);

    void addAnnotation(Annotation annotation);

    int annotationCount();

    Iterable<Annotation> annotations();

    Iterable<Annotation> annotations(String annotationType);
}
