package org.getalp.lexsema.supervised.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WindowLoader {

    Map<String, WordWindow> wordWindows;
    private String file;

    public WindowLoader(String file) {
        this.file = file;

        wordWindows = new HashMap<>();
    }

    public void load() throws IOException {
        File trainingData = new File(file);
        BufferedReader br = new BufferedReader(new FileReader(trainingData));
        String inst = "";
        while ((inst = br.readLine()) != null) {
            String[] tokens = inst.trim().split("\t");
            wordWindows.put(tokens[0], new WordWindow(tokens[0], Integer.valueOf(tokens[1]), Integer.valueOf(tokens[2])));
        }
    }

    public Map<String, WordWindow> getWordWindows() {
        return wordWindows;
    }

    public class WordWindow {
        String word;
        Integer start;
        Integer end;


        public WordWindow(String word, Integer start, Integer end) {
            this.word = word;
            this.start = start;
            this.end = end;
        }

        public String getWord() {
            return word;
        }

        public Integer getStart() {
            return start;
        }

        public Integer getEnd() {
            return end;
        }
    }
}
