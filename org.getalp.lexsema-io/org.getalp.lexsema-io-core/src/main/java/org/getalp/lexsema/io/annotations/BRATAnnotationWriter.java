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

public class BRATAnnotationWriter implements AnnotationWriter {

    private static final Logger logger = LoggerFactory.getLogger(BRATAnnotationWriter.class);

    private int termCounter = 1;

    @Override
    public void writeAnnotations(Path output, Document document) {

        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(output))) {
            for (Word word : document.words()) {
                Annotation previousAnnotation = null;
                for (Annotation annotation : word.annotations()) {
                    if (previousAnnotation == null || !annotation.type().equals(previousAnnotation.type())) {
                        writeAnnotation(printWriter, word, annotation);
                        previousAnnotation = annotation;
                    }
                }
                if(!word.annotations().isEmpty()){
                    termCounter++;
                }
            }
        } catch (IOException e) {
            logger.error("Cannot open target file for writing: {}", e.getLocalizedMessage());
        }
    }

    private void writeAnnotation(PrintWriter printWriter, Word word, Annotation annotation) {
        printWriter.println(String.format("T%d\t%s %d %d\t%s", termCounter, annotation.type(), word.getBegin(), word.getEnd(), word.getSurfaceForm()));
    }
}
