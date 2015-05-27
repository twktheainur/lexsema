package org.getalp.lexsema.io.text;


import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.SentenceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RussianPythonSentenceProcessor implements SentenceProcessor {
    private static Logger logger = LoggerFactory.getLogger(RussianPythonSentenceProcessor.class);
    @Override
    public Sentence process(String sentenceText, String documentId) {
        Sentence resultSentence = new SentenceImpl("");
        try {
            File tempText = File.createTempFile("lexsema_nlptools2", null);

            ProcessBuilder processBuilder = new ProcessBuilder("python3","nlptools2"+File.separator+"tagger.py",tempText.getAbsolutePath());
            Process nlpTool = processBuilder.start();
            nlpTool.waitFor();
            File result = new File(tempText.getAbsolutePath()+".lemma");
            try (BufferedReader resultReader = new BufferedReader(new FileReader(result))) {
                String line="";
                while(line!=null){
                    if(!line.contains("<S>")&&!line.contains("</S>") ){
                        String[] fields = line.split("\t");
                        logger.info("Word="+fields[0] +" Lemma="+fields[1] + " Pos="+fields[2]);
                    }
                    line = resultReader.readLine();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return null;
    }
}
