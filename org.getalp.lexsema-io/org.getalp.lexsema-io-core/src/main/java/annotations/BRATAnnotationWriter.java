package annotations;

import org.getalp.lexsema.similarity.Annotation;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

public class BRATAnnotationWriter implements AnnotationWriter {

    private static final Logger logger = LoggerFactory.getLogger(BRATAnnotationWriter.class);

    private int termCounter;

    @Override
    public void writeAnnotations(Path output, Document document) {

        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(output))) {
            for (Word word : document.words()) {
                termCounter++;
                Annotation previousAnnotation = null;
                for (Annotation annotation : word.annotations()) {
                    if (previousAnnotation == null || !annotation.type().equals(previousAnnotation.type())) {
                        writeAnnotation(printWriter, word, annotation);
                        previousAnnotation = annotation;
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Cannot open target file for writing: {}", e.getLocalizedMessage());
        }
    }

    private void writeAnnotation(PrintWriter printWriter, Word word, Annotation annotation) {
        printWriter.println(MessageFormat.format("T{0}\t{1}\t{2}\t{3}\t{4}", termCounter, annotation.type(), word.getBegin(), word.getEnd(), word.getSurfaceForm()));
    }
}
