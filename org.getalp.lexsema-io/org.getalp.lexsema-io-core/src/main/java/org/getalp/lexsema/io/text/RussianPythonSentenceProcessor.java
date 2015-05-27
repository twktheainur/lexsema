package org.getalp.lexsema.io.text;


import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.SentenceImpl;
import org.getalp.lexsema.similarity.WordImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class RussianPythonSentenceProcessor implements SentenceProcessor {
    private static Logger logger = LoggerFactory.getLogger(RussianPythonSentenceProcessor.class);
    @Override
    public Sentence process(String sentenceText, String documentId) {
        Sentence resultSentence = new SentenceImpl("");
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
                            resultSentence.addWord(new WordImpl(String.valueOf(index), lemma, word, pos));
                            logger.info("Word=" + fields[0] + " Lemma=" + fields[1] + " Pos=" + fields[2]);
                        }
                    }
                    index++;
                    line = resultReader.readLine();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resultSentence;
    }
}
