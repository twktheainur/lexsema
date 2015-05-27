package org.getalp.lexsema.io.annotresult;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class SemevalWriter implements ConfigurationWriter {
    private static Logger logger = LoggerFactory.getLogger(SemevalWriter.class);
    private String path;
    private String commentSeparator;

    public SemevalWriter(String path) {
        this.path = path;
        commentSeparator = " ";
    }

    public SemevalWriter(String path, String commentSeparator) {
        this.path = path;
        this.commentSeparator = commentSeparator;
    }

    @Override
    public void write(Document d, Integer[] assignments) {
        try (PrintStream ps = new PrintStream(path)) {
            String id = d.getId();
            int wordIndex = 0;
            for (Word w : d) {
                if (w.getId() != null && !w.getId().isEmpty()) {
                    if (assignments[wordIndex] >= 0 && !d.getSenses(wordIndex).isEmpty()) {
                        ps.printf("%s %s %s%s!! %s#%s%n", id, w.getId(), d.getSenses(wordIndex).get(assignments[wordIndex]).getId(), commentSeparator, w.getLemma(), w.getPartOfSpeech());
                    } /*else {
                        ps.println(id + " " + w.getId() + " !! "+w.getLemma()+"#"+w.getPartOfSpeech());
                    }*/
                }
                wordIndex++;
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }

    }
}
