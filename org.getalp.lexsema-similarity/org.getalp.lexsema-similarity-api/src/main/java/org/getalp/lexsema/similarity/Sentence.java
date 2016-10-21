package org.getalp.lexsema.similarity;

public interface Sentence extends Document {
    Text getParentText();
    void setParentText(Text text);
}
