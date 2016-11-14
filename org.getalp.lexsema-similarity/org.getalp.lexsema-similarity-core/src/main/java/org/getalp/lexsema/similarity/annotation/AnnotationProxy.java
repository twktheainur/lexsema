package org.getalp.lexsema.similarity.annotation;


import org.getalp.lexsema.similarity.AnnotableElement;
import org.getalp.lexsema.similarity.Annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class AnnotationProxy implements AnnotableElement {

    private final List<Annotation> annotations;

    public AnnotationProxy() {
        annotations = new ArrayList<>();
    }

    @Override
    public Annotation getAnnotation(int index) {
        return annotations.get(index);
    }

    @Override
    public void addAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }

    @Override
    public int annotationCount() {
        return annotations.size();
    }

    @Override
    public Iterable<Annotation> annotations() {
        return Collections.unmodifiableCollection(annotations);
    }

    @Override
    public Iterable<Annotation> annotations(String annotationType) {
        return annotations.stream().filter(annotation -> {
            final String type = annotation.type();
            return type.equals(annotationType);
        }).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder appender = new StringBuilder();
        annotations.forEach(annotation -> appender.append(annotation.toString()).append("\n"));
        return appender.toString();
    }
}
