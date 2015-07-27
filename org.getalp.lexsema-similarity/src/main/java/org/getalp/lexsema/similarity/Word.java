package org.getalp.lexsema.similarity;


import org.getalp.lexsema.ontolex.LexicalEntry;


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

    void loadSenses(Iterable<Sense> senses);

    boolean isNull();
}

