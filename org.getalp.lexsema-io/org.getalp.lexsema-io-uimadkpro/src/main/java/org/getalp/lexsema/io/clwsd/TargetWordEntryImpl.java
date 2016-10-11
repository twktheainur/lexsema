package org.getalp.lexsema.io.clwsd;

import edu.stanford.nlp.util.Pair;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TargetWordEntryImpl implements TargetWordEntry {
    private List<Pair<Text, Integer>> contexts;
    private Word targetWord;

    public TargetWordEntryImpl(Word targetWord) {
        this.targetWord = targetWord;
        contexts = new ArrayList<>();
    }

    @Override
    public Iterator<Pair<Text, Integer>> iterator() {
        return contexts.iterator();
    }

    @Override
    public void addContext(Text sentence, int position) {
        contexts.add(new Pair<>(sentence, position));
    }

    @Override
    public Word getTargetWord() {
        return targetWord;
    }
}
