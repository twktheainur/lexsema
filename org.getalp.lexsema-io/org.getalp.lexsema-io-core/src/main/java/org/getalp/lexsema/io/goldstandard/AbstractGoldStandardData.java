package org.getalp.lexsema.io.goldstandard;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractGoldStandardData implements GoldStandardData {

    protected abstract List<GoldStandardEntry> getData();

    protected abstract Map<String, Integer> getTextIndex();

    protected abstract Map<String, Integer> getSentenceIndex();

    protected abstract Map<String, Integer> getTextEndIndex();

    protected abstract Map<String, Integer> getSentenceEndIndex();

    @Override
    public List<GoldStandardEntry> getTextData(String textId) {
        List<GoldStandardEntry> ret;
        if (getTextIndex().containsKey(textId)) {
            int start = getTextIndex().get(textId);
            int end = getTextEndIndex().get(textId);
            try {
                ret = getData().subList(start, end + 1);
            } catch (IndexOutOfBoundsException e) {
                ret = getData().subList(start, end);
            }
        } else {
            ret = new ArrayList<>();
        }
        return ret;
    }

    @Override
    public List<GoldStandardEntry> getSentenceData(String textId, String sentenceId) {
        List<GoldStandardEntry> ret;
        String key = textId + "." + sentenceId;
        if (getSentenceIndex().containsKey(key)) {
            int start = getSentenceIndex().get(key);
            int end = getSentenceEndIndex().get(key);
            ret = getData().subList(start, end + 1);
        } else {
            ret = new ArrayList<>();
        }
        return ret;
    }
}
