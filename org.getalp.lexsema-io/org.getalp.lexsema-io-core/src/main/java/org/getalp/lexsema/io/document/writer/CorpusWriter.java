package org.getalp.lexsema.io.document.writer;

import org.getalp.lexsema.similarity.Text;

public interface CorpusWriter {
    void writeCorpus(Iterable<Text> texts);
}
