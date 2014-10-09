package org.getalp.disambiguation;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private String id;
    private List<Word> words;
    private List<List<Sense>> sense;


    public Document() {
        words = new ArrayList<Word>();
        sense = new ArrayList<List<Sense>>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Word> getWords() {
        return words;
    }

    public List<List<Sense>> getSense() {
        return sense;
    }

    public void setSense(List<List<Sense>> sense) {
        this.sense = sense;
    }
}
