package org.getalp.lexsema.io.resource.dbnary;

import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Word;

public interface DBNaryLoader extends LRLoader {
    LexicalEntry retrieveLexicalEntryForWord(Word targetWord);

    @SuppressWarnings("BooleanParameter")
    @Override
    LRLoader shuffle(boolean shuffle);

    @SuppressWarnings("BooleanParameter")
    @Override
    LRLoader extendedSignature(boolean hasExtendedSignature);

    @SuppressWarnings("BooleanParameter")
    @Override
    LRLoader loadDefinitions(boolean loadDefinitions);

    @SuppressWarnings("BooleanParameter")
    @Override
    LRLoader loadRelated(boolean loadRelated);

    @SuppressWarnings("BooleanParameter")
    @Override
    LRLoader stemming(boolean stemming);

    @SuppressWarnings("BooleanParameter")
    @Override
    LRLoader filterStopWords(boolean usesStopWords);
}
