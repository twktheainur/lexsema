package org.getalp.lexsema.similarity;


import org.getalp.lexsema.util.Language;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class NullSentence implements Sentence{

    private static final Sentence instance = new NullSentence();

    public static Sentence getInstance() {
        return instance;
    }

    private NullSentence() {
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public void setId(String id) {
    }

    @Override
    public Word getWord(int offset, int index) {
        return null;
    }

    @Override
    public Word getWord(int index) {
        return NullWord.getInstance();
    }

    @Override
    public void addWord(Word word) {
    }

    @Override
    public void addWordSenses(Iterable<Sense> senses) {
    }

    @Override
    public void addWords(Iterable<Word> words) {
    }

    @Override
    public void addWordsSenses(Iterable<Iterable<Sense>> senses) {
    }

    @Override
    public List<Sense> getSenses(int offset, int index) {
        return Collections.emptyList();
    }

    @Override
    public List<Sense> getSenses(int index) {
        return Collections.emptyList();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int indexOfWord(Word word) {
        return 0;
    }

    @Override
    public Language getLanguage() {
        return null;
    }

    @Override
    public void setLanguage(Language language) {

    }

    @Override
    public Collection<Word> words() {
        return Collections.emptyList();
    }

    @Override
    public Iterator<Word> iterator() {
        final List<Word> emptyList = Collections.<Word>emptyList();
        return emptyList.iterator();
    }
}
