package org.getalp.lexsema.io.resource;

import org.getalp.lexsema.io.LexicalEntry;
import org.getalp.lexsema.io.Sense;

import java.util.List;


public interface LRLoader {
    public List<Sense> getSenses(LexicalEntry w);

    public List<List<Sense>> getAllSenses(List<LexicalEntry> w);
}
