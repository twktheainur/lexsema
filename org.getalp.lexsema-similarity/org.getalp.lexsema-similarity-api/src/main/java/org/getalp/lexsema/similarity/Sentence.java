package org.getalp.lexsema.similarity;

public interface Sentence extends Document, AnnotableElement {
    Text getParentText();
    void setParentText(Text text);
}
