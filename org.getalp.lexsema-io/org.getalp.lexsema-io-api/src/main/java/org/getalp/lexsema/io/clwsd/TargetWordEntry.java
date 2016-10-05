package org.getalp.lexsema.io.clwsd;

import edu.stanford.nlp.util.Pair;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;

import java.util.Iterator;

public interface TargetWordEntry extends Iterable<Pair<Text, Integer>> {
    @Override
    Iterator<Pair<Text, Integer>> iterator();

    public void addContext(Text sentence, int position);

    public Word getTargetWord();
}
