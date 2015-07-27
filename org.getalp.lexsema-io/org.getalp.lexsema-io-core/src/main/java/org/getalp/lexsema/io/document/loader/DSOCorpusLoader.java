package org.getalp.lexsema.io.document.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.jwi.data.IHasLifecycle;
import org.getalp.lexsema.io.text.EnglishDKPTextProcessor;
import org.getalp.lexsema.io.text.TextProcessor;
import org.getalp.lexsema.similarity.*;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.*;
import org.getalp.lexsema.similarity.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DSOCorpusLoader extends CorpusLoaderImpl {
    private static final Logger logger = LoggerFactory.getLogger(DSOCorpusLoader.class);
    private final String pathToDSO;

    private static final Pattern SENSE_NUMBER_PATTERN = Pattern.compile(">>\\s(.*)\\s([0-9])+\\s<<");

    private final Dictionary wordnet;

    private final TextProcessor textProcessor = new EnglishDKPTextProcessor();

    public DSOCorpusLoader(String pathToDSO, String pathToWordnet) {
        this.pathToDSO = pathToDSO;
        wordnet = new Dictionary(new File(pathToWordnet));
    }

    private static void open(IHasLifecycle wordnet) {
        try {
            wordnet.open();
        } catch (IOException e) {
            logger.error(MessageFormat.format("Cannot open Wordnet:{0}", e.getLocalizedMessage()));
        }
    }

    private void processWordFiles(String listFileName, String pos) {
        logger.info(MessageFormat.format("[DSO] Processing file {0}...", listFileName));
        File listFile = new File(pathToDSO + File.separator + listFileName);
        try (BufferedReader br = new BufferedReader(new FileReader(listFile))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                processWordInList(line, pos);
            }
        } catch (IOException e) {
            logger.error(MessageFormat.format("Error while processing DSO list file:{0}", e.getLocalizedMessage()));
        }
    }

    private void processWordInList(String word, String pos) {
        logger.info(MessageFormat.format("[DSO] Processing word {0}...", word));
        File wordFile = new File(MessageFormat.format("{0}{1}{2}.{3}", pathToDSO, File.separator, word, pos));
        try (BufferedReader br = new BufferedReader(new FileReader(wordFile))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (!line.isEmpty()) {
                    processSentenceInWord(line, word, pos);
                }
            }
        } catch (IOException e) {
            logger.error(MessageFormat.format("Error while processing DSO list file:{0}", e.getLocalizedMessage()));
        }
    }

    @SuppressWarnings("FeatureEnvy")
    private void processSentenceInWord(String sentence, String wordLemma, String wordPOS) {
        Matcher mather = SENSE_NUMBER_PATTERN.matcher(sentence);
        String senseNumber = mather.group(1);
        int wordIndex = findWordIndex(sentence);
        Sentence txtSentence = getSentence(textProcessor.process(sentence, ""));
        setWordInfo(wordIndex, txtSentence, senseNumber, wordLemma, wordPOS);
    }

    Sentence getSentence(Text text){
        Sentence txtSentence = NullSentence
        for(Sentence snt: text.sentences()){
            txtSentence = snt;
        }
        return txtSentence;
    }

    private int findWordIndex(String sentence) {
        String[] sentenceTokens = sentence.split(" ");
        int wordIndex = 0;
        while (wordIndex < sentenceTokens.length && sentenceTokens[wordIndex].equals(">>")) {
            wordIndex++;
        }
        return wordIndex;
    }

    private void setWordInfo(int wordIndex, Sentence sentence, String tag, String lemma, String pos) {
        Word word = sentence.getWord(wordIndex);
       setEnclosingSentenceAndTag(word,sentence,tag, lemma, pos);
    }

    private void setEnclosingSentenceAndTag(Word word, Sentence sentence, String tag, String lemma, String pos){
        word.setLemma(lemma);
        word.setPartOfSpeech(pos);
        word.setEnclosingSentence(sentence);
        word.setSemanticTag(getSemanticTag(lemma,pos,Integer.valueOf(tag)));
    }

    private String getSemanticTag(String lemma, String pos, int senseNumber) {
        String tag = "";
        IIndexWord iw = wordnet.getIndexWord(lemma, getWordnetPOS(pos));
        final List<IWordID> wordIDs = iw.getWordIDs();
        if (senseNumber > 0 && wordIDs.size() > senseNumber - 1) {
            IWordID wordID = wordIDs.get(senseNumber - 1);
            IWord word = wordnet.getWord(wordID);
            String senseKey = word.getSenseKey().toString();
            tag = senseKey.substring(senseKey.indexOf("%") + 1);
        }
        return tag;
    }

    private POS getWordnetPOS(String pos) {
        POS foundPOS = null;
        if (pos.equals("v")) {
            foundPOS = POS.VERB;
        }
        if (pos.equals("n")) {
            foundPOS = POS.NOUN;
        }
        return foundPOS;
    }

    @Override
    public void load() {
        open(wordnet);
        Text text = new TextImpl();
        text.setId("");
        processWordFiles("vlist.txt", "v");
        //processWordFiles("nlist.txt", "n");
        addText(text);
    }

    @Override
    public CorpusLoader loadNonInstances(boolean loadExtra) {
        return this;
    }

}
