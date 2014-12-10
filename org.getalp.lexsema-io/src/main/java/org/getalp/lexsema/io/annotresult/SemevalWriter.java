package org.getalp.lexsema.io.annotresult;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class SemevalWriter implements ConfigurationWriter {
    private static Logger logger = LoggerFactory.getLogger(SemevalWriter.class);
    String path;

    public SemevalWriter(String path) {
        this.path = path;
    }

    @Override
    public void write(Document d, int[] assignments) {
        try (PrintStream ps = new PrintStream(path)) {
            String id = d.getId();
            int wordIndex = 0;
            for (Word w : d) {
                if (assignments[wordIndex] >= 0) {
                    ps.println(id + " " + w.getId() + " " + d.getSenses(wordIndex).get(assignments[wordIndex]));
                } else {
                    ps.println(id + " " + w.getId() + " ");
                }
                wordIndex++;
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }

    }
}
