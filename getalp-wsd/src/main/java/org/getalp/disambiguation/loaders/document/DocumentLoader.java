package org.getalp.disambiguation.loaders.document;


import org.getalp.disambiguation.Document;

import java.util.ArrayList;
import java.util.List;

public abstract class DocumentLoader {
    List<Document> documents;

    protected DocumentLoader() {
        documents = new ArrayList<Document>();
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public abstract void load();
}
