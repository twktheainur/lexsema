package org.getalp.lexsema.wsd.experiments;

import java.io.FileInputStream;
import java.io.PrintWriter;

import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;

public class GenerateRawSemeval2007Task7 
{
    public static String semeval2007task7Path = "../data/senseval2007_task7/test/eng-coarse-all-words.xml";
    
    public static String clean(String str)
    {
    	if (str == null) str = "";
    	str = str.toLowerCase();
    	str = str.replace("%", "");
    	str = str.replace(" ", "");
    	return str;
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
    
    public static String id(Word word)
    {
    	String id = clean(word.getId());
    	if (id.equals("non-target")) return lemma(word) + "%nt";
    	return lemma(word) + "%" + id;
    }

	public static void main(String[] args) throws Exception 
	{    
		CorpusLoader corpus = new Semeval2007CorpusLoader(new FileInputStream(semeval2007task7Path)).loadNonInstances(true);
        corpus.load();
        PrintWriter pwraw = new PrintWriter("../data/senseval2007_task7/semeval2007task7.raw");
        PrintWriter pwlem = new PrintWriter("../data/senseval2007_task7/semeval2007task7.lem");
        PrintWriter pwids = new PrintWriter("../data/senseval2007_task7/semeval2007task7.ids");
        for (Text t : corpus)
        {
        	for (Sentence s : t.sentences())
        	{
            	for (Word w : s)
            	{
            		pwraw.print(surfaceForm(w) + " ");
            		pwlem.print(lemma(w) + " ");
            		pwids.print(id(w) + " ");
            	}
            	pwraw.println();
            	pwlem.println();
            	pwids.println();
        	}
        }
        pwraw.close();
        pwlem.close();
        pwids.close();
	}
}
