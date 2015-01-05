package org.getalp.lexsema.supervised.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileWindowLoader implements WindowLoader {

    Map<String, WordWindow> wordWindows;
    private String file;

    public FileWindowLoader(String file) {
        this.file = file;

        wordWindows = new HashMap<>();
    }

    @Override
    public void load() throws IOException {
        File trainingData = new File(file);
        try (BufferedReader br = new BufferedReader(new FileReader(trainingData))) {
            String inst = "";

            do {
                inst = br.readLine();
                if (inst == null) {
                    break;
                }
                String[] tokens = inst.trim().split("\t");
                wordWindows.put(tokens[0], new WordWindowImpl(tokens[0], Integer.valueOf(tokens[1]), Integer.valueOf(tokens[2])));
            } while (!inst.isEmpty());
            br.close();
        }
    }

    @Override
    public Map<String, WordWindow> getWordWindows() {
        return Collections.unmodifiableMap(wordWindows);
    }
}
