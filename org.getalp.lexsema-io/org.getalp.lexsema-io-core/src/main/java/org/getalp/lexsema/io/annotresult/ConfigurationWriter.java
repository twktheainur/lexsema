package org.getalp.lexsema.io.annotresult;

import org.getalp.lexsema.similarity.Document;

public interface ConfigurationWriter {
    public void write(Document d, int[] assignments);

    public void write(Document d, int[] assignments, String[] idAssignments);
}
