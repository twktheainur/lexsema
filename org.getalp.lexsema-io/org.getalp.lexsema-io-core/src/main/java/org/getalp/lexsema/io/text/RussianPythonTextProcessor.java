package org.getalp.lexsema.io.text;


import org.getalp.lexsema.similarity.DefaultDocumentFactory;
import org.getalp.lexsema.similarity.DocumentFactory;
import org.getalp.lexsema.similarity.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.MessageFormat;

public class RussianPythonTextProcessor implements TextProcessor {
    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.DEFAULT;
    private static final Logger logger = LoggerFactory.getLogger(RussianPythonTextProcessor.class);
    @Override
    public Text process(String sentenceText, String documentId) {
        Text resultSentence = DOCUMENT_FACTORY.createText();
        try {
            File tempText = File.createTempFile("lexsema_nlptools2", null);
            try (PrintWriter pw = new PrintWriter(tempText)) {
                pw.println(sentenceText);
            }
            ProcessBuilder processBuilder = new ProcessBuilder("python3","nlptools2"+File.separator+"tagger.py",tempText.getAbsolutePath());
            Process nlpTool = processBuilder.start();
            nlpTool.waitFor();
            File result = new File(tempText.getAbsolutePath()+".lemma");
            try (BufferedReader resultReader = new BufferedReader(new FileReader(result))) {
                String line="";
                int index = 0;
                while(line!=null){
                    if (!line.contains("<S>") && !line.contains("</S>") && !line.isEmpty()) {
                        String[] fields = line.split("\t");
                        String word = fields[0];
                        String lemma = fields[1];
                        String pos = fields[2];
                        if (!pos.toLowerCase().contains("pnct")) {
                            resultSentence.addWord(DOCUMENT_FACTORY.createWord(String.valueOf(index), lemma, word, pos));
                            logger.info(MessageFormat.format("Word={0} Lemma={1} Pos={2}", fields[0], fields[1], fields[2]));
                        }
                    }
                    index++;
                    line = resultReader.readLine();
                }
            }

        } catch (IOException e) {
            logger.error("I/O error: {}",e.getLocalizedMessage());
        } catch (InterruptedException e) {
            logger.error("Interrupted: {}", e.getLocalizedMessage());
        }
        return resultSentence;
    }
}
