package org.getalp.lexsema.io.clwsd;

import javafx.util.Pair;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Word;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TargetWordEntryImpl implements TargetWordEntry {
    private List<Pair<List<String>,Integer>> contexts;
    private Word targetWord;

    public TargetWordEntryImpl(Word targetWord) {
        this.targetWord = targetWord;
        contexts = new ArrayList<>();
    }

    @Override
    public Iterator<Pair<List<String>, Integer>> iterator() {
        return contexts.iterator();
    }

    @Override
    public void addContext(List<String> sentence, int position){
        contexts.add(new Pair<List<String>, Integer>(sentence,position));
    }

    @Override
    public Word getTargetWord() {
        return targetWord;
    }
}
