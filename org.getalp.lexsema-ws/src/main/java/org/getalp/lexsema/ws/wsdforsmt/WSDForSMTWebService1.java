package org.getalp.lexsema.ws.wsdforsmt;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.DocumentImpl;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.WordImpl;
import org.getalp.lexsema.similarity.measures.lesk.IndexedLeskSimilarity;
import org.getalp.lexsema.ws.core.WebServiceServlet;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.LargeDocumentDisambiguator;
import org.getalp.lexsema.wsd.method.MultiThreadCuckooSearch;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorerWithCache;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

@SuppressWarnings("serial")
public class WSDForSMTWebService1  extends WebServiceServlet
{
    private static StanfordCoreNLP stanford = null;
    
    private static DictionaryLRLoader dictionary = null;
    
    private static Disambiguator disambiguator = null;
    
    private static Map<String, String> cache = new HashMap<>();
    
    private static boolean verbose = true;
    
    protected void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        setHeaders(request, response);
        loadStanford();
        loadDictionary();
        loadDisambiguator();
        
        String rawText = request.getParameter("input");
        String output = "";
        
        if (cache.containsKey(rawText))
        {
            if (verbose) System.out.println("Found in cache");
            output = cache.get(rawText);
        }
        else
        {
            if (verbose) System.out.println("Got the following input of size " + rawText.length() + " characters:");
            if (verbose) System.out.println(rawText);

            List<String> tokenizedText = Arrays.asList(rawText.split(" "));
            
            if (verbose) System.out.println("Input has " + tokenizedText.size() + " words");
            
            if (verbose) System.out.println("Parsing input...");
            Document txt = rawToText(rawText);
            if (verbose) System.out.println("Parsed " + txt.size() + " words");

            if (verbose) System.out.println("Loading senses...");
            dictionary.loadSenses(txt);

            if (verbose) System.out.println("Disambiguating...");
            Configuration c = disambiguator.disambiguate(txt);
            disambiguator.release();
            
            if (verbose) System.out.println("Aligning disambiguation...");
            List<String> realDisambiguation = alignDisambiguation(txt, c, tokenizedText);
            
            if (verbose)
            {
                for (int i = 0 ; i < tokenizedText.size() ; i++) 
                {
                    System.out.println("Word " + i + " : \"" + tokenizedText.get(i) + "\" [" + realDisambiguation.get(i) + "]");
                }
            }
            output = Arrays.toString(realDisambiguation.toArray());
        }

        if (verbose) System.out.println("Writing output of size " + output.length() + "...");
        // response.setBufferSize(Integer.MAX_VALUE);
        response.getWriter().print(output);
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

    private static List<String> alignDisambiguation(Document currentText, Configuration currentDisambiguation, List<String> realText)
    {
        List<String> realDisambiguation = new ArrayList<>();
        final int window = 5;
        int i = 0;
        for (int k = 0 ; k < realText.size() ; k++)
        {
            int iBkp = i;
            boolean found = false;
            while (!found && (i - iBkp) < window && i < realText.size())
            {
                if (realText.get(k).contains(currentText.getWord(i).getSurfaceForm()) ||
                    currentText.getWord(i).getSurfaceForm().contains(realText.get(k)))
                {
                    found = true;
                }
                else
                {
                    i++;
                }
            }
            if (!found)
            {
                realDisambiguation.add("0");
                i = iBkp;
            }
            else
            {
                int assignment = currentDisambiguation.getAssignment(i);
                String senseID = (assignment >= 0) ? currentText.getSenses(i).get(assignment).getId() : "0";
                realDisambiguation.add(senseID);
                i++;
            }
        }    
        return realDisambiguation;
    }
    
    private static synchronized void loadStanford()
    {
        if (stanford != null) return;
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        props.put("tokenize.options", "untokenizable=allKeep");
        stanford = new StanfordCoreNLP(props);
    }

    private static synchronized void loadDictionary() throws Exception
    {
        if (dictionary != null) return;
        dictionary = new DictionaryLRLoader(new FileInputStream("/home/viall/current/data/lesk_dict/all/zebestalt"), true);
    }

    private static synchronized void loadDisambiguator()
    {
        if (disambiguator != null) return;
        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new IndexedLeskSimilarity());
        int iterations = 100000;
        double minLevyLocation = 1;
        double maxLevyLocation = 5;
        double minLevyScale = 0.5;
        double maxLevyScale = 1.5;
        disambiguator = new MultiThreadCuckooSearch(iterations, minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, scorer, false);   
        disambiguator = new LargeDocumentDisambiguator(disambiguator, 300, verbose);
    }

    private static Document rawToText(String raw)
    {
        Document txt = new DocumentImpl();
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