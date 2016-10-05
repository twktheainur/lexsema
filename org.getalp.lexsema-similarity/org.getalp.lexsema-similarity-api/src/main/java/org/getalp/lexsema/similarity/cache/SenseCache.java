package org.getalp.lexsema.similarity.cache;

import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Word;

import java.util.List;

/**
 * Created by tchechem on 19/01/15.
 */
public interface SenseCache {
    List<Sense> getSenses(Word w);

    void addToCache(Word w, List<Sense> ls);
}
