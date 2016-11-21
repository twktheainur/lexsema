package org.getalp.lexsema.io.document.loader;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import org.getalp.lexsema.similarity.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class OldDSOCorpusLoader extends CorpusLoaderImpl
{

    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.DEFAULT;

    private final String pathToDSO;

    private final Dictionary wordnet;
    
    private Text text;
    
    public OldDSOCorpusLoader(String pathToDSO, String pathToWordnet)
    {
        this.pathToDSO = pathToDSO;
        wordnet = new Dictionary(new File(pathToWordnet));
    }
    
    private static void open(Dictionary wordnet)
    {
        try
        {
            wordnet.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void processList(String listFileName, String pos)
    {
        System.out.println("[DSO] Processing file " + listFileName + "...");
        File listFile = new File(pathToDSO + File.separator + listFileName);
        try (BufferedReader br = new BufferedReader(new FileReader(listFile)))
        {
            for (String line = br.readLine() ; line != null ; line = br.readLine())
            {
                processWordInList(line, pos);
            }
            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void processWordInList(String word, String pos)
    {
        System.out.println("[DSO] Processing word " + word + "...");
        File wordFile = new File(pathToDSO + File.separator + word + "." + pos);
        try (BufferedReader br = new BufferedReader(new FileReader(wordFile)))
        {
            for (String line = br.readLine() ; line != null ; line = br.readLine())
            {
                if (line.isEmpty()) continue;
                processSentenceInWord(line, word, pos);
            }
            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void processSentenceInWord(String sentence, String wordLemma, String wordPOS)
    {
        try (Scanner scan = new Scanner(sentence)) {
            scan.next(); // get rid of the file identification
            scan.next(); // get rid of the sentence number
            int senseNumber = 0;
            Sentence txtSentence = DOCUMENT_FACTORY.createSentence("");
            while (scan.hasNext()) {
                String tmp = scan.next();
                if (tmp.equals(">>")) {
                    String surfaceForm = scan.next();
                    senseNumber = scan.nextInt();
                    scan.next(); // get rid of "<<"
                    Word txtWord = DOCUMENT_FACTORY.createWord("", wordLemma, surfaceForm, wordPOS);
                    txtWord.setSemanticTag(getSemanticTag(wordLemma, wordPOS, senseNumber));
                    txtWord.setEnclosingSentence(txtSentence);
                    txtSentence.addWord(txtWord);
                } else {
                    Word txtWord = DOCUMENT_FACTORY.createWord("", "", tmp, "");
                    txtWord.setEnclosingSentence(txtSentence);
                    txtSentence.addWord(txtWord);
                }
            }
            text.addSentence(txtSentence);
            scan.close();
        }
    }
    
    private String getSemanticTag(String lemma, String pos, int senseNumber)
    {   
        if (senseNumber <= 0) return "";
        IIndexWord iw = wordnet.getIndexWord(lemma, getWordnetPOS(pos));
        if (iw.getWordIDs().size() <= senseNumber - 1) return "";
        IWordID wordID = iw.getWordIDs().get(senseNumber - 1);
        IWord word = wordnet.getWord(wordID);
        String senseKey = word.getSenseKey().toString();
        return senseKey.substring(senseKey.indexOf("%") + 1);
    }
    
    private POS getWordnetPOS(String pos)
    {
        if (pos.equals("v")) return POS.VERB;
        if (pos.equals("n")) return POS.NOUN; 
        return null;
    }

    @Override
    public void load()
    {
        open(wordnet);
        text = DOCUMENT_FACTORY.createText();
        text.setId("");
        processList("vlist.txt", "v");
        processList("nlist.txt", "n");
        addText(text);
    }

    @Override
    public CorpusLoader loadNonInstances(boolean loadExtra)
    {
        return this;
    }

}