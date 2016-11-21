package org.getalp.lexsema.io.document.writer;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.util.Language;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

public class XMLCorpusWriter implements CorpusWriter{


    private final Language language;

    public XMLCorpusWriter(Language language) {
        this.language = language;
    }

    @Override
    public void writeCorpus(Path file, Iterable<Text> texts) throws IOException {
        try(PrintWriter corpusPrintWriter = new PrintWriter(Files.newOutputStream(file))) {
            corpusPrintWriter.println(String.format("\t<corpus lang=\"%s\">",language));
            for (Text text : texts) {
                writeText(text, corpusPrintWriter);
            }
            corpusPrintWriter.println("\t</corpus>");
        }
    }


    private void writeText(Document text, PrintWriter corpusPrintWriter) {
        corpusPrintWriter.println(String.format("\t<text id=\"%s\"",text.getId()));
        for (Word word : text) {
            writeWord(word, corpusPrintWriter);
        }
        corpusPrintWriter.println("\t</text>");
    }

    private void writeWord(Word word, PrintWriter corpusPrintWriter) {
        String lemma = word.getLemma();
        String surfaceForm = word.getSurfaceForm();
        String pos = word.getPartOfSpeech();
        if(!pos.equals("SENT") || !pos.equals("PUNC")) {
            corpusPrintWriter
                    .println(MessageFormat
                            .format("\t\t\t\t<wf lemma=\"{0}\" pos=\"{1}\">{3}</wf>",
                                    lemma, pos, surfaceForm));
        } else {
            corpusPrintWriter.println(String.format("%s\n", surfaceForm));
        }
    }
}
