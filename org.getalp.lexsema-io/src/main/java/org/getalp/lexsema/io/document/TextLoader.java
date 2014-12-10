package org.getalp.lexsema.io.document;

import org.getalp.lexsema.similarity.Text;

public interface TextLoader extends Iterable<Text> {
    /**
     * Loads the resource
     */
    public void load();

    @SuppressWarnings("all")
    public TextLoader loadNonInstances(boolean loadExtra);

}
