package org.getalp.lexsema.ws.w2v;

import java.io.File;
import java.io.IOException;
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
import org.nd4j.linalg.jblas.NDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

public class Word2VecWebService extends WebServiceServlet
{
    private static WordVectors word2vec = null; 
    
    private static boolean word2vecIsLoaded = false;
    
    private static final String default_path = "/home/viall/current/data/word2vec/model_large.bin";
    
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
    }

    private void setHeaders(HttpServletRequest request, HttpServletResponse response)
    {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setContentType("text");
    }
    
    private void handleWhatNull(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        writeErrorParameterNull(response, "what");
    }
    
    private void handleGetWordVector(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        if (!word2vecIsLoaded) { writeErrorWord2vecNotLoaded(response); return; }
        String word = request.getParameter("word");
        if (word == null) { writeErrorParameterNull(response, "word"); return; }
        double[] vector = word2vec.getWordVector(word);
        response.getWriter().println(Arrays.toString(vector));
    }
    
    private void handleGetMostSimilarWords(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        if (!word2vecIsLoaded) { writeErrorWord2vecNotLoaded(response); return; }
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

    private Collection<String> getMostSimilarWords(double[] zeWord, int topN) 
    {
        double[][] zeWord2D = new double[1][zeWord.length];
        zeWord2D[0] = zeWord;
        return getMostSimilarWords(new NDArray(zeWord2D), topN);
    }
    
    private Collection<String> getMostSimilarWords(INDArray zeWord, int topN) 
    {
        PairStringDouble[] zenearests = new PairStringDouble[topN];
        for (int i = 0 ; i < topN ; i++) zenearests[i] = new PairStringDouble("", 0.0);
        Collection<String> allWords = word2vec.vocab().words();
        for (String word : allWords) 
        {
            double sim = Transforms.cosineSim(zeWord, word2vec.getWordVectorMatrix(word));
            if (sim > zenearests[0].dbl) 
            {
                zenearests[0].dbl = sim; 
                zenearests[0].str = word;
                Arrays.sort(zenearests);
            }
        }
        List<String> zenearestsstr = new ArrayList<>();
        for (PairStringDouble pair : zenearests) 
        {
            zenearestsstr.add(pair.str);
        }
        return zenearestsstr;
    }

    private static class PairStringDouble implements Comparable<PairStringDouble> 
    {
        public String str;
        public Double dbl;
        public PairStringDouble(String str, Double dbl) 
        {
            this.str = str;
            this.dbl = dbl;
        }
        public int compareTo(PairStringDouble o) 
        {
            return dbl.compareTo(o.dbl);
        }
    }
    
    private static WordVectors loadWord2vec(String path)
    {
        try
        {
            return WordVectorSerializer.loadGoogleModel(new File(path), true, false);
        } 
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
