package org.getalp.lexsema.util.segmentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpaceSegmenter implements Segmenter {
    private boolean removePunctuation = true;

    @Override
    public List<String> segment(String value) {
        String punctuationLessValue = value;
        if (removePunctuation) {
            punctuationLessValue = punctuationLessValue.replaceAll("\\p{Punct}+", " ");
        }
        List<String> ret = new ArrayList<>();
        Collections.addAll(ret, punctuationLessValue.split("\\p{Z}"));
        return ret;
    }

    public void setRemovePunctuation(boolean removePunctuation) {
        this.removePunctuation = removePunctuation;
    }
}
