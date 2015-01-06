package org.getalp.lexsema.similarity;

import com.hp.hpl.jena.graph.Node;
import lombok.EqualsAndHashCode;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.graph.OntologyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EqualsAndHashCode
public class WordImpl implements Word {
    private final String id;
    private final String lemma;
    private final String surfaceForm;
    private final String textPos;
    LexicalEntry lexicalEntry;
    private String semanticTag;
    private Sentence enclosingSentence = null;
    private List<String> precedingNonInstances;

    public WordImpl(String id, String lemma, String surfaceForm, String pos) {
        this.id = id;
        this.lemma = lemma;
        this.surfaceForm = surfaceForm;
        textPos = pos;
        precedingNonInstances = new ArrayList<>();
    }


    @Override
    public void addPrecedingInstance(String precedingNonInstance) {
        precedingNonInstances.add(precedingNonInstance);
    }

    @Override
    public Sentence getEnclosingSentence() {
        return enclosingSentence;
    }

    @Override
    public void setEnclosingSentence(Sentence enclosingSentence) {
        this.enclosingSentence = enclosingSentence;
    }

    @Override
    public void setLexicalEntry(LexicalEntry le) {
        lexicalEntry = le;
    }

    @Override
    public String getLemma() {
        if (lexicalEntry == null) {
            return lemma;
        } else {
            return lexicalEntry.getLemma();
        }
    }

    @Override
    public void setLemma(String lemma) {
        lexicalEntry.setLemma(lemma);
    }

    @Override
    public String getPartOfSpeech() {
        if (lexicalEntry == null) {
            return textPos;
        } else {
            return lexicalEntry.getPartOfSpeech();
        }
    }

    @Override
    public void setPartOfSpeech(String partOfSpeech) {
        lexicalEntry.setPartOfSpeech(partOfSpeech);
    }

    @Override
    public int getNumber() {
        return lexicalEntry.getNumber();
    }

    @Override
    public void setNumber(int number) {
        lexicalEntry.setNumber(number);
    }

    @Override
    public LexicalResource getLexicalResource() {
        return lexicalEntry.getLexicalResource();
    }

    @Override
    public OntologyModel getOntologyModel() {
        return lexicalEntry.getOntologyModel();
    }

    @Override
    public Node getNode() {
        return lexicalEntry.getNode();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getSurfaceForm() {
        return surfaceForm;
    }

    @Override
    public Iterator<String> iterator() {
        return precedingNonInstances.iterator();
    }

    public String getSemanticTag() {
        return semanticTag;
    }

    public void setSemanticTag(String semanticTag) {
        this.semanticTag = semanticTag;
    }
}
