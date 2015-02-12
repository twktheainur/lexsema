package org.getalp.lexsema.io.document;


import org.getalp.lexsema.similarity.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class TextLoaderImpl implements TextLoader {
    List<Text> documents;

    protected TextLoaderImpl() {
        documents = new ArrayList<>();
    }

    @Override
    public abstract void load();


    @Override
    public Iterator<Text> iterator() {
        return documents.iterator();
    }

    protected void addText(Text text) {
        documents.add(text);
    }

    public abstract TextLoader loadNonInstances(boolean loadExtra);
}
