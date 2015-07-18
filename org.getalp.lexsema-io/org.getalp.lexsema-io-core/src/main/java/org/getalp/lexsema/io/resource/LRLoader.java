package org.getalp.lexsema.io.resource;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Word;
import java.util.List;

public interface LRLoader
{
    List<Sense> getSenses(Word w);

    void loadSenses(Document document);

    LRLoader shuffle(boolean shuffle);

    LRLoader extendedSignature(boolean hasExtendedSignature);

    LRLoader loadDefinitions(boolean loadDefinitions);

    LRLoader loadRelated(boolean loadRelated);

    LRLoader stemming(boolean stemming);

    LRLoader filterStopWords(boolean usesStopWords);

    LRLoader index(boolean useIndex);

}
