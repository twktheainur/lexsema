package org.getalp.lexsema.ws.wsdforsmt;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.getalp.lexsema.io.text.DicollecteFrenchLemmatizer;
import org.getalp.lexsema.ws.core.WebServiceServlet;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetConfiguration;
import it.uniroma1.lcl.babelnet.BabelSynset;

@SuppressWarnings("serial")
public class WSDForSMTWebService2  extends WebServiceServlet
{
	private static BabelNet babelnet = null;
	
	private static DicollecteFrenchLemmatizer lemmatizer = null;
	
	private static boolean verbose = false;

    private static Map<String, Integer> cache = new HashMap<>();
    
	protected void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		setHeaders(request, response);
		loadBabelnet();
		loadLemmatizer();
		
		String firstArg = request.getParameter("first");
		if (verbose) System.out.println("First arg : " + firstArg);
		
		String secondArg = request.getParameter("second");
		if (verbose) System.out.println("Second arg : " + secondArg);

        int score = 0;
        String key = firstArg + secondArg;
        
        if (cache.containsKey(key))
        {
            if (verbose) System.out.println("Found in cache");
            score = cache.get(key);
        }
        else
	    {
			String[] firsts = firstArg.split(", ");
			List<BabelSynset> synsets = new ArrayList<>();
			for (String wordnetID : firsts)
			{
				List<BabelSynset> tmp = babelnet.getSynsetsFromWordNetOffset(wordnetID); 
				if (tmp != null) synsets.addAll(tmp);
			}
			
	        String[] seconds = secondArg.split(", ");
			
	        for (String word : seconds)
	        {
	        	List<String> lemmas = lemmatizer.getLemmas(word);
	        	for (String lemma : lemmas)
	        	{
	        	    for (edu.mit.jwi.item.POS pos : edu.mit.jwi.item.POS.values())
	        	    {
	                	for (BabelSynset synset : babelnet.getSynsets(it.uniroma1.lcl.jlt.util.Language.FR, lemma, pos))
	                	{
	                		if (synsets.contains(synset))
	                		{
	                			score++;
	                		}
	                	}
	        	    }
	        	}
	        }
	        
	        cache.put(key, score);
	    }
        
        response.getWriter().print(score);
		
		response.getWriter().close();
	}

	private void setHeaders(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException
	{        
		request.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "*");
		response.setHeader("Access-Control-Allow-Headers", "*");
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
	}

	private static synchronized void loadBabelnet()
	{
		if (babelnet != null) return;
        BabelNetConfiguration.getInstance().setConfigurationFile(new File("/home/viall/current/data/babelnet/2.5.1/babelnet.properties"));
        babelnet = BabelNet.getInstance();
	}
	
	private static synchronized void loadLemmatizer()
	{
	    if (lemmatizer != null) return;
	    lemmatizer = new DicollecteFrenchLemmatizer("/home/viall/current/data/dicollecte/lexique-dicollecte-fr-v5.6.txt");
	}

}