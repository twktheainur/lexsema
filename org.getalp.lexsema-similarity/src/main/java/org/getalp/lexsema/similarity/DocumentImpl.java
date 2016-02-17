package org.getalp.lexsema.similarity;


import org.getalp.lexsema.util.Language;

import java.util.*;

public class DocumentImpl implements Document {
    private String id = "";
    private final List<Word> lexicalEntries;
    private final List<List<Sense>> senses;
    private Language language = Language.UNSUPPORTED;


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
    public Word getWord(int index) {
        return getWord(0, index);
    }

    @Override
    public void addWord(Word word) {
        lexicalEntries.add(word);
    }

    @Override
    public void addWordSenses(Iterable<Sense> senses) {
        List<Sense> currentWordSenses = new ArrayList<>();
        for (Sense sense : senses) {
            currentWordSenses.add(sense);
        }
        try {
            final Word target = lexicalEntries.get(this.senses.size());
            target.loadSenses(currentWordSenses);
            this.senses.add(currentWordSenses);
        } catch (java.lang.IndexOutOfBoundsException e) {
            System.err.println("Exception caught: " + e);
            System.err.println("When trying to add the following senses:");
            for (Sense sense : senses) {
                System.err.println(sense.getId() + ": " + sense.getDefinition());
            }
            System.err.println("lexicalEntries size: " + lexicalEntries.size());
            System.err.println("senses size: " + this.senses.size());
        }
    }

    @Override
    public boolean isAlreadyLoaded(){

        return senses.size()>=lexicalEntries.size();
    }


    @Override
    public void addWords(Iterable<Word> words) {
        for (Word w : words) {
            addWord(w);
        }
    }

    @Override
    public void addWordsSenses(Iterable<Iterable<Sense>> senses) {
        for (Iterable<Sense> cws : senses) {
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
    public int numberOfSensesForWord(int index) {
        final List<Sense> wordSenses = senses.get(index);
        return wordSenses.size();
    }

    @Override
    public int indexOfWord(Word word) {
        return lexicalEntries.indexOf(word);
    }


    @Override
    public Iterator<Word> iterator() {
        return lexicalEntries.iterator();
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public Collection<Word> words() {
        return Collections.unmodifiableCollection(lexicalEntries);
    }
}
