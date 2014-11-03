package org.getalp.segmentation;

import java.util.ArrayList;
import java.util.List;

public class SpaceSegmenter implements Segmenter {
    private boolean removePuctuation = true;

    @Override
    public List<String> segment(String value) {
        if (removePuctuation) {
            value = value.replaceAll("\\p{Punct}+", " ");
        }
        List<String> ret = new ArrayList<String>();
        for (String token : value.split("\\p{Z}")) {
            ret.add(token);
        }
        return ret;
    }

    public void setRemovePuctuation(boolean removePuctuation) {
        this.removePuctuation = removePuctuation;
    }
}
