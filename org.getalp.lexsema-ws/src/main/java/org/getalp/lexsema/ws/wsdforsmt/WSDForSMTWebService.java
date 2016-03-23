package org.getalp.lexsema.ws.wsdforsmt;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.getalp.lexsema.ws.core.WebServiceServlet;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetConfiguration;
import it.uniroma1.lcl.babelnet.BabelSynset;

public class WSDForSMTWebService  extends WebServiceServlet
{
	private static final BabelNet babelnet = loadBabelnet();
	
	private static BabelNet loadBabelnet()
	{
        BabelNetConfiguration.getInstance().setConfigurationFile(new File("/home/viall/current/data/babelnet/2.5.1/babelnet.properties"));
        BabelNet babelnet = BabelNet.getInstance();
        return babelnet;
	}

	protected void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		setHeaders(request, response);

		String firstArg = request.getParameter("first");
		String secondArg = request.getParameter("second");
	
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
        	for (BabelSynset synset : babelnet.getSynsets(it.uniroma1.lcl.jlt.util.Language.FR, word))
        	{
        		if (synsets.contains(synset))
        		{
        			score++;
        		}
        	}
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

}