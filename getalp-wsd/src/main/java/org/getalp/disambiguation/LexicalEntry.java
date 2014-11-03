package org.getalp.disambiguation;

import java.util.ArrayList;
import java.util.List;

public class LexicalEntry {
    private String id;
    private String lemma;
    private String surfaceForm;
    private String pos;
    private List<String> precedingNonInstances;

    public LexicalEntry(String id, String lemma, String surfaceForm, String pos) {
        this.id = id;
        this.lemma = lemma;
        this.surfaceForm = surfaceForm;
        this.pos = pos;
        precedingNonInstances = new ArrayList<>();
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

    public List<String> getPrecedingNonInstances() {
        return precedingNonInstances;
    }

    public void setPrecedingNonInstances(List<String> precedingNonInstances) {
        this.precedingNonInstances = precedingNonInstances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LexicalEntry)) return false;

        LexicalEntry that = (LexicalEntry) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "LexicalEntry{" +
                "pos='" + pos + '\'' +
                ", lemma='" + lemma + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
