package org.getalp.lexsema.similarity;


import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class DocumentImpl implements Document {

    private static final Logger logger = LoggerFactory.getLogger(DocumentImpl.class);

    private String id = "";
    private final List<Word> lexicalEntries;
    private final List<List<Sense>> senses;
    private Language language = Language.UNSUPPORTED;


    DocumentImpl() {
        lexicalEntries = new ArrayList<>();
        senses = new ArrayList<>();
    }

    DocumentImpl(Language language) {
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
            logger.debug("Exception caught: {}", e);
            logger.debug("When trying to add the following senses:");
            for (Sense sense : senses) {
                logger.debug("{} : {}", sense.getId(), sense.getSemanticSignature());
            }
            logger.debug("lexicalEntries size: {}", lexicalEntries.size());
            logger.debug("senses size: {}", this.senses.size());
        }
    }

    @Override
    public boolean isAlreadyLoaded() {

        return senses.size() >= lexicalEntries.size();
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public String asString() {
        StringBuilder output = new StringBuilder();
        for (Word le : this) {
            final String lemma = le.getLemma();
            output.append(lemma.trim());
            output.append(" ");
        }
        final String s = output.toString();
        return s.trim();
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

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(" ").append(getId()).append(" [\n");
        for (Word word : lexicalEntries) {
            output.append("\t").append(word.toString());
        }
        output.append("]\n");
        return output.toString();
    }
}
