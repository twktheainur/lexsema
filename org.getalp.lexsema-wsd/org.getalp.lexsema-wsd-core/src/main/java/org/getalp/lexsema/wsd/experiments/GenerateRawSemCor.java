package org.getalp.lexsema.wsd.experiments;

import java.io.File;
import java.io.PrintWriter;

import org.getalp.lexsema.io.document.loader.SemCorCorpusLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.data.parse.*;
import edu.mit.jwi.item.ISenseEntry;
import edu.mit.jwi.item.ISenseKey;

public class GenerateRawSemCor 
{
    public static String semcor21Path = "../data/semcor/2.1/brown12.xml";

    public static SemCorCorpusLoader semCor21 = new SemCorCorpusLoader(semcor21Path);

    public static String wordnet21Path = "../data/wordnet/1.6/dict/";

    public static Dictionary wordnet21 = new Dictionary(new File(wordnet21Path));

    public static int notFound = 0;

    public static String clean(String word)
    {
    	if (word == null) word = "";
    	word = word.toLowerCase();
    	word = word.replace("%", "");
    	return word;
    }

    public static String surfaceForm(Word word)
    {
    	return clean(word.getSurfaceForm());
    }

    public static String lemma(Word word)
    {
    	String lemma = clean(word.getLemma());
    	if (lemma.equals("")) return surfaceForm(word);
    	return lemma;
    }

    public static String tag(Word word)
    {
    	String lemma = clean(word.getLemma());
    	if (lemma.equals("")) return surfaceForm(word);
    	String tag = clean(word.getSenseAnnotation());
    	if (tag.contains(";"))
    	{
    		tag = tag.substring(0, tag.indexOf(";"));
    	}
    	if (tag.equals("")) return surfaceForm(word);
    	tag = lemma + "%" + tag;
    	return tag;
    }
    
    public static String synset(Word word)
    {
    	String lemma = clean(word.getLemma());
    	if (lemma.equals("")) return surfaceForm(word);
    	String tag = clean(word.getSenseAnnotation());
    	if (tag.contains(";"))
    	{
    		tag = tag.substring(0, tag.indexOf(";"));
    	}
    	if (tag.equals("")) return surfaceForm(word);
    	tag = lemma + "%" + tag;
    	String synset = "";
    	ISenseEntry ise = wordnet21.getSenseEntry(SenseKeyParser.getInstance().parseLine(tag));
    	if (ise == null) 
    	{
    		notFound++;
    		System.out.println("Not found : " + tag + " (" + notFound + ")");
    		return lemma;
    	}
    	synset += ise.getPOS().getTag();
    	synset += String.format("%08d", ise.getOffset());
    	return synset;
    }

	public static void main(String[] args) throws Exception 
	{
        semCor21.loadPunctuation(true);
        semCor21.load();
        wordnet21.open();
        PrintWriter pwraw = new PrintWriter("../data/semcor/2.1/semcor.raw");
        PrintWriter pwlem = new PrintWriter("../data/semcor/2.1/semcor.lem");
        PrintWriter pwtag = new PrintWriter("../data/semcor/2.1/semcor.tag");
        PrintWriter pwsyn = new PrintWriter("../data/semcor/2.1/semcor.syn");
        for (Text t : semCor21)
        {
        	for (Sentence s : t.sentences())
        	{
            	for (Word w : s)
            	{
            		pwraw.print(surfaceForm(w) + " ");
            		pwlem.print(lemma(w) + " ");
            		pwtag.print(tag(w) + " ");
            		pwsyn.print(synset(w) + " ");
            	}
            	pwraw.println();
            	pwlem.println();
            	pwtag.println();
            	pwsyn.println();
        	}
        }
        pwraw.close();
        pwlem.close();
        pwtag.close();
        pwsyn.close();
	}
}
