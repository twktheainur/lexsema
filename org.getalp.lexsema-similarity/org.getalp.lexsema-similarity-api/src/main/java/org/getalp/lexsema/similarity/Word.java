package org.getalp.lexsema.similarity;


import org.getalp.lexsema.util.Language;

import java.util.Collection;


public interface Word extends Iterable<Sense>, AnnotableElement {

    String getLemma();

    void setLemma(String lemma);

    String getPartOfSpeech();

    void setPartOfSpeech(String partOfSpeech);

    boolean isNull();

    void addPrecedingInstance(Word precedingNonInstance);

    Sentence getEnclosingSentence();

    void setEnclosingSentence(Sentence enclosingSentence);

    String getId();

    String getSurfaceForm();

    String getSenseAnnotation();

    void setSemanticTag(String semanticTag);
    
    int getBegin();
    
    int getEnd();

    Iterable<Word> precedingNonInstances();

    void loadSenses(Collection<Sense> senses);

    Language getLanguage();
}

