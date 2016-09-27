package org.getalp.lexsema.io.annotresult;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.MessageFormat;

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
    public void write(Document d, int[] assignments) {
        try (PrintStream ps = new PrintStream(path)) {
            String id = d.getId();
            int wordIndex = 0;
            for (Word w : d) {

                logger.debug("@@@@@@@@@@@@@@@");
                logger.debug(MessageFormat.format("Word Id {0}", w.getId()));
                logger.debug(MessageFormat.format("SurfaceForm {0}", w.getSurfaceForm()));
                logger.debug(MessageFormat.format("Lemma {0}", w.getLemma()));
                logger.debug(MessageFormat.format("SenseAnnotation {0}", w.getSenseAnnotation()));
                logger.debug("@@@@@@@@@@@@@@@");

                if (w.getId() != null && !w.getId().isEmpty()) {
                    logger.debug(MessageFormat.format("Assignement {0}", assignments[wordIndex]));
                    logger.debug(MessageFormat.format("empty : {0}", d.getSenses(wordIndex).isEmpty()));
                    if (assignments[wordIndex] >= 0 && !d.getSenses(wordIndex).isEmpty()) {
                        ps.printf("%s %s %s%s!! %s#%s%n", id, w.getId(), d.getSenses(wordIndex).get(assignments[wordIndex]).getId(), commentSeparator, w.getLemma(), w.getPartOfSpeech());
                    } else {
                        ps.println(id + " " + w.getId() + " !! "+w.getLemma()+"#"+w.getPartOfSpeech());
                    }
                }
                wordIndex++;
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }

    }

    @Override
    public void write(Document d, int[] assignments, String[] idAssignments) {

        try (PrintStream ps = new PrintStream(path)) {
            String id = d.getId();
            int wordIndex = 0;
            for (Word w : d) {

                logger.debug("@@@@@@@@@@@@@@@");
                logger.debug(MessageFormat.format("Word Id {0}", w.getId()));
                logger.debug(MessageFormat.format("SurfaceForm {0}", w.getSurfaceForm()));
                logger.debug(MessageFormat.format("Lemma {0}", w.getLemma()));
                logger.debug(MessageFormat.format("SenseAnnotation {0}", w.getSenseAnnotation()));
                logger.debug("@@@@@@@@@@@@@@@");

                if (w.getId() != null && !w.getId().isEmpty()) {
                    logger.debug(MessageFormat.format("Assignement {0}", idAssignments[wordIndex]));
                    logger.debug(MessageFormat.format("empty : {0}", d.getSenses(wordIndex).isEmpty()));
                    //if (assignments[wordIndex] >= 0 && !d.getSenses(wordIndex).isEmpty()) {
                    if (assignments[wordIndex] >= 0) {
                        ps.printf("%s %s %s%s!! %s#%s%n", id, w.getId(), d.getSenses(wordIndex).get(assignments[wordIndex]).getId(), commentSeparator, w.getLemma(), w.getPartOfSpeech());
                    } else {
                        ps.printf("%s %s %s%s!! %s#%s%n", id, w.getId(), idAssignments[wordIndex].replaceAll("\"", ""), commentSeparator, w.getLemma(), w.getPartOfSpeech());
                    }
                        // } else {
                   //     ps.println(id + " " + w.getId() + " !! "+w.getLemma()+"#"+w.getPartOfSpeech());
                   // }
                }
                wordIndex++;
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }


    }
}
