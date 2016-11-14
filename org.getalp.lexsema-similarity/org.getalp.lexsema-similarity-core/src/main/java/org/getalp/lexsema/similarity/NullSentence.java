package org.getalp.lexsema.similarity;


import org.getalp.lexsema.similarity.annotation.Annotations;
import org.getalp.lexsema.util.Language;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class NullSentence implements Sentence{

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String asString() {
        return "";
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
        return new NullWord();
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
    public boolean isAlreadyLoaded() {
        return true;
    }

    @Override
    public Iterator<Word> iterator() {
        final List<Word> emptyList = Collections.emptyList();
        return emptyList.iterator();
    }

    @Override
    public Text getParentText() {
        return DefaultDocumentFactory.DEFAULT.nullText();
    }

    @Override
    public void setParentText(Text text) {

    }

    @Override
    public Annotation getAnnotation(int index) {
        return Annotations.createNullAnnotation();
    }

    @Override
    public void addAnnotation(Annotation annotation) {

    }

    @Override
    public int annotationCount() {
        return 0;
    }

    @Override
    public Iterable<Annotation> annotations() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<Annotation> annotations(String annotationType) {
        return Collections.emptyList();
    }
}
