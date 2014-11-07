package org.getalp.lexsema.supervised.features.extractors;

import org.getalp.lexsema.io.Document;
import org.getalp.lexsema.io.LexicalEntry;
import org.getalp.lexsema.io.Sentence;
import org.getalp.lexsema.supervised.features.WindowLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tchechem on 11/5/14.
 */
public class AlignedContextFeatureExtractor implements LocalTextFeatureExtractor {

    private final WindowLoader windowLoader;

    public AlignedContextFeatureExtractor(WindowLoader loader) {
        this.windowLoader = loader;
    }

    @Override
    public List<String> getFeatures(Document document, int currentIndex) {
        WindowLoader.WordWindow ww = windowLoader.getWordWindows().get(document.getLexicalEntries().get(currentIndex).getLemma());
        List<String> features = new ArrayList<>();
        if (ww != null) {
            LexicalEntry currentEntry = document.getLexicalEntries().get(currentIndex);
            Sentence currentSentence = currentEntry.getEnclosingSentence();
            int sentenceIndex = currentSentence.getLexicalEntries().indexOf(currentEntry);

            for (int j = sentenceIndex - ww.getStart(); j <= sentenceIndex + ww.getEnd(); j++) {
                if (j != sentenceIndex) {
                    String lemmaFeature;
                    if (j < 0 || j >= currentSentence.getLexicalEntries().size()) {
                        lemmaFeature = "\"X\"";
                    } else {
                        lemmaFeature = "\"" + currentSentence.getLexicalEntries().get(j).getLemma();
                    }
                    features.add(lemmaFeature);
                }
            }
        }
        return features;
    }
}
