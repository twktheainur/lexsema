package org.getalp.lexsema.io.dictionary;

import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class FullLRDictionaryWriter implements DictionaryWriter {

    private static final Logger logger = LoggerFactory.getLogger(FullLRDictionaryWriter.class);

    Map<Word, List<Sense>> wordListMap;


    @SuppressWarnings("unused")
    public FullLRDictionaryWriter(LRLoader loader) {
        wordListMap = Collections.unmodifiableMap(loader.getAllSenses());
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public void writeDictionary(File dictionary) {

        try (PrintWriter printWriter = new PrintWriter(dictionary)) {
            printWriter.println("<dict>");
            int wordIndex = 0;
            for (Map.Entry<Word, List<Sense>> wordListEntry : wordListMap.entrySet()) {
                    wordIndex++;
                    writeWordStartTag(printWriter, wordListEntry.getKey());
                    for (Sense sense : wordListEntry.getValue()) {
                        writeSenseStartTag(printWriter);
                        writeSenseContent(printWriter, sense);
                        writeSenseEndTag(printWriter);
                    }
                    writeWordEndTag(printWriter);
                    logger.trace("Word {} ({}/{})", wordListEntry.getKey().getLemma() ,wordIndex, wordListMap.size());
            }
            printWriter.println("</dict>");
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }

    }

    private void writeWordStartTag(final PrintWriter pw, final LexicalEntry word) {
        pw.println(String.format("<word tag=\"%s%%%s\">", word.getLemma(), word.getPartOfSpeech()));
    }

    private void writeWordEndTag(PrintWriter pw) {
        pw.println("</word>");
    }

    private void writeSenseStartTag(PrintWriter pw) {
        pw.println("<sense>");
    }

    private void writeSenseEndTag(PrintWriter pw) {
        pw.println("</sense>");
    }

    private void writeSenseContent(PrintWriter pw, Sense sense) {
        pw.println(String.format("<ids>%s</ids>", sense.getId()));
        pw.print("<def>");
        pw.print(sense.getSemanticSignature().toString());
        pw.println("</def>");
    }
}
