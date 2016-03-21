package org.getalp.lexsema.wsd.experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.lesk.*;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.*;
import org.getalp.lexsema.wsd.score.*;

import edu.mit.jwi.item.POS;
import it.uniroma1.lcl.babelnet.BabelGloss;
import it.uniroma1.lcl.babelnet.BabelLicense;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetConfiguration;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSenseSource;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetSource;

import org.getalp.lexsema.io.resource.babelnet.*;

public class WSDForSMT2
{
	/**
	 * Input: Array of Wordnet senses (form [xxx, yyy, zzz]) following by array of words (same form) 
	 * Output: integer, score
	 */
    public static void main(String[] args) throws Exception
    {
		PrintStream stdout = System.out;
		System.setOut(new PrintStream(new File("/dev/null")));
		System.setErr(new PrintStream(new File("/dev/null")));
		
        BabelNetConfiguration.getInstance().setConfigurationFile(new File("../data/babelnet/2.5.1/babelnet.properties"));
        BabelNet babelnet = BabelNet.getInstance();
        
		StringBuilder argsInOneBuilder = new StringBuilder();
		for (String arg : args) {
			argsInOneBuilder.append(arg + " ");
		}
		String argsInOne = argsInOneBuilder.toString();
		
		String firstArg = argsInOne.substring(argsInOne.indexOf("[") + 1, argsInOne.indexOf("]"));
		String secondArg = argsInOne.substring(argsInOne.lastIndexOf("[") + 1, argsInOne.lastIndexOf("]"));
		
		String[] firsts = firstArg.split(", ");
		List<BabelSynset> synsets = new ArrayList<>();
		for (String wordnetID : firsts)
		{
			List<BabelSynset> tmp = babelnet.getSynsetsFromWordNetOffset(wordnetID); 
			if (tmp != null) synsets.addAll(tmp);
		}
        String[] seconds = secondArg.split(", ");
        int score = 0;
		
        for (String word : seconds)
        {
        	// si un des sens du mot est dans les firsts, score + 1
        	for (BabelSynset synset : babelnet.getSynsets(it.uniroma1.lcl.jlt.util.Language.FR, word))
        	{
        		if (synsets.contains(synset))
        		{
        			score++;
        		}
        	}
        }

        System.setOut(stdout);
        System.out.println(score);
    }
}
