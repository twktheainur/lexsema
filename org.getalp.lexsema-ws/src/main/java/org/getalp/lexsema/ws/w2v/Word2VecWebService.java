package org.getalp.lexsema.ws.w2v;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.getalp.lexsema.ws.core.WebServiceServlet;
import org.nd4j.linalg.api.ndarray.INDArray;

public class Word2VecWebService extends WebServiceServlet
{
    private static WordVectors word2vec = null; 
        
    private static final String default_path = "/home/viall/current/data/word2vec/model_large.bin";
    
    private static double[][] vectors = null;
    
    private static String[] words = null;
    
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
        if (word2vec == null) { writeErrorWord2vecNotLoaded(response); return; }
        String word = request.getParameter("word");
        if (word == null) { writeErrorParameterNull(response, "word"); return; }
        double[] vector = word2vec.getWordVector(word);
        response.getWriter().println(Arrays.toString(vector));
    }
    
    private void handleGetMostSimilarWords(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        if (word2vec == null) { writeErrorWord2vecNotLoaded(response); return; }
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
        word2vec = loadWord2vec(path);
        if (word2vec == null) response.getWriter().println("fail");
        else response.getWriter().println("success");
    }

    private void handleLoadDefaultModel(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        word2vec = loadWord2vec(default_path);
        if (word2vec == null) response.getWriter().println("fail");
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
        return getMostSimilarWords(word2vec.getWordVectorMatrix(zeWord), topN);
    }
    
    private double dot_product(double[] a, double[] b) {
        double ret = 0;
        for (int i = 0 ; i < a.length ; i++) {
            ret += a[i] * b[i];
        }
        return ret;
    }
    
    private double[] toDoubleArray(INDArray ndarray) {
        int length = ndarray.length();
        double[] res = new double[length];
        for (int i = 0 ; i < length ; i++) {
            res[i] = ndarray.getDouble(i);
        }
        return res;
    }

    private Collection<String> getMostSimilarWords(double[] zeWord, int topN) 
    {
        Stuff[] zenearests = new Stuff[topN];
        for (int i = 0 ; i < topN ; i++) zenearests[i] = new Stuff(0.0, 0);

        int nbOfVectors = vectors.length;
        int nbOfThreads = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[nbOfThreads];
        int nbOfVectorsPerThread = nbOfVectors / nbOfThreads;
        int nbOfVectorsRemaining = nbOfVectors - (nbOfVectorsPerThread * nbOfThreads);
        
        for (int i = 0 ; i < nbOfThreads ; i++) {
            int min = i * nbOfVectorsPerThread;
            threads[i] = new Thread() {
                public void run() {
                    for (int j = min ; j < min + nbOfVectorsPerThread ; j++) {
                        double[] v = vectors[j];
                        double sim = dot_product(zeWord, v);
                        synchronized(zenearests) {
                            if (sim > zenearests[0].sim) {
                                zenearests[0].sim = sim; 
                                zenearests[0].index = j;
                                Arrays.sort(zenearests);
                            }
                        }
                    }
                }
            };
        }

        for (Thread thread : threads) {
            thread.start();
        }

        int min = nbOfThreads * nbOfVectorsPerThread;
        for (int j = min ; j < min + nbOfVectorsRemaining ; j++) {
            double[] v = vectors[j];
            double sim = dot_product(zeWord, v);
            synchronized(zenearests) {
                if (sim > zenearests[0].sim) {
                    zenearests[0].sim = sim; 
                    zenearests[0].index = j;
                    Arrays.sort(zenearests);
                }
            }
        }
        
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new Error(e);
            }
        }
        
        List<String> zenearestsstr = new ArrayList<>();
        for (Stuff pair : zenearests) 
        {
            zenearestsstr.add(words[pair.index]);
        }
        return zenearestsstr;
    }
    
    private Collection<String> getMostSimilarWords(INDArray zeWord, int topN) 
    {
        return getMostSimilarWords(toDoubleArray(zeWord), topN);
    }

    private static class Stuff implements Comparable<Stuff> 
    {
        public Double sim;
        public Integer index;
        public Stuff(double dbl, int index) 
        {
            this.sim = dbl;
            this.index = index;
        }
        public int compareTo(Stuff o) 
        {
            return sim.compareTo(o.sim);
        }
    }
    
    private static WordVectors loadWord2vec(String path)
    {
        try
        {
            WordVectors w2v = WordVectorSerializer.loadGoogleModel(new File(path), true, false);
            int nbWords = w2v.vocab().words().size();
            words = new String[nbWords];
            for (int i = 0 ; i < nbWords ; i++) {
                words[i] = w2v.vocab().wordAtIndex(i);
            }
            int vectorDimension = w2v.lookupTable().getWeights().columns();
            vectors = new double[nbWords][vectorDimension];
            for (int i = 0 ; i < nbWords ; i++) {
                for (int j = 0 ; j < vectorDimension ; j++) {
                    vectors[i][j] = w2v.lookupTable().getWeights().slice(i).getDouble(j);
                }
            }
            return w2v;
        } 
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
