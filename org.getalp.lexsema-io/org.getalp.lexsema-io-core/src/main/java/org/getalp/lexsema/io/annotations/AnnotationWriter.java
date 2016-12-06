package org.getalp.lexsema.io.annotations;


import org.getalp.lexsema.similarity.Document;

import java.nio.file.Path;

public interface AnnotationWriter {
    void writeAnnotations(Path output, Document document);
}
