package org.getalp.lexsema.io.goldstandard;


import java.util.List;

/**
 * Interface for gold standard data classes automatically generated from a gold standard file
 */
public interface GoldStandardData {
    public List<GoldStandardEntry> getTextData(String textId);

    public List<GoldStandardEntry> getSentenceData(String textId, String sentenceId);
}
