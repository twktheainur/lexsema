package org.getalp.lexsema.similarity;


import org.getalp.lexsema.util.Language;

public interface DocumentFactory {
    Document createDocument();
    Document createDocument(Language language);
    Document nullDocument();
    Sentence createSentence(String id);
    Sentence createSentence(String id, Language language);
    Sentence nullSentence();
    Text createText(Language language);
    Text createText();
    Text nullText();
    Word createWord(String id, String lemma, String surfaceForm, String pos);
    Word createWord(String id, String lemma, String surfaceForm, String pos, int begin, int end);
    Word createWord(String id, String lemma, String surfaceForm, String pos, Language language);
    Word createWord(String id, String lemma, String surfaceForm, String pos, Language language, int begin, int end);
    Word nullWord();
    Sense createSense(String id);
    Sense createSense(String id, Language language);
    Sense nullSense();
}
