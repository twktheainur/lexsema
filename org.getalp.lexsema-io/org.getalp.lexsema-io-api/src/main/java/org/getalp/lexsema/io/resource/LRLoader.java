package org.getalp.lexsema.io.resource;

import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurus;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Word;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface LRLoader extends Serializable
{
    List<Sense> getSenses(Word w);

    Map<Word,List<Sense>> getAllSenses();

    void loadSenses(Document document);

    LRLoader shuffle(boolean shuffle);

    LRLoader extendedSignature(boolean hasExtendedSignature);

    LRLoader loadDefinitions(boolean loadDefinitions);

    LRLoader loadRelated(boolean loadRelated);

    LRLoader stemming(boolean stemming);

    LRLoader filterStopWords(boolean usesStopWords);

    LRLoader addThesaurus(AnnotatedTextThesaurus thesaurus);

    LRLoader index(boolean useIndex);

    LRLoader distributed(boolean isDistributed);

}
