package org.getalp.lexsema.io.document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.SentenceImpl;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.TextImpl;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.WordImpl;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.*;

public class DSOTextLoader extends TextLoaderImpl
{
    private String pathToDSO;
    
    private Dictionary wordnet;
    
    private Text text;
    
    public DSOTextLoader(String pathToDSO, String pathToWordnet)
    {
        this.pathToDSO = pathToDSO;
        this.wordnet = new Dictionary(new File(pathToWordnet));
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void processSentenceInWord(String sentence, String wordLemma, String wordPOS)
    {
        Scanner scan = new Scanner(sentence);
        scan.next(); // get rid of the file identification
        scan.next(); // get rid of the sentence number
        int senseNumber = 0;
        Sentence txtSentence = new SentenceImpl("");
        while (scan.hasNext())
        {
            String tmp = scan.next();
            if (tmp.equals(">>"))
            {
                String surfaceForm = scan.next();
                senseNumber = scan.nextInt();
                scan.next(); // get rid of "<<"
                Word txtWord = new WordImpl("", wordLemma, surfaceForm, wordPOS);
                txtWord.setSemanticTag(getSemanticTag(wordLemma, wordPOS, senseNumber));
                txtWord.setEnclosingSentence(txtSentence);
                txtSentence.addWord(txtWord);
            }
            else
            {
                Word txtWord = new WordImpl("", "", tmp, "");
                txtWord.setEnclosingSentence(txtSentence);
                txtSentence.addWord(txtWord);
            }
        }
        text.addSentence(txtSentence);
        scan.close();
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

    public void load()
    {
        open(wordnet);
        text = new TextImpl();
        text.setId("");
        processList("vlist.txt", "v");
        //processList("nlist.txt", "n");
        addText(text);
    }

    public TextLoader loadNonInstances(boolean loadExtra)
    {
        return this;
    }

}
