package org.getalp.disambiguation.loaders.resource;

import org.getalp.disambiguation.LexicalEntry;
import org.getalp.disambiguation.Sense;

import java.util.List;


public interface LRLoader {
    public List<Sense> getSenses(LexicalEntry w);

    public List<List<Sense>> getAllSenses(List<LexicalEntry> w);
}
