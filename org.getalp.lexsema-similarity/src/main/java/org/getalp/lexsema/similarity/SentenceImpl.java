package org.getalp.lexsema.similarity;

public class SentenceImpl extends DocumentImpl implements Sentence {

    public SentenceImpl(String id) {
        super();
        setId(id);
    }

    @Override
    public String toString() {
        String output = "";
        for (Word le : this) {
            output += le.getLemma().trim() + " ";
        }
        return output.trim();
    }
}
