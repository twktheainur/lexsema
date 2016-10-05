package org.getalp.lexsema.similarity;

public class SentenceImpl extends DocumentImpl implements Sentence {

    public SentenceImpl(String id) {
        super();
        setId(id);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Word le : this) {
            final String lemma = le.getLemma();
            output.append(lemma.trim());
            output.append(" ");
        }
        final String s = output.toString();
        return s.trim();
    }

    @Override
    public boolean isNull() {
        return false;
    }

}
