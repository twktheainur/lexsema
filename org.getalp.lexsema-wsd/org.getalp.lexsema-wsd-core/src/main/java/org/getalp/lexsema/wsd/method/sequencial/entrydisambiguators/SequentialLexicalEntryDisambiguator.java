package org.getalp.lexsema.wsd.method.sequencial.entrydisambiguators;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.wsd.configuration.Configuration;

import java.util.List;

public abstract class SequentialLexicalEntryDisambiguator implements Runnable {
    private int start;
    private int end;
    private int currentIndex;
    private Configuration configuration;
    private Document document;

    protected SequentialLexicalEntryDisambiguator(Configuration configuration, Document d, int start, int end, int currentIndex) {
        this.start = start;
        this.end = end;
        this.currentIndex = currentIndex;
        this.configuration = configuration;
        document = d;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Document getDocument() {
        return document;
    }

    public List<Sense> getSenses(int offset, int index) {
        return document.getSenses(offset, index);
    }
}
