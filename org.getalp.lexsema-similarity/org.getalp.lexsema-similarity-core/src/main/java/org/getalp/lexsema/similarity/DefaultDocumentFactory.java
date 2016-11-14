package org.getalp.lexsema.similarity;

import org.getalp.lexsema.util.Language;

public class DefaultDocumentFactory implements DocumentFactory {

    public static final DocumentFactory DEFAULT = new DefaultDocumentFactory();

    @Override
    public Document createDocument() {
        return new DocumentImpl();
    }

    @Override
    public Document createDocument(Language language) {
        return new DocumentImpl(language);
    }

    @Override
    public Document nullDocument() {
        return new NullDocument();
    }

    @Override
    public Sentence createSentence(String id) {
        return  new SentenceImpl(id);
    }

    @Override
    public Sentence createSentence(String id, Language language) {
        return new SentenceImpl(id,language);
    }

    @Override
    public Sentence nullSentence() {
        return new NullSentence();
    }

    @Override
    public Text createText(Language language) {
        return new TextImpl(language);
    }

    @Override
    public Text createText() {
        return new TextImpl();
    }

    @Override
    public Text nullText() {
        return new NullText();
    }

    @Override
    public Word createWord(String id, String lemma, String surfaceForm, String pos) {
        return new WordImpl(id,lemma,surfaceForm,pos);
    }

    @Override
    public Word createWord(String id, String lemma, String surfaceForm, String pos, int begin, int end) {
        return new WordImpl(id,lemma,surfaceForm,pos, begin, end);
    }

    @Override
    public Word createWord(String id, String lemma, String surfaceForm, String pos, Language language) {
        return new WordImpl(id,lemma,surfaceForm,pos,language);
    }

    @Override
    public Word createWord(String id, String lemma, String surfaceForm, String pos, Language language, int begin, int end) {
        return new WordImpl(id,lemma,surfaceForm,pos,language,begin,end);
    }

    @Override
    public Word nullWord() {
        return new NullWord();
    }

    @Override
    public Sense createSense(String id) {
        return new SenseImpl(id);
    }

    @Override
    public Sense createSense(String id, Language language) {
        return null;
    }

    @Override
    public Sense nullSense() {
        return new NullSense();
    }
}
