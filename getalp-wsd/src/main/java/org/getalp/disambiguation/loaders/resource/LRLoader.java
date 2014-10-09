package org.getalp.disambiguation.loaders.resource;

import org.getalp.disambiguation.Sense;
import org.getalp.disambiguation.Word;

import java.util.List;


public interface LRLoader {
    public List<Sense> getSenses(Word w);
    public List<List<Sense>> getAllSenses(List<Word> w);
}
