package org.getalp.disambiguation.loaders.document;


import org.getalp.disambiguation.Text;
import org.getalp.disambiguation.loaders.Loader;

import java.util.ArrayList;
import java.util.List;

public abstract class TextLoader implements Loader {
    List<Text> documents;

    protected TextLoader() {
        documents = new ArrayList<>();
    }

    public List<Text> getTexts() {
        return documents;
    }

    public abstract void load();
}
