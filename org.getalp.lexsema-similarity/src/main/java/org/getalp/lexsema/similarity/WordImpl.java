package org.getalp.lexsema.similarity;

import com.hp.hpl.jena.graph.Node;
import lombok.EqualsAndHashCode;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.util.Language;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EqualsAndHashCode
public class WordImpl implements Word {
    private final String id;
    private final String surfaceForm;
    private final String textPos;
    LexicalEntry lexicalEntry;
    private String lemma;
    private String semanticTag;
    private Sentence enclosingSentence = null;
    private List<Word> precedingNonInstances;
    private int begin = 0;
    private int end = 0;

    public WordImpl(String id, String lemma, String surfaceForm, String pos) {
        this.id = id;
        this.lemma = lemma;
        this.surfaceForm = surfaceForm;
        textPos = pos;
        precedingNonInstances = new ArrayList<>();
    }

    public WordImpl(String id, String lemma, String surfaceForm, String pos, int begin, int end) {
        this.id = id;
        this.lemma = lemma;
        this.surfaceForm = surfaceForm;
        textPos = pos;
        precedingNonInstances = new ArrayList<>();
        this.begin = begin;
        this.end = end;
    }


    @Override
    public void addPrecedingInstance(Word precedingNonInstance) {
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
        if (lexicalEntry != null) {
            lexicalEntry.setLemma(lemma);
        } else {
            this.lemma = lemma;
        }
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
    public LexicalResourceEntity getParent() {
        if (lexicalEntry != null) {
            return lexicalEntry.getParent();
        }
        return null;

    }

    @Override
    public Language getLanguage() {
        if (lexicalEntry == null) {
            return Language.UNSUPPORTED;
        } else {
            return lexicalEntry.getLanguage();
        }
    }

    @Override
    public void setLanguage(Language language) {
        if (lexicalEntry != null) {
            lexicalEntry.setLanguage(language);
        }
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
    public Iterator<Word> iterator() {
        return precedingNonInstances.iterator();
    }

    @Override
    public String getSemanticTag() {
        return semanticTag;
    }

    @Override
    public void setSemanticTag(String semanticTag) {
        this.semanticTag = semanticTag;
    }

    @Override
    public int compareTo(LexicalResourceEntity o) {
        return id.compareTo(o.getNode().toString());
    }

    @Override
    public String toString() {
        return lexicalEntry.toString();
    }
    
    @Override
    public int getBegin() {
    	return begin;
    }
    
    @Override
    public int getEnd() {
    	return end;
    }
}
