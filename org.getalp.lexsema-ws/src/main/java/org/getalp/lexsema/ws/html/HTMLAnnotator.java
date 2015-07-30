package org.getalp.lexsema.ws.html;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.io.text.EnglishDKPTextProcessor;
import org.getalp.lexsema.io.text.TextProcessor;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.measures.lesk.AnotherLeskSimilarity;
import org.getalp.lexsema.ws.core.WebServiceServlet;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.CuckooSearchDisambiguator;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.MultiThreadConfigurationScorerWithCache;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

import edu.mit.jwi.Dictionary;

public class HTMLAnnotator extends WebServiceServlet
{
    private static final long serialVersionUID = 1L;
    
    private static final String wordnetPath = "/home/viall/current/data/wordnet/3.0/dict";
        
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        setHeaders(request, response);
        
        Dictionary wordnet = loadWordnet(wordnetPath);
        WordnetLoader wordnetloader = loadWordnetLoader(wordnet);
        TextProcessor txtProcessor = new EnglishDKPTextProcessor();
        
        String originalText = request.getParameter("i");
        String strippedText = stripHTMLTags(originalText);
        System.out.println("=== Original Text ===");
        System.out.println(originalText);
        System.out.println("=== Stripped Text ===");
        System.out.println(strippedText);

        String m = request.getParameter("m");
        int offsetAnnotation = m == null ? 0 : Integer.valueOf(m);

        Text txt = txtProcessor.process(strippedText, "");
        wordnetloader.loadSenses(txt);
        
        Configuration c = disambiguate(txt);
        Pair<String, Integer> ret = annotate(originalText, c, offsetAnnotation, wordnetloader);
        String annotatedText = ret.getLeft();
        int indexWordAnnotated = ret.getRight();

        String xmlText = constructXMLResponse(annotatedText, indexWordAnnotated);

        System.out.println("=== Returned Text ===");
        System.out.println(annotatedText);
        
        response.getOutputStream().write(xmlText.getBytes("UTF-8")); 
    }
    
    private void setHeaders(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
    }
    
    private String stripHTMLTags(String str)
    {
        return str.replaceAll("\\<.*?\\>", "");
    }
    
    private Configuration disambiguate(Text txt)
    {
        ConfigurationScorer scorer = new MultiThreadConfigurationScorerWithCache(new AnotherLeskSimilarity());
        CuckooSearchDisambiguator cuckooDisambiguator = new CuckooSearchDisambiguator(new StopCondition(StopCondition.Condition.SCORERCALLS, 100), 5, 0.5, 1, 0, scorer, true);
        Configuration c = cuckooDisambiguator.disambiguate(txt);
        cuckooDisambiguator.release();
        return c;
    }
    
    private Pair<String, Integer> annotate(String str, Configuration c, int offsetAnnotation, WordnetLoader wordnetloader)
    {
        String ret = str;
        int offset = 0;
        int indexWordAnnotated = 0;
        for (int i = 0 ; i < c.size() ; i++)
        {
            if (c.getAssignment(i) != -1)
            {
                Word word = c.getDocument().getWord(i);
                Sense sense = wordnetloader.getSenses(word).get(c.getAssignment(i));
                String definition = word.getLemma() + " : ";
                definition += sense.getSemanticSignature().toString();
                int beginOfWord = ret.indexOf(word.getSurfaceForm(), offset);
                if (beginOfWord == -1) continue;
                int endOfWord = beginOfWord + word.getSurfaceForm().length();
                offset = endOfWord;
                if (isInsideHTMLTag(ret, beginOfWord)) continue;
                if (isInsideLink(ret, beginOfWord)) continue;
                ret = ret.substring(0, beginOfWord) + 
                      "<span id=\"wsdword" + (indexWordAnnotated + offsetAnnotation) + "\">" + 
                      word.getSurfaceForm() + 
                      "</span>" + 
                      "<span id=\"wsdsense" + (indexWordAnnotated + offsetAnnotation) + "\">" +
                      definition + 
                      "</span>" + 
                      ret.substring(endOfWord);
                indexWordAnnotated++;
            }
        } 
        return new ImmutablePair<String, Integer>(ret, indexWordAnnotated);
    }
    
    private boolean isInsideLink(String str, int index)
    {
        boolean insideLink = false;
        for (int i = 0 ; i < index ; i++)
        {
            if (str.charAt(i) == '<' && str.charAt(i+1) == 'a')
            {
                insideLink = true;
            }
            else if (str.charAt(i) == '<' && 
                     str.charAt(i+1) == '/' && 
                     str.charAt(i+2) == 'a'&& 
                     str.charAt(i+3) == '>')
            {
                insideLink = false;
            }
        }
        return insideLink;
    }
    
    private boolean isInsideHTMLTag(String str, int index)
    {
        boolean insideHTMLTag = false;
        for (int i = 0 ; i < index ; i++)
        {
            if (str.charAt(i) == '<')
            {
                insideHTMLTag = true;
            }
            else if (str.charAt(i) == '>')
            {
                insideHTMLTag = false;
            }
        }
        return insideHTMLTag;
    }

    private String constructXMLResponse(String annotatedText, int indexWordAnnotated)
    {
        String ret = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
              "<root>" + 
              "<n>" + indexWordAnnotated + "</n>" +
              "<content><![CDATA[" + annotatedText + "]]></content>" +
              "</root>";
        return ret;
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
