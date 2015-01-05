package org.getalp.lexsema.supervised.features.extractors;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.supervised.features.WindowLoader;
import org.getalp.lexsema.supervised.features.WordWindow;

import java.util.ArrayList;
import java.util.List;


public class AlignedContextFeatureExtractor implements LocalTextFeatureExtractor {

    private final WindowLoader fileWindowLoader;

    public AlignedContextFeatureExtractor(WindowLoader loader) {
        fileWindowLoader = loader;
    }

    @Override
    public List<String> getFeatures(Document document, int currentIndex) {
        WordWindow ww = fileWindowLoader.getWordWindows().get(document.getWord(0, currentIndex).getLemma());
        List<String> features = new ArrayList<>();
        if (ww != null) {
            Word currentEntry = document.getWord(0, currentIndex);
            Sentence currentSentence = currentEntry.getEnclosingSentence();
            int sentenceIndex = currentSentence.indexOfWord(currentEntry);

            for (int j = sentenceIndex - ww.getStart(); j <= sentenceIndex + ww.getEnd(); j++) {
                if (j != sentenceIndex) {
                    String lemmaFeature;
                    if (j < 0 || j >= currentSentence.size()) {
                        lemmaFeature = "\"X\"";
                    } else {
                        String lemma = currentSentence.getWord(0, j).getLemma();
                        if (lemma == null) {
                            lemma = currentSentence.getWord(0, j).getSurfaceForm();
                        }
                        lemmaFeature = String.format("\"%s\"", lemma);
                    }
                    features.add(lemmaFeature);
                }
            }
        }
        return features;
    }
}
