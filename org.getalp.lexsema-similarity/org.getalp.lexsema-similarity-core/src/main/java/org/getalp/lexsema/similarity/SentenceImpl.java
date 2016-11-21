package org.getalp.lexsema.similarity;

import org.getalp.lexsema.similarity.annotation.AnnotationProxy;
import org.getalp.lexsema.util.Language;


class SentenceImpl extends DocumentImpl implements Sentence {

    private Text parentText;

    SentenceImpl(String id) {
        super();
        setId(id);
    }

    SentenceImpl(String id, Language language) {
        super(language);
        setId(id);
    }

    @Override
    public boolean isNull() {
        super.isNull();
        return false;
    }


    @Override
    public Text getParentText() {
        return parentText;
    }

    @Override
    public void setParentText(Text text) {
        parentText = text;
    }

    private final AnnotableElement annotationProxy = new AnnotationProxy();

    @Override
    public Annotation getAnnotation(int index) {
        return annotationProxy.getAnnotation(index);
    }

    @Override
    public void addAnnotation(Annotation annotation) {
        annotationProxy.addAnnotation(annotation);
    }

    @Override
    public int annotationCount() {
        return annotationProxy.annotationCount();
    }

    @Override
    public Iterable<Annotation> annotations() {
        return annotationProxy.annotations();
    }

    @Override
    public Iterable<Annotation> annotations(String annotationType) {
        return annotationProxy.annotations(annotationType);
    }
}
