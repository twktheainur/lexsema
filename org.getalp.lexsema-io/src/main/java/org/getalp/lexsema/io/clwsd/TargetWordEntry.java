package org.getalp.lexsema.io.clwsd;

import edu.stanford.nlp.util.Pair;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Word;

import java.util.Iterator;
import java.util.List;

public interface TargetWordEntry extends Iterable<Pair<Sentence, Integer>> {
    @Override
    Iterator<Pair<Sentence, Integer>> iterator();
    public void addContext(Sentence sentence, int position);
    public Word getTargetWord();
}
