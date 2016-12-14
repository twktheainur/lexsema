package org.getalp.lexsema.io.document.loader;


import org.getalp.lexsema.io.text.TextProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

public class RawCorpusLoader extends CorpusLoaderImpl {

    private final Reader reader;
    private final TextProcessor processor;
    private static final Logger logger = LoggerFactory.getLogger(RawCorpusLoader.class);

    public RawCorpusLoader(Reader reader, TextProcessor processor) {
        this.reader = reader;
        this.processor = processor;
    }

    @Override
    public void load() {
        StringBuilder text = new StringBuilder();

        try (BufferedReader inputReader = new BufferedReader(reader)) {
            String line = "";
            while(line!=null){
                text.append(" ").append(line).append("\n");
                line = inputReader.readLine();
            }
        } catch (FileNotFoundException e) {
            logger.error("File not found - {}", e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error("Cannot read file: {}", e.getLocalizedMessage());
        }
        addText(processor.process(text.toString(),"RawText"));
    }

    @Override
    public CorpusLoader loadNonInstances(boolean loadExtra) {
        return this;
    }
}
