package org.getalp.lexsema.acceptali.closure.writer;

import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.util.Language;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

public class FileTranslationClosureWriter implements TranslationClosureWriter {
    private File targetDirectory;
    private int senseFileIndex = 0;

    private File senseFile;
    private File lexicalEntryFile;

    public FileTranslationClosureWriter(String targetDirectory) {
        this.targetDirectory = new File(targetDirectory);
        if (!this.targetDirectory.exists()) {
            this.targetDirectory.mkdirs();
        }

        senseFile = new File(targetDirectory + File.separator + "LexicalSenses.csv");
        lexicalEntryFile = new File(targetDirectory + File.separator + "LexicalEntries.csv");

    }

    @Override
    public void writeClosure(LexicalResourceTranslationClosure<LexicalSense> closure) {
        try {
            PrintWriter senseWriter = new PrintWriter(senseFile);
            PrintWriter entryWriter = new PrintWriter(lexicalEntryFile);

            Map<Language, Map<LexicalEntry, Set<LexicalSense>>> closureData = closure.senseClosureByLanguageAndEntry();

            String output = "";
            for (Language l : closureData.keySet()) {
                for (LexicalEntry le : closureData.get(l).keySet()) {
                    writeLexicalEntry(l, le, closureData.get(l).get(le).size(), entryWriter);
                    for (LexicalSense ls : closureData.get(l).get(le)) {
                        writeLexicalSense(ls, senseWriter);
                    }
                }
            }
            entryWriter.close();
            senseWriter.close();
        } catch (FileNotFoundException ignored) {
        }


    }

    private void writeLexicalSense(LexicalSense lexicalSense, PrintWriter senseWriter) {
        senseWriter.println(String.format("%s\t%s\t%s", lexicalSense.getNode().getURI(), lexicalSense.getDefinition(), lexicalSense.getSenseNumber()));
        senseFileIndex++;
    }

    private void writeLexicalEntry(Language language, LexicalEntry lexicalEntry, int numberOfSenses, PrintWriter entryWriter) {
        String output = String.format("%s\t%s\t%s\t%s\t%s\t%d", language.toString(), lexicalEntry.getNode().toString(), lexicalEntry.getLemma(), lexicalEntry.getPartOfSpeech(), lexicalEntry.getNumber(), numberOfSenses);
        entryWriter.println(output);

    }

}
