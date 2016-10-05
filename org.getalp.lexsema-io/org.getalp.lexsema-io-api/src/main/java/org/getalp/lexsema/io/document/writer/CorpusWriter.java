package org.getalp.lexsema.io.document.writer;

import org.getalp.lexsema.similarity.Text;

import java.io.File;
import java.io.FileNotFoundException;

public interface CorpusWriter {
    void writeCorpus(File file, Iterable<Text> texts) throws FileNotFoundException;
}
