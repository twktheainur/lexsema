package org.getalp.lexsema.wsd.method;

import java.util.*;
import org.getalp.lexsema.similarity.*;
import org.getalp.lexsema.similarity.signatures.DefaultSemanticSignatureFactory;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.StopList;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.tartarus.snowball.ext.EnglishStemmer;

public class SimplifiedLeskDisambiguator implements Disambiguator
{
	
	public List<String> getWordsInSameSentence(Word word)
	{
		List<String> ret = new ArrayList<String>();
		Sentence sentence = word.getEnclosingSentence();
		for (Word w : sentence)
		{
			if (w != word)
			{
				ret.add(w.getSurfaceForm());
			}
		}
		return ret;
	}
	
	public String stemstem(String word)
	{
	    EnglishStemmer stemmer = new EnglishStemmer();
	    stemmer.setCurrent(word);
	    stemmer.stem();
	    return stemmer.getCurrent();
	}
	
    public int computeOverlap(Sense sense, List<String> sentenceWords)
	{
		int overlap = 0;
		SemanticSignature semsig = sense.getSemanticSignature();
		for (SemanticSymbol sym : semsig.getSymbols())
		{
			if (StopList.isStopWord(sym.getSymbol())) continue;
			for (String sentenceWord : sentenceWords)
			{
				if (StopList.isStopWord(sentenceWord)) continue;
				sentenceWord = stemstem(sentenceWord);
				if (sym.getSymbol().equals(sentenceWord))
				{
					overlap++;
				}
			}
		}
		return overlap;
	}

	@Override
	public Configuration disambiguate(Document document)
	{
		ContinuousConfiguration ret = new ContinuousConfiguration(document, 0);
		for (int i = 0 ; i < document.words().size() ; i++)
		{
			Word word = document.getWord(i);
			List<String> sentenceWords = getWordsInSameSentence(word);
			int assignment = -1;
			int overlapMax = -1;
			List<Sense> senses = document.getSenses(i);
			for (int j = 0 ; j < senses.size(); j++)
			{
				Sense sense = senses.get(j);
				int overlap = computeOverlap(sense, sentenceWords);
				if (overlap > overlapMax)
				{
					overlapMax = overlap;
					assignment = j;
				}
			}
			ret.setSense(i, assignment);
		}
		return ret;
	}

	@Override
	public Configuration disambiguate(Document document, Configuration c) 
	{
		return disambiguate(document);
	}

	@Override
	public void release() 
	{
		
	}

}
