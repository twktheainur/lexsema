package org.getalp.lexsema.similarity;


import org.getalp.lexsema.ontolex.LexicalEntry;

import java.util.Collection;


public interface Word extends LexicalEntry, Iterable<Sense> {
    void addPrecedingInstance(Word precedingNonInstance);

    Sentence getEnclosingSentence();

    void setEnclosingSentence(Sentence enclosingSentence);

    void setLexicalEntry(LexicalEntry le);

    String getId();

    String getSurfaceForm();

    String getSenseAnnotation();

    void setSemanticTag(String semanticTag);
    
    int getBegin();
    
    int getEnd();

    Iterable<Word> precedingNonInstances();

    void loadSenses(Collection<Sense> senses);

    boolean isNull();
}

