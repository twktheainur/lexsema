package org.getalp.lexsema.io.dictionary;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DocumentDictionaryWriter implements DictionaryWriter {

    private static final Logger logger = LoggerFactory.getLogger(DocumentDictionaryWriter.class);

    List<Text> documents;


    @SuppressWarnings("unused")
    public DocumentDictionaryWriter(List<Text> documents) {
        this.documents = Collections.unmodifiableList(documents);
    }

    @SuppressWarnings("unused")
    public DocumentDictionaryWriter(Text d) {
        documents = new ArrayList<>();
        documents.add(d);

    }

    public DocumentDictionaryWriter(Iterable<Text> loader) {
        documents = new ArrayList<>();
        for (Text document : loader) {
            documents.add(document);
        }
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public void writeDictionary(File dictionary) {

        try (PrintWriter printWriter = new PrintWriter(dictionary)) {
            printWriter.println("<dict>");
            for (Document document : documents) {
                for (int wordIndex = 0; wordIndex < document.size(); wordIndex++) {
                    Word w = document.getWord(0, wordIndex);
                    writeWordStartTag(printWriter, w);
                    for (Sense sense : document.getSenses(wordIndex)) {
                        writeSenseStartTag(printWriter);
                        writeSenseContent(printWriter, sense);
                        writeSenseEndTag(printWriter);
                    }
                    writeWordEndTag(printWriter);
                    logger.trace("Word {} ({}/{})", w.getLemma() ,wordIndex, document.size());
                }
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
