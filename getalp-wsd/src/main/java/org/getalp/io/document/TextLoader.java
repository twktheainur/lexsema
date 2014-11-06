package org.getalp.io.document;


import org.getalp.io.Loader;
import org.getalp.io.Text;

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
