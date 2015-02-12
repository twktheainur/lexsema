package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.wsd.configuration.ConfidenceConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads the mfs mapping from the baseline key file and annotates the text with the most frequent senses.
 */
public class FirstSenseDisambiguator implements Disambiguator {

    private static Logger logger = LoggerFactory.getLogger(FirstSenseDisambiguator.class);
    private Map<String, String> mfsMapping;

    public FirstSenseDisambiguator(String mfsBaselineKey) {
        if (mfsBaselineKey != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(mfsBaselineKey))) {
                String line = "";
                mfsMapping = new HashMap<>();
                do {
                    line = br.readLine();
                    if (line != null) {
                        String[] mfsEntries = line.split("\t")[0].split(" ");
                        mfsMapping.put(mfsEntries[1], mfsEntries[2]);
                    }
                } while (line != null && !line.isEmpty());
            } catch (FileNotFoundException e) {
                logger.error("File not found: " + e.getMessage());
            } catch (IOException e) {
                logger.error("Error reading file: " + e.getMessage());
            }
        }
    }

    public FirstSenseDisambiguator() {
    }

    @Override
    public Configuration disambiguate(Document document) {
        Configuration c = new ConfidenceConfiguration(document);
        for (int i = 0; i < document.size(); i++) {
            if (mfsMapping != null) {
                int index = -1;
                List<Sense> sense = document.getSenses(i);
                for (int j = 0; j < sense.size(); j++) {
                    Word target = document.getWord(0, i);
                    if (sense.get(j).getId().equals(mfsMapping.get(target.getId()))) {
                        index = j;
                    }
                }
                c.setSense(i, index);
            } else {
                c.setSense(i, 0);
            }
        }
        return c;
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c) {
        Configuration cret = new ConfidenceConfiguration(document);
        for (int i = 0; i < document.size(); i++) {
            if (c.getAssignment(i) == -1) {
                if (mfsMapping != null) {
                    int index = -1;
                    List<Sense> sense = document.getSenses(i);
                    for (int j = 0; j < sense.size(); j++) {
                        if (sense.get(j).getId().equals(mfsMapping.get(document.getWord(0, i).getId()))) {
                            index = j;
                        }
                    }
                    c.setSense(i, index);
                } else {
                    c.setSense(i, 0);
                }
            } else {
                cret.setSense(i, c.getAssignment(i));
            }
        }
        return cret;
    }

    @Override
    public void release() {

    }
}
