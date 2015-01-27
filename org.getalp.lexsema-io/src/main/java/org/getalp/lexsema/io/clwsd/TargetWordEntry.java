package org.getalp.lexsema.io.clwsd;

import javafx.util.Pair;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Word;

import java.util.Iterator;
import java.util.List;

/**
 * Created by tchechem on 20/01/15.
 */
public interface TargetWordEntry extends Iterable<Pair<List<String>, Integer>> {
    @Override
    Iterator<Pair<List<String>, Integer>> iterator();
    public void addContext(List<String> sentence, int position);
    Word getTargetWord();
}
