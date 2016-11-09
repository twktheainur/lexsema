package org.getalp.lexsema.similarity;

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
}
