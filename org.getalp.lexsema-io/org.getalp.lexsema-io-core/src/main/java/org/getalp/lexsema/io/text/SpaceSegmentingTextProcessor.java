package org.getalp.lexsema.io.text;


import org.getalp.lexsema.similarity.*;
import org.getalp.lexsema.util.Language;

public class SpaceSegmentingTextProcessor implements TextProcessor {
    @Override
    public Text process(String sentenceText, String documentId) {
        String[] tokens = sentenceText.split(" ");
        Text text = new TextImpl(Language.ENGLISH);
        Sentence sentence = new SentenceImpl(documentId);
        for(String token: tokens){
            Word word = new WordImpl("",token,token,"");
            text.addWord(word);
            sentence.addWord(word);
        }
        text.addSentence(sentence);
        return text;
    }
}
