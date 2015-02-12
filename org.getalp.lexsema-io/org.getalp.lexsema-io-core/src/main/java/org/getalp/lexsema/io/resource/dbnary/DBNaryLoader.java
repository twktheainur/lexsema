package org.getalp.lexsema.io.resource.dbnary;

import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Word;

import java.util.List;

/**
 * Created by tchechem on 07/02/15.
 */
public interface DBNaryLoader extends LRLoader {
    LexicalEntry retrieveLexicalEntryForWord(Word targetWord);

    @Override
    List<Sense> getSenses(Word w);

    @Override
    void loadSenses(Document document);

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
    LRLoader setLoadRelated(boolean loadRelated);

    @SuppressWarnings("BooleanParameter")
    @Override
    LRLoader setStemming(boolean stemming);

    @SuppressWarnings("BooleanParameter")
    @Override
    LRLoader setUsesStopWords(boolean usesStopWords);
}
