package org.getalp.lexsema.ontolex;

@SuppressWarnings("unused")
public interface LexicalEntry extends LexicalResourceEntity {
    String getLemma();

    void setLemma(String lemma);

    String getPartOfSpeech();

    void setPartOfSpeech(String partOfSpeech);

    int getNumber();

    void setNumber(int number);
}
