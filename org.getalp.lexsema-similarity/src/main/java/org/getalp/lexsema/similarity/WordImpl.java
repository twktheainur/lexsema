package org.getalp.lexsema.similarity;

import com.hp.hpl.jena.graph.Node;
import lombok.EqualsAndHashCode;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.util.Language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@EqualsAndHashCode
public class WordImpl implements Word {
    private final String id;
    private final String surfaceForm;
    private final String textPos;
    private LexicalEntry lexicalEntry;
    private String semanticTag = "";
    private Sentence enclosingSentence = NullSentence.getInstance();
    private final List<Word> precedingNonInstances = Collections.emptyList();
    private String lemma;
    private final int begin;
    private final int end;
    private final List<Sense> senses = Collections.emptyList();

    public WordImpl(String id, String lemma, String surfaceForm, String pos) {
        this.id = id;
        this.lemma = lemma;
        this.surfaceForm = surfaceForm;
        textPos = pos;
        begin=0;
        end=lemma.length();
    }

    public WordImpl(String id, String lemma, String surfaceForm, String pos, int begin, int end) {
        this.id = id;
        this.lemma = lemma;
        this.surfaceForm = surfaceForm;
        textPos = pos;
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
    public Iterator<Sense> iterator() {
        return senses.iterator();
    }

    @Override
    public String getSenseAnnotation() {
        return semanticTag;
    }

    @Override
    public void setSemanticTag(String semanticTag) {
        this.semanticTag = semanticTag;
    }

    @Override
    public int compareTo(LexicalResourceEntity o) {
        final Node node = o.getNode();
        return id.compareTo(node.toString());
    }

    @Override
    public String toString() {
        if(lexicalEntry!=null) {
            return lexicalEntry.toString();
        } else {
            return String.format("Word|%s#%s|",lemma,textPos);
        }
    }
    
    @Override
    public int getBegin() {
    	return begin;
    }
    
    @Override
    public int getEnd() {
    	return end;
    }

    @Override
    public Iterable<Word> precedingNonInstances() {
        return Collections.unmodifiableList(precedingNonInstances);
    }

    @Override
    public void loadSenses(Iterable<Sense> senses) {
        senses.forEach(this.senses::add);
    }

    @Override
    public boolean isNull() {
        return false;
    }
}
