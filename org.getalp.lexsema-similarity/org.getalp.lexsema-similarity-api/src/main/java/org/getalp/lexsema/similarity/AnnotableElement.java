package org.getalp.lexsema.similarity;


import java.io.Serializable;
import java.util.Collection;

public interface AnnotableElement extends Serializable {
    Annotation getAnnotation(int index);

    void addAnnotation(Annotation annotation);

    int annotationCount();

    Collection<Annotation> annotations();

    Collection<Annotation> annotations(String annotationType);
}
