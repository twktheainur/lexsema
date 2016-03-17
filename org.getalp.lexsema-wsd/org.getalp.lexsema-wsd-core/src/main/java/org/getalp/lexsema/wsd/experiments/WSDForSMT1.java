package org.getalp.lexsema.wsd.experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.io.text.EnglishDKPTextProcessor;
import org.getalp.lexsema.io.text.TextProcessor;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.DocumentImpl;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.SentenceImpl;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.TextImpl;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.WordImpl;
import org.getalp.lexsema.similarity.measures.lesk.IndexedLeskSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.MultiThreadCuckooSearch;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorerWithCache;

import cern.colt.Arrays;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.ISenseEntry;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class WSDForSMT1
{
	/**
	 * Input: <raw text> (e.g. "this is a cat")
	 * Output: Array of Wordnet senses (form [xxxx, yyyy, zzzz]) 
	 */
	public static void main(String[] args) throws Exception
	{		
		PrintStream stdout = System.out;
		System.setOut(new PrintStream(new File("/dev/null")));
		
		StringBuilder rawTextBuilder = new StringBuilder();
		for (String arg : args) {
			rawTextBuilder.append(arg + " ");
		}
		
		LRLoader lrloader = new DictionaryLRLoader(new FileInputStream("../data/lesk_dict/all/dict_all_stopwords_stemming_semcor_wordnetglosstag_250"), true);
		//TextProcessor txtProcessor = new EnglishDKPTextProcessor();
		//Text txt = txtProcessor.process(rawTextBuilder.toString(), "");
		Document txt = rawToText(rawTextBuilder.toString());
		//Sentence txts = txt.sentences().iterator().next();
		lrloader.loadSenses(txt);

        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new IndexedLeskSimilarity());
            
        int iterations = 100000;
        double minLevyLocation = 1;
        double maxLevyLocation = 5;
        double minLevyScale = 0.5;
        double maxLevyScale = 1.5;

        Disambiguator disambiguator = new MultiThreadCuckooSearch(iterations, minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, scorer, false);               
        Configuration c = disambiguator.disambiguate(txt);
        disambiguator.release();
        
        Dictionary wordnet = new Dictionary(new File("../data/wordnet/3.0/dict"));
        wordnet.open();
        
        String[] outputArray = new String[c.size()];
        for (int i = 0 ; i < c.size() ; i++) {
        	if (c.getAssignment(i) == -1) {
        		outputArray[i] = "0";
        	}
        	else {
	        	String senseID = txt.getSenses(i).get(c.getAssignment(i)).getId();
	        	Iterator<ISenseEntry> senseIterator = wordnet.getSenseEntryIterator();
	        	while (senseIterator.hasNext()) {
	        		ISenseEntry sense = senseIterator.next();
	        		if (sense.getSenseKey().toString().equals(senseID)) {
	        			outputArray[i] = Integer.toString(sense.getOffset()) + sense.getPOS().getTag();
	        		}
	        	}
        	}
        }
        System.setOut(stdout);
        System.out.println(Arrays.toString(outputArray));
	}

	private static Document rawToText(String raw)
	{
		Document txt = new DocumentImpl();
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP stanford = new StanfordCoreNLP(props);
		Annotation document = new Annotation(raw);
		stanford.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) 
		{
			for (CoreLabel token: sentence.get(TokensAnnotation.class))
			{
				String lemma = token.getString(LemmaAnnotation.class);
				String surfaceForm = token.originalText();
				String pos = token.getString(PartOfSpeechAnnotation.class);
				Word word = new WordImpl("", lemma, surfaceForm, pos);
				txt.addWord(word);
			}
		}
		return txt;
	}
}
