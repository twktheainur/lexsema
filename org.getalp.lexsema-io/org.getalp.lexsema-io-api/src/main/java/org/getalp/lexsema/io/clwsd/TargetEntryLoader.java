package org.getalp.lexsema.io.clwsd;

import edu.stanford.nlp.util.Pair;
import org.getalp.lexsema.similarity.Text;

import java.util.Iterator;

public interface TargetEntryLoader extends Iterable<Pair<Text, Integer>> {
    TargetWordEntry load();

    @Override
    Iterator<Pair<Text, Integer>> iterator();

    public TargetWordEntry getEntry();
}
