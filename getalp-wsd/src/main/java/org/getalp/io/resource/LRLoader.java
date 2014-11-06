package org.getalp.io.resource;

import org.getalp.io.LexicalEntry;
import org.getalp.io.Sense;

import java.util.List;


public interface LRLoader {
    public List<Sense> getSenses(LexicalEntry w);

    public List<List<Sense>> getAllSenses(List<LexicalEntry> w);
}
