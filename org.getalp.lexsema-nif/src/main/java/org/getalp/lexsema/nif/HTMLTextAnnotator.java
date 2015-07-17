package org.getalp.lexsema.nif;

import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.io.text.EnglishDKPTextProcessor;
import org.getalp.lexsema.io.text.TextProcessor;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.measures.lesk.AnotherLeskSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.CuckooSearchDisambiguator;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.MultiThreadConfigurationScorerWithCache;

import edu.mit.jwi.Dictionary;

public class HTMLTextAnnotator  extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    
    private static final String wordnetPath = "/home/coyl/lig/data/wordnet/3.0/dict";
    
    private static final Dictionary wordnet = loadWordnet(wordnetPath);

    private static final WordnetLoader wordnetloader = loadWordnetLoader(wordnet);
    
    private static final TextProcessor txtProcessor = new EnglishDKPTextProcessor();
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        handle(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        handle(request, response);
    }
    
    private void handle(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            handle2(request, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void handle2(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        String originalText = request.getParameter("i");
        System.out.println(originalText);
        Text txt = txtProcessor.process(originalText, "");
        wordnetloader.loadSenses(txt);
        ConfigurationScorer scorer = new MultiThreadConfigurationScorerWithCache(new AnotherLeskSimilarity());
        CuckooSearchDisambiguator cuckooDisambiguator = new CuckooSearchDisambiguator(new StopCondition(StopCondition.Condition.SCORERCALLS, 100), 5, 0.5, 1, 0, scorer, true);
        Configuration c = cuckooDisambiguator.disambiguate(txt);
        cuckooDisambiguator.release();
        String ret = originalText;
        int offset = 0;
        int indexWordAnnotated = 0;
        for (int i = 0 ; i < c.size() ; i++)
        {
            if (c.getAssignment(i) != -1)
            {
                Word word = txt.getWord(i);
                Sense sense = wordnetloader.getSenses(word).get(c.getAssignment(i));
                String definition = word.getLemma() + " : ";
                definition += sense.getSemanticSignature().toString();
                int beginOfWord = ret.indexOf(word.getSurfaceForm(), offset);
                int endOfWord = beginOfWord + word.getSurfaceForm().length();
                ret = ret.substring(0, beginOfWord) + 
                      "<span id=\"wsdword" + indexWordAnnotated + "\">" + 
                      word.getSurfaceForm() + 
                      "</span>" + 
                      "<div id=\"wsdsense" + indexWordAnnotated + "\">" +
                      definition + 
                      "</div>" + 
                      ret.substring(endOfWord);
                offset = endOfWord;
                indexWordAnnotated++;
            }
        }
        ret = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
              "<root>" + 
              "<n>" + indexWordAnnotated + "</n>" +
              "<content><![CDATA[" + ret + "]]></content>" +
              "</root>";
        response.getOutputStream().write(ret.getBytes("UTF-8"));
    }
    
    private static Dictionary loadWordnet(String wordnetPath)
    {
        Dictionary wordnet = null;
        URL wordnetURL = null;
        try
        {
            wordnetURL = new URL("file", null, wordnetPath);
            wordnet = new Dictionary(wordnetURL);
            wordnet.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return wordnet;
    }

    private static WordnetLoader loadWordnetLoader(Dictionary wordnet)
    {
        WordnetLoader wordnetLoader = new WordnetLoader(wordnet);
        wordnetLoader.loadDefinitions(true);
        return wordnetLoader;
    }
    
}
