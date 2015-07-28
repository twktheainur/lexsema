package org.getalp.lexsema.supervised.features;

import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;

import java.io.IOException;
import java.util.*;

public class DocumentCollectionWindowLoader implements WindowLoader {

    Map<String, WordWindow> wordWindows;
    Set<String> words;
    private CorpusLoader corpus;

    public DocumentCollectionWindowLoader(CorpusLoader corpus) {
        this.corpus = corpus;
        wordWindows = new HashMap<>();
        words = new TreeSet<>();
    }

    @Override
    public void load() throws IOException {
        for (Text t : corpus) {
            for (Sentence s : t.sentences()) {
                int wordIndex = 0;
                for (Word w : s) {
                    String lemma = w.getLemma();
                    if (lemma == null) {
                        continue;
                    }
                    if (!words.contains(lemma)) {
                        words.add(lemma);
                    }
                    int windowStart = wordIndex;
                    int windowEnd = s.size() - wordIndex;
                    if (!wordWindows.containsKey(lemma)) {
                        wordWindows.put(lemma, new WordWindowImpl(lemma, windowStart, windowEnd));
                    } else {
                        WordWindow currentLemmaWindow = wordWindows.get(lemma);
                        currentLemmaWindow.updateWindow(windowStart, windowEnd);
                    }
                    wordIndex++;
                }
            }
        }
    }

    @Override
    public Map<String, WordWindow> getWordWindows() {
        return Collections.unmodifiableMap(wordWindows);
    }

}
