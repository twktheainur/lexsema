package org.getalp.lexsema.similarity;


import org.getalp.lexsema.util.Language;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DocumentImpl implements Document {
    private String id;
    private List<Word> lexicalEntries;
    private List<List<Sense>> senses;
    private Language language;


    public DocumentImpl() {
        lexicalEntries = new ArrayList<>();
        senses = new ArrayList<>();
    }

    public DocumentImpl(Language language) {
        lexicalEntries = new ArrayList<>();
        senses = new ArrayList<>();
        this.language = language;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Word getWord(int offset, int index) {
        return lexicalEntries.get(index + offset);
    }

    @Override
    public void addWord(Word w) {
        lexicalEntries.add(w);
    }

    @Override
    public void addWordSenses(Iterable<Sense> s) {
        List<Sense> currentWordSenses = new ArrayList<>();
        for (Sense sense : s) {
            currentWordSenses.add(sense);
        }
        senses.add(currentWordSenses);
    }

    @Override
    public void addWords(Iterable<Word> words) {
        for (Word w : words) {
            addWord(w);
        }
    }

    @Override
    public void addWordsSenses(Iterable<Iterable<Sense>> s) {
        for (Iterable<Sense> cws : s) {
            addWordSenses(cws);
        }
    }

    @Override
    public List<Sense> getSenses(int offset, int index) {
        return senses.get(index + offset);
    }

    @Override
    public List<Sense> getSenses(int index) {
        return senses.get(index);
    }

    @Override
    public int size() {
        return lexicalEntries.size();
    }

    @Override
    public int indexOfWord(Word w) {
        return lexicalEntries.indexOf(w);
    }


    @Override
    public Iterator<Word> iterator() {
        return lexicalEntries.iterator();
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
