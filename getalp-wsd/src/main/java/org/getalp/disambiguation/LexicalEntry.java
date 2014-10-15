package org.getalp.disambiguation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tchechem on 9/16/14.
 */
public class LexicalEntry {
    private String id;
    private String lemma;
    private String surfaceForm;
    private String pos;
    private List<String> preceedingNonInstances;

    public LexicalEntry(String id, String lemma, String surfaceForm, String pos) {
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
        if (!(o instanceof LexicalEntry)) return false;

        LexicalEntry lexicalEntry = (LexicalEntry) o;

        if (!id.equals(lexicalEntry.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
