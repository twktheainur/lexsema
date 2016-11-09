package org.getalp.lexsema.io.document.writer;

import org.getalp.lexsema.similarity.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public interface CorpusWriter {
    void writeCorpus(Path file, Iterable<Text> texts) throws IOException;
}
