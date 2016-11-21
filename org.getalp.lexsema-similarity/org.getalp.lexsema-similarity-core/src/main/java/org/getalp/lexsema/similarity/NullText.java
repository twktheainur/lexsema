package org.getalp.lexsema.similarity;


import org.getalp.lexsema.util.Language;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class NullText implements Text{

    private final Word nullWord = new NullWord();

    @Override
    public String getId() {
        return "";
    }

    @Override
    public void setId(String id) {

    }

    @Override
    public Word getWord(int offset, int index) {
        return nullWord;
    }

    @Override
    public Word getWord(int index) {
        return nullWord;
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
    public int numberOfSensesForWord(int index) {
        return 0;
    }

    @Override
    public int indexOfWord(Word word) {
        return 0;
    }

    @Override
    public Language getLanguage() {
        return Language.UNSUPPORTED;
    }

    @Override
    public void setLanguage(Language language) {

    }

    @Override
    public Collection<Word> words() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAlreadyLoaded() {
        return false;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String asString() {
        return "";
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void addSentence(Sentence sentence) {
    }

    @Override
    public int numberOfSentences() {
        return 0;
    }

    @Override
    public Collection<Sentence> sentences() {
        return Collections.emptyList();
    }

    @Override
    public Iterator<Word> iterator() {
        final List<Word> words = Collections.<Word>emptyList();
        return words.iterator();
    }
}
