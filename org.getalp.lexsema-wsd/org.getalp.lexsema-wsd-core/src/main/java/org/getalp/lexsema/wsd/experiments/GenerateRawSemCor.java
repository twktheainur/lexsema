package org.getalp.lexsema.wsd.experiments;

import java.io.PrintWriter;

import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.SemCorCorpusLoader;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;

public class GenerateRawSemCor 
{
    public static String semcor21Path = "../data/semcor/2.1/all.xml";

    public static SemCorCorpusLoader semCor21 = new SemCorCorpusLoader(semcor21Path);

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
    	if (tag.equals("")) return surfaceForm(word);
    	tag = lemma + "%" + tag;
    	tag = tag.replace("%5", "%3");
    	return tag;
    }

	public static void main(String[] args) throws Exception 
	{
        semCor21.loadPunctuation(true);
        semCor21.load();
        PrintWriter pwraw = new PrintWriter("../data/semcor/2.1/semcor.raw");
        PrintWriter pwlem = new PrintWriter("../data/semcor/2.1/semcor.lem");
        PrintWriter pwtag = new PrintWriter("../data/semcor/2.1/semcor.tag");
        for (Text t : semCor21)
        {
        	for (Sentence s : t.sentences())
        	{
            	for (Word w : s)
            	{
            		pwraw.print(surfaceForm(w) + " ");
            		pwlem.print(lemma(w) + " ");
            		pwtag.print(tag(w) + " ");
            	}
            	pwraw.println();
            	pwlem.println();
            	pwtag.println();
        	}
        }
        pwraw.close();
        pwlem.close();
        pwtag.close();
	}
}
