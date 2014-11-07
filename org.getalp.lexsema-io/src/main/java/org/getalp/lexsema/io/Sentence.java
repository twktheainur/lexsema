package org.getalp.lexsema.io;

public class Sentence extends Document {

    public Sentence(String id) {
        super();
        setId(id);
    }

    @Override
    public String toString() {
        String output = "";
        for (LexicalEntry le : getLexicalEntries()) {
            output += le.getLemma().trim() + " ";
        }
        return output.trim();
    }
}
