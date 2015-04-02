package org.getalp.lexsema.io.goldstandard;


public interface GoldStandardEntry {
    public String getTextId();

    public String getSentenceId();

    public String getWordId();

    public String getAnnotation();

    public String getComment();
}
