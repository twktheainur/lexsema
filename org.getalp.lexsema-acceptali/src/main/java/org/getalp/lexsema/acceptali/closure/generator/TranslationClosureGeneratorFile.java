package org.getalp.lexsema.acceptali.closure.generator;

import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosureImpl;
import org.getalp.lexsema.ontolex.*;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class TranslationClosureGeneratorFile implements TranslationClosureGenerator {
    private static final Logger logger = LoggerFactory.getLogger(TranslationClosureGeneratorFile.class);
    private final String path;
    private final LexicalResource dbNary;


    TranslationClosureGeneratorFile(final LexicalResource dbNary, final String path) {
        this.path = path;
        this.dbNary = dbNary;
    }


    @Override
    public LexicalResourceTranslationClosure<LexicalSense> generateClosure(int degree) {
        return generateClosure();
    }

    @SuppressWarnings("all")
    @Override
    public LexicalResourceTranslationClosure<LexicalSense> generateClosure() {
        LexicalResourceTranslationClosure<LexicalSense> closure = new LexicalResourceTranslationClosureImpl();
        List<String> senses = new ArrayList<>();
        try (BufferedReader sensesReader = new BufferedReader(new FileReader(String.format("%s%sLexicalSenses.csv", path, File.separator)))) {
            String line;
            do {
                line = sensesReader.readLine();
                senses.add(line);
            } while (line != null);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        int currentSenseIndex = 0;

        try (BufferedReader entryReader = new BufferedReader(new FileReader(String.format("%s%sLexicalEntries.csv", path, File.separator)))) {
            String line;
            do {
                line = entryReader.readLine();
                if (line != null) {
                    String[] fields = line.split("\t");
                    Language language = Language.valueOf(fields[0]);
                    LexicalEntry entry = new LexicalEntryImpl(dbNary, fields[1], null, fields[2], fields[3]);
                    if (!fields[4].isEmpty()) {
                        entry.setNumber(Integer.valueOf(fields[4]));
                    }
                    entry.setLanguage(language);
                    int numSenses = Integer.valueOf(fields[5]);
                    int localSenseNumber = 0;
                    for (localSenseNumber = currentSenseIndex; localSenseNumber < currentSenseIndex + numSenses && localSenseNumber < senses.size(); localSenseNumber++) {
                        if (senses.get(localSenseNumber) != null) {
                            String[] senseFields = senses.get(localSenseNumber).split("\t");
                            LexicalSense sense;
                            if (senseFields.length > 2) {
                                sense = new LexicalSenseImpl(dbNary, senseFields[0], entry, senseFields[2]);
                            } else {
                                sense = new LexicalSenseImpl(dbNary, senseFields[0], entry, "");
                            }
                            sense.setDefinition(senseFields[1]);
                            sense.setLanguage(language);
                            closure.addSense(language, entry, sense);
                        }
                    }
                    currentSenseIndex = localSenseNumber;
                }
            } while (line != null);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return closure;
    }
}
