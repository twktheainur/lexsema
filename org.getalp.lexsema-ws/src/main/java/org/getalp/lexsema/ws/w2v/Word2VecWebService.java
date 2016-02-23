package org.getalp.lexsema.ws.w2v;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.getalp.lexsema.ws.core.WebServiceServlet;
import org.nd4j.linalg.api.ndarray.INDArray;

public class Word2VecWebService extends WebServiceServlet
{
    private static final String default_path = "/home/viall/current/data/word2vec/model_large.bin";
    
    private static WordVectors w2v = null;
    
    private static double[][] vectors = null;
    
    private static String[] words = null;
    
    private static HashMap<String, Integer> wordsIndexes = null;
    
    private static boolean loaded = false;
    
    protected void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        setHeaders(request, response);
        String what = request.getParameter("what");
        if (what == null)
        {
            handleWhatNull(request, response);
        }
        else if (what.equals("get_word_vector"))
        {
            handleGetWordVector(request, response);
        }
        else if (what.equals("get_most_similar_words"))
        {
            handleGetMostSimilarWords(request, response);
        }
        else if (what.equals("load_model"))
        {
            handleLoadModel(request, response);
        }
        else if (what.equals("load_default_model"))
        {
            handleLoadDefaultModel(request, response);
        }
        else
        {
            handleWhatInvalid(request, response);
        }
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
    
    private void handleWhatNull(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        writeErrorParameterNull(response, "what");
    }
    
    private void handleGetWordVector(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        if (!loadWord2vec(default_path, false)) writeErrorWord2vecNotLoaded(response);
        String word = request.getParameter("word");
        if (word == null) { writeErrorParameterNull(response, "word"); return; }
        double[] vector = vectors[wordsIndexes.get(word)];
        response.getWriter().println(Arrays.toString(vector));
    }
    
    private void handleGetMostSimilarWords(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        if (!loadWord2vec(default_path, false)) writeErrorWord2vecNotLoaded(response);
        String word = request.getParameter("word");
        String vector = request.getParameter("vector");
        if (word == null && vector == null) { writeErrorParameterNull(response, "word / vector"); return; }
        String nAsStr = request.getParameter("n");
        if (nAsStr == null) nAsStr = "1";
        int n = Integer.parseInt(nAsStr);
        if (word != null)
        {
            Collection<String> most_similar_words = getMostSimilarWords(word, n);
            response.getWriter().println(most_similar_words.toString());
        }
        else if (vector != null)
        {
            String[] strValues = vector.replace("[", "").replace("]", "").split(", ");
            double[] vectord = new double[strValues.length];
            for (int i = 0 ; i < vectord.length ; i++) vectord[i] = Double.parseDouble(strValues[i]);
            Collection<String> most_similar_words = getMostSimilarWords(vectord, n);
            response.getWriter().println(most_similar_words.toString());
        }
    }
    
    private void handleLoadModel(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String path = request.getParameter("path");
        if (path == null) { writeErrorParameterNull(response, "path"); return; }
        if (!loadWord2vec(path, true)) response.getWriter().println("fail");
        else response.getWriter().println("success");
    }

    private void handleLoadDefaultModel(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        if (!loadWord2vec(default_path, true)) response.getWriter().println("fail");
        else response.getWriter().println("success");
    }

    private void handleWhatInvalid(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.getWriter().println("Error: parameter \"what\" invalid.");
    }
    
    private void writeErrorWord2vecNotLoaded(HttpServletResponse response) throws IOException
    {
        response.getWriter().println("Error: word2vec is not loaded.");
    }
    
    private void writeErrorParameterNull(HttpServletResponse response, String parameterName) throws IOException
    {
        response.getWriter().println("Error: parameter \"" + parameterName + "\" missing.");
    }
    
    private Collection<String> getMostSimilarWords(String zeWord, int topN) 
    {
        System.out.println("similarz ");
        System.out.println(Arrays.toString(w2v.wordsNearest(zeWord, topN).toArray(new String[topN])));
        return getMostSimilarWords(vectors[wordsIndexes.get(zeWord)], topN);
    }
    
    private double dot_product(double[] a, double[] b) 
    {
        double ret = 0;
        for (int i = 0 ; i < a.length ; i++) 
        {
            ret += a[i] * b[i];
        }
        return ret;
    }
    
    private Collection<String> getMostSimilarWords(double[] zeWord, int topN) 
    {
        Stuff[] zenearests = new Stuff[topN];
        for (int i = 0 ; i < topN ; i++) zenearests[i] = new Stuff(0.0, 0);
        int nbOfVectors = vectors.length;
        for (int j = 0 ; j < nbOfVectors ; j++) 
        {
            double[] v = vectors[j];
            double sim = dot_product(zeWord, v);
            if (sim > zenearests[0].sim) 
            {
                zenearests[0].sim = sim; 
                zenearests[0].index = j;
                Arrays.sort(zenearests);
            }
        }
        List<String> zenearestsstr = new ArrayList<>();
        for (int i = topN - 1 ; i >= 0 ; i--) 
        {
            zenearestsstr.add(words[zenearests[i].index]);
        }
        return zenearestsstr;
    }
    
    private static class Stuff implements Comparable<Stuff> 
    {
        public Double sim;
        public Integer index;
        public Stuff(double sim, int index) 
        {
            this.sim = sim;
            this.index = index;
        }
        public int compareTo(Stuff o) 
        {
            return sim.compareTo(o.sim);
        }
    }
    
    private static synchronized boolean loadWord2vec(String path, boolean reload)
    {
        if (loaded && !reload) return true;
        try
        {
            w2v = WordVectorSerializer.loadGoogleModel(new File(path), true, false);
            int nbWords = w2v.vocab().words().size();
            int vectorDimension = w2v.lookupTable().getWeights().columns();
            words = new String[nbWords];
            wordsIndexes = new HashMap<>();
            vectors = new double[nbWords][vectorDimension];
            int last_percentage = 0;
            for (int i = 0 ; i < nbWords ; i++) {
                int current_percentage = ((int) ((((double) (i + 1)) / ((double) (nbWords))) * 100.0));
                if (current_percentage > last_percentage) System.out.println("Adding words... (" + current_percentage + "%)\r");
                last_percentage = current_percentage;
                words[i] = w2v.vocab().wordAtIndex(i);
                wordsIndexes.put(words[i], i);
                INDArray ndarray = w2v.lookupTable().getWeights().vectorAlongDimension(i, 1);
                for (int j = 0 ; j < vectorDimension ; j++)
                {
                    vectors[i][j] = ndarray.getDouble(j);
                }
                if (words[i].equals("Hopital_Europeen_Georges_Pompidou"))
                {
                    System.out.println("index " + i);
                    System.out.println("Hopital_Europeen_Georges_Pompidou vector");
                    System.out.println(Arrays.toString(w2v.getWordVector("Hopital_Europeen_Georges_Pompidou")));
                    System.out.println("My Hopital_Europeen_Georges_Pompidou vector");
                    System.out.println(Arrays.toString(vectors[i]));
                }
            }
            loaded = true;
        } 
        catch (IOException e)
        {
            e.printStackTrace();
            loaded = false;
        }
        return loaded;
    }
}
