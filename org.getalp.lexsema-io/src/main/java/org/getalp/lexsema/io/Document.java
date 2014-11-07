package org.getalp.lexsema.io;


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

    public LexicalEntry getLexicalEntry(int offset, int index) {
        return lexicalEntries.get(index + offset);
    }

    public List<Sense> getSenses(int offset, int index) {
        return senses.get(index + offset);
    }

    public List<Sense> getSenses(int index) {
        return senses.get(index);
    }

    public void setSenses(List<List<Sense>> sense) {
        this.senses = sense;
    }
}
