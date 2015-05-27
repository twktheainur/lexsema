package org.getalp.lexsema.io.annotresult;

import org.getalp.lexsema.similarity.Document;

public interface ConfigurationWriter {
    public void write(Document d, Integer[] assignments);
}
