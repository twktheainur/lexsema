package org.getalp.disambiguation;

import org.getalp.disambiguation.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private String id;
    private List<LexicalEntry> lexicalEntries;
    private List<List<Sense>> senses;


    public Document() {
        lexicalEntries = new ArrayList<>();
        senses = new ArrayList<>();
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
        return senses;
    }

    public LexicalEntry getLexicalEntry(Configuration c, int index) {
        return lexicalEntries.get(index + c.getStart());
    }

    public List<Sense> getSenses(Configuration c, int index) {
        return senses.get(index + c.getStart());
    }

    public List<Sense> getSenses(int index) {
        return senses.get(index);
    }

    public void setSenses(List<List<Sense>> sense) {
        this.senses = sense;
    }
}
