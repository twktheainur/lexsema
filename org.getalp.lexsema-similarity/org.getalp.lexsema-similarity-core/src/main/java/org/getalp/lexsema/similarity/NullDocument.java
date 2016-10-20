package org.getalp.lexsema.similarity;

import org.getalp.lexsema.util.Language;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

final class NullDocument implements Document{
    @Override
    public String getId() {
        return null;
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
        return null;
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
        return null;
    }

    @Override
    public List<Sense> getSenses(int index) {
        return null;
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
        return null;
    }

    @Override
    public void setLanguage(Language language) {

    }

    @Override
    public Collection<Word> words() {
        return null;
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
    public Iterator<Word> iterator() {
        return null;
    }
}
