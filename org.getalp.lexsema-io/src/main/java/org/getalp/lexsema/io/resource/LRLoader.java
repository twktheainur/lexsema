package org.getalp.lexsema.io.resource;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Word;

import java.util.List;


public interface LRLoader {
    public List<Sense> getSenses(Word w);

    public List<List<Sense>> getAllSenses(List<Word> w);

    public void loadSenses(Document document);
}
