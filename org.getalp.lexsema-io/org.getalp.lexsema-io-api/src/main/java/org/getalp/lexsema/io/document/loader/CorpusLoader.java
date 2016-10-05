package org.getalp.lexsema.io.document.loader;

import org.getalp.lexsema.similarity.Text;

public interface CorpusLoader extends Iterable<Text> {
    /**
     * Loads the resource
     */
    public void load();

    @SuppressWarnings("all")
    public CorpusLoader loadNonInstances(boolean loadExtra);

}
