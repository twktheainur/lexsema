package org.getalp.lexsema.ontolex;

import java.util.List;

/**
 * Created by tchechem on 11/6/14.
 */
public interface LexicalEntry extends LexicalResourceEntity {
    List<LexicalSense> getSenses();

    String getLemma();

    String getPartOfSpeech();

    int getNumber();

    void setLemma(String lemma);

    void setPartOfSpeech(String partOfSpeech);

    void setNumber(int number);
}
