package org.getalp.disambiguation;

import org.getalp.disambiguation.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private String id;
    private List<LexicalEntry> lexicalEntries;
    private List<List<Sense>> sense;


    public Document() {
        lexicalEntries = new ArrayList<>();
        sense = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<LexicalEntry> getLexicalEntries() {
        return lexicalEntries;
    }

    public List<List<Sense>> getSenses() {
        return sense;
    }

    public LexicalEntry getLexicalEntry(Configuration c, int index) {
        return lexicalEntries.get(index + c.getStart());
    }

    public List<Sense> getSenses(Configuration c, int index) {
        return sense.get(index + c.getStart());
    }

    public void setSenses(List<List<Sense>> sense) {
        this.sense = sense;
    }
}
