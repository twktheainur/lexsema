package org.getalp.lexsema.io.document.writer;

import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.MessageFormat;

public class SemCorCorpusWriter implements CorpusWriter {


    public SemCorCorpusWriter() {
    }


    @Override
    public void writeCorpus(File outputFile, Iterable<Text> texts) throws FileNotFoundException {
        try (PrintWriter corpusPrintWriter = new PrintWriter(outputFile)) {
            corpusPrintWriter.println("\t<semcor>");
            for(Text text: texts){
                writeText(text, corpusPrintWriter);
            }
            corpusPrintWriter.println("\t</semcor>");
        }

    }

    private void writeText(Text text, PrintWriter corpusPrintWriter){
        corpusPrintWriter.println("\t<context filename=\"\" paras=\"yes\">");
        int sentenceNumber = 0;
        for(Sentence sentence: text.sentences()){
            writeSentence(sentence,corpusPrintWriter,sentenceNumber);
            sentenceNumber++;
        }
        corpusPrintWriter.println("\t</context>");
    }

    private void writeSentence(Iterable<Word> sentence, PrintWriter corpusPrintWriter, int sentenceNumber){

        corpusPrintWriter.println(MessageFormat.format("\t\t<p pnum=\"{0}\">", String.valueOf(sentenceNumber)));
        corpusPrintWriter.println(MessageFormat.format("\t\t\t<s snum=\"{0}\">", String.valueOf(sentenceNumber)));
        for(Word word: sentence) {
            writeWord(word, corpusPrintWriter);
        }
        corpusPrintWriter.println("\t\t\t</s>");
        corpusPrintWriter.println("\t\t</p>");
    }

    private void writeWord(Word word, PrintWriter corpusPrintWriter) {
        String lemma = word.getLemma();
        String surfaceForm = word.getSurfaceForm();
        String pos = word.getPartOfSpeech();
        String senseTag = word.getSenseAnnotation();
        corpusPrintWriter
                .println(MessageFormat
                        .format("\t\t\t\t<wf cmd=\"done\" lemma=\"{0}\" pos=\"{1}\" lexsn=\"{2}\">{3}</wf>",
                                lemma, pos, senseTag, surfaceForm));
    }
}
