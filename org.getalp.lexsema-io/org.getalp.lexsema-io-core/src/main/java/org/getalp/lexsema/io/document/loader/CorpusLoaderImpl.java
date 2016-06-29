package org.getalp.lexsema.io.document.loader;


import org.getalp.lexsema.similarity.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class CorpusLoaderImpl implements CorpusLoader {
    List<Text> documents;

    protected CorpusLoaderImpl() {
        documents = new ArrayList<>();
    }

    @Override
    public abstract void load();


    @Override
    public Iterator<Text> iterator() {
        return documents.iterator();
    }
    
    protected void clearTexts() {
    	documents.clear();
    }

    protected void addText(Text text) {
        documents.add(text);
    }

    public abstract CorpusLoader loadNonInstances(boolean loadExtra);
}
