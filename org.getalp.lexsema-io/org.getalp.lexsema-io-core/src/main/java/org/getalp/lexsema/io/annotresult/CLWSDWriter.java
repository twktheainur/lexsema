package org.getalp.lexsema.io.annotresult;


import org.getalp.lexsema.ontolex.dbnary.Translation;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class CLWSDWriter {

    private static Logger logger = LoggerFactory.getLogger(CLWSDWriter.class);

    Map<String, PrintWriter> writers;
    private File targetDirectory;

    public CLWSDWriter(String targetDirectory) throws IOException {
        this.targetDirectory = new File(targetDirectory);
        if (!this.targetDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            this.targetDirectory.mkdirs();
        }
        writers = new HashMap<>();
    }


    public void writeEntry(Word targetWord, int contextIndex, Translation translation, Language language) {
        try {
            String id = String.format("%s.%s", targetWord.getId(), language.getISO2Code());
            PrintWriter writer;
            if (!writers.containsKey(id)) {
                File languageDir = new File(String.format("%s%s%s", targetDirectory.getAbsolutePath(), File.separatorChar, language.getLanguageName()));
                if (!languageDir.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    languageDir.mkdirs();
                }
                String radix = String.format("%s_%s_answer.txt", targetWord.getId(), language.getISO2Code());
                File outputFile = new File(String.format("%s%s%s", languageDir.getAbsolutePath(), File.separatorChar, radix));
                if (!outputFile.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    outputFile.createNewFile();
                }
                writer = new PrintWriter(outputFile);
                writers.put(id, writer);
            } else {
                writer = writers.get(id);
            }
            String outputLine = String.format("%s %d :: %s;", id, contextIndex, translation.getWrittenForm());
            logger.info(outputLine);
            writer.println(outputLine);
            writer.flush();
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private void release() {
        for (PrintWriter pw : writers.values()) {
            pw.close();
        }
    }
}
