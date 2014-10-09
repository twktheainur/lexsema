package org.getalp.disambiguation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tchechem on 9/16/14.
 */
public class Word {
    private String id;
    private String lemma;
    private String surfaceForm;
    private String pos;
    private List<String> preceedingNonInstances;

    public Word(String id, String lemma, String surfaceForm, String pos) {
        this.id = id;
        this.lemma = lemma;
        this.surfaceForm = surfaceForm;
        this.pos = pos;
        preceedingNonInstances = new ArrayList<String>();
    }

    public String getId() {
        return id;
    }

    public String getLemma() {
        return lemma;
    }

    public String getSurfaceForm() {
        return surfaceForm;
    }

    public String getPos() {
        return pos;
    }

    public List<String> getPreceedingNonInstances() {
        return preceedingNonInstances;
    }

    public void setPreceedingNonInstances(List<String> preceedingNonInstances) {
        this.preceedingNonInstances = preceedingNonInstances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Word)) return false;

        Word word = (Word) o;

        if (!id.equals(word.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
