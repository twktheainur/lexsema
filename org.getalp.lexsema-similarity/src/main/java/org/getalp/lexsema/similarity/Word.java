package org.getalp.lexsema.similarity;


import org.getalp.lexsema.ontolex.LexicalEntry;


public interface Word extends LexicalEntry, Iterable<Word> {
    public void addPrecedingInstance(Word precedingNonInstance);

    public Sentence getEnclosingSentence();

    public void setEnclosingSentence(Sentence enclosingSentence);

    public void setLexicalEntry(LexicalEntry le);

    public String getId();

    public String getSurfaceForm();

    public String getSemanticTag();

    public void setSemanticTag(String semanticTag);
}

