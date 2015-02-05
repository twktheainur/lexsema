package org.getalp.lexsema.io.resource;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Word;

import java.util.List;


public interface LRLoader {
    public List<Sense> getSenses(Word w);

    public void loadSenses(Document document);

    @SuppressWarnings("BooleanParameter")
    public LRLoader suffle(boolean shuffle);

    @SuppressWarnings("BooleanParameter")
    public LRLoader extendedSignature(boolean hasExtendedSignature);

    public LRLoader loadDefinition(boolean loadDefinitions);
    public LRLoader setLoadRelated(boolean loadRelated);
}
