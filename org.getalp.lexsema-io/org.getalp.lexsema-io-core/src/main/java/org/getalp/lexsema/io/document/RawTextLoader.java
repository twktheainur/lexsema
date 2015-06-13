package org.getalp.lexsema.io.document;


import org.getalp.lexsema.io.text.TextProcessor;

import java.io.*;

public class RawTextLoader extends TextLoaderImpl {

    Reader reader;
    TextProcessor processor;

    public RawTextLoader(Reader reader, TextProcessor processor) {
        this.reader = reader;
        this.processor = processor;
    }

    @Override
    public void load() {
        StringBuilder text = new StringBuilder();

        try (BufferedReader inputReader = new BufferedReader(reader)) {
            String line = "";
            while(line!=null){
                text.append(" ").append(line);
                line = inputReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        addText(processor.process(text.toString(),""));
    }

    @Override
    public TextLoader loadNonInstances(boolean loadExtra) {
        return this;
    }
}
