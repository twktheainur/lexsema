package org.getalp.lexsema.io.clwsd;

import edu.stanford.nlp.util.Pair;
import org.getalp.lexsema.similarity.Sentence;

import java.util.Iterator;

public interface TargetEntryLoader extends Iterable<Pair<Sentence, Integer>> {
    TargetWordEntry load();

    @Override
    Iterator<Pair<Sentence, Integer>> iterator();

    public TargetWordEntry getEntry();
}
