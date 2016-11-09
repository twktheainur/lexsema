package org.getalp.lexsema.axalign.experiments;

import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.DSOCorpusLoader;
import org.getalp.lexsema.io.document.writer.CorpusWriter;
import org.getalp.lexsema.io.document.writer.SemCorCorpusWriter;

import java.io.IOException;
import java.nio.file.Paths;

public final class ConvertDSO {
    private ConvertDSO() {
    }

    public static void main (String... args) throws IOException {
        CorpusLoader textLoader = new DSOCorpusLoader("../data/dso","../data/wordnet/2.1/dict");
        textLoader.load();
        CorpusWriter corpusWriter = new SemCorCorpusWriter();
        corpusWriter.writeCorpus(Paths.get("dsoCorpus.xml"),textLoader);
    }
}
