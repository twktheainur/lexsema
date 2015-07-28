package org.getalp.lexsema.io.document.loader;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.data.IHasLifecycle;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import org.getalp.lexsema.io.text.EnglishDKPTextProcessor;
import org.getalp.lexsema.io.text.TextProcessor;
import org.getalp.lexsema.similarity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DSOCorpusLoader extends CorpusLoaderImpl {
    private static final Logger logger = LoggerFactory.getLogger(DSOCorpusLoader.class);
    private final String pathToDSO;

    private final Dictionary wordnet;

    private final TextProcessor textProcessor = new EnglishDKPTextProcessor();
    private final Text text;


    public DSOCorpusLoader(String pathToDSO, String pathToWordnet) {
        this.pathToDSO = pathToDSO;
        wordnet = new Dictionary(new File(pathToWordnet));
        text = new TextImpl();
        text.setId("");
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
        Collection<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(listFile))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                lines.add(line);
            }
            lines.parallelStream().forEach(line -> processWordInList(line, pos));
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
                    processSentenceInWord(getSentence(textProcessor.process(line, "")));
                }
            }
        } catch (IOException e) {
            logger.error(MessageFormat.format("Error while processing DSO list file:{0}", e.getLocalizedMessage()));
        }
    }

    @SuppressWarnings("FeatureEnvy")
    private void processSentenceInWord(Document sentence) {
        int currentWord = 3;
        Sentence cleanSentence = new SentenceImpl(sentence.getId());
        while(currentWord<sentence.size()){
            Word word = sentence.getWord(currentWord);
            @SuppressWarnings("LawOfDemeter") String lemma = word.getLemma();
            @SuppressWarnings("LawOfDemeter") String pos = word.getPartOfSpeech();
            if(lemma.equals(">")){
                currentWord+=2;
                Word targetWord = sentence.getWord(currentWord);
                Word tag = sentence.getWord(currentWord + 1);
                processTargetWord(targetWord,tag);
                cleanSentence.addWord(targetWord);
                currentWord+=3;
            } else if(!Objects.equals(lemma, pos)){
                cleanSentence.addWord(word);
            }
            currentWord++;
        }
        synchronized (text) {
            text.addSentence(cleanSentence);
        }
    }

    Sentence getSentence(Text text){
        Sentence txtSentence = NullSentence.getInstance();
        for(Sentence snt: text.sentences()){
            txtSentence = snt;
        }
        return txtSentence;
    }


    private void processTargetWord(Word targetWord, Word tag){
        int senseNumber = -1;
        try {
            senseNumber = Integer.valueOf(tag.getSurfaceForm());
        } catch (NumberFormatException ignored){

        }
        String tagString = getSemanticTag(targetWord.getLemma(),targetWord.getPartOfSpeech(),senseNumber);
        targetWord.setSemanticTag(tagString);
    }
    private String getSemanticTag(String lemma, String pos, int senseNumber) {
        String tag = "";
        POS wnPos = getWordnetPOS(pos);
        if(wnPos!=null) {
            IIndexWord iw = wordnet.getIndexWord(lemma, wnPos);
            if(iw!=null) {
                final List<IWordID> wordIDs = iw.getWordIDs();
                if (senseNumber > 0 && wordIDs.size() > senseNumber - 1) {
                    IWordID wordID = wordIDs.get(senseNumber - 1);
                    IWord word = wordnet.getWord(wordID);
                    String senseKey = word.getSenseKey().toString();
                    tag = senseKey.substring(senseKey.indexOf("%") + 1);
                }
            }
        }
        return tag;
    }

    private POS getWordnetPOS(String pos) {
        final String s = pos.toLowerCase();
        char lPos = s.charAt(0);
        return POS.getPartOfSpeech(lPos);
    }

    @Override
    public void load() {
        open(wordnet);
        processWordFiles("vlist.txt", "v");
        //processWordFiles("nlist.txt", "n");
        addText(text);
    }

    @Override
    public CorpusLoader loadNonInstances(boolean loadExtra) {
        return this;
    }

}
