package org.getalp.lexsema.wsd.experiments;

import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;

import java.io.FileInputStream;
import java.io.PrintWriter;

public class GenerateRawSemeval2007Task7 
{
    public static String semeval2007task7Path = "../data/senseval2007_task7/test/eng-coarse-all-words.xml";
    
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

	public static void main(String[] args) throws Exception 
	{    
		CorpusLoader corpus = new Semeval2007CorpusLoader(new FileInputStream(semeval2007task7Path)).loadNonInstances(true);
        corpus.load();
        PrintWriter pwraw = new PrintWriter("../data/senseval2007_task7/semeval2007task7.raw");
        PrintWriter pwlem = new PrintWriter("../data/senseval2007_task7/semeval2007task7.lem");
        for (Text t : corpus)
        {
        	for (Sentence s : t.sentences())
        	{
            	for (Word w : s)
            	{
            		pwraw.print(surfaceForm(w) + " ");
            		pwlem.print(lemma(w) + " ");
            	}
            	pwraw.println();
            	pwlem.println();
        	}
        }
        pwraw.close();
        pwlem.close();
	}
}
