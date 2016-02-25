package org.getalp.lexsema.ws.w2v;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.getalp.lexsema.util.VectorOperation;
import org.getalp.lexsema.ws.core.WebServiceServlet;

public class Word2VecWebService extends WebServiceServlet
{
    private static final String default_path = "/home/viall/current/data/word2vec/model_large.bin";
    
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
        else if (what.equals("get_most_synonym_words"))
        {
            handleGetMostSynonymWords(request, response);
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
        if (!wordsIndexes.containsKey(word))
        {
            response.getWriter().print("[]");
        }
        else
        {
            double[] vector = vectors[wordsIndexes.get(word)];
            response.getWriter().print(Arrays.toString(vector));
        }
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
            response.getWriter().print(most_similar_words.toString());
        }
        else if (vector != null)
        {
            String[] strValues = vector.replace("[", "").replace("]", "").split(", ");
            double[] vectord = new double[strValues.length];
            for (int i = 0 ; i < vectord.length ; i++) vectord[i] = Double.parseDouble(strValues[i]);
            Collection<String> most_similar_words = getMostSimilarWords(vectord, n);
            response.getWriter().print(most_similar_words.toString());
        }
    }
    
    private void handleGetMostSynonymWords(HttpServletRequest request, HttpServletResponse response) throws IOException
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
            Collection<String> most_similar_words = getMostSynonymWords(word, n);
            response.getWriter().print(most_similar_words.toString());
        }
        else if (vector != null)
        {
            String[] strValues = vector.replace("[", "").replace("]", "").split(", ");
            double[] vectord = new double[strValues.length];
            for (int i = 0 ; i < vectord.length ; i++) vectord[i] = Double.parseDouble(strValues[i]);
            Collection<String> most_similar_words = getMostSynonymWords(vectord, n);
            response.getWriter().print(most_similar_words.toString());
        }
    }
    
    private void handleLoadModel(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String path = request.getParameter("path");
        if (path == null) { writeErrorParameterNull(response, "path"); return; }
        if (!loadWord2vec(path, true)) response.getWriter().print("fail");
        else response.getWriter().print("success");
    }

    private void handleLoadDefaultModel(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        if (!loadWord2vec(default_path, true)) response.getWriter().print("fail");
        else response.getWriter().print("success");
    }

    private void handleWhatInvalid(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.getWriter().print("Error: parameter \"what\" invalid.");
    }
    
    private void writeErrorWord2vecNotLoaded(HttpServletResponse response) throws IOException
    {
        response.getWriter().print("Error: word2vec is not loaded.");
    }
    
    private void writeErrorParameterNull(HttpServletResponse response, String parameterName) throws IOException
    {
        response.getWriter().print("Error: parameter \"" + parameterName + "\" missing.");
    }

    private Collection<String> getMostSimilarWords(String zeWord, int topN) 
    {
        if (!wordsIndexes.containsKey(zeWord)) return new ArrayList<>();
        return getMostSimilarWords(vectors[wordsIndexes.get(zeWord)], topN);
    }

    private Collection<String> getMostSynonymWords(String zeWord, int topN) 
    {
        if (!wordsIndexes.containsKey(zeWord)) return new ArrayList<>();
        return getMostSynonymWords(vectors[wordsIndexes.get(zeWord)], topN);
    }
    
    private Collection<String> getMostSimilarWords(double[] zeWord, int topN) 
    {
        Stuff[] zenearests = new Stuff[topN];
        for (int i = 0 ; i < topN ; i++) zenearests[i] = new Stuff(0.0, 0);
        int nbOfVectors = vectors.length;
        for (int j = 0 ; j < nbOfVectors ; j++) 
        {
            double[] v = vectors[j];
            double sim = VectorOperation.dot_product(zeWord, v);
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
    
    private static double[] term_to_term_product(double[] a, double[] b)
    {
        double[] ret = new double[a.length];
        for (int i = 0 ; i < ret.length ; i++)
        {
            int sign = a[i] * b[i] < 0 ? -1 : 1;
            ret[i] = sign * Math.sqrt(Math.abs(a[i] * b[i]));
        }
        return ret;
    }
    
    private static double[] weak_contextualization(double[] a, double[] b)
    {
        double[] ret = new double[a.length];
        for (int i = 0 ; i < ret.length ; i++)
        {
            int sign = a[i] * b[i] < 0 ? -1 : 1;
            ret[i] = a[i] + b[i] + sign * Math.sqrt(Math.abs(a[i] * b[i]));
        }
        return ret;
    }
    
    private static double absolute_synonymy(double[] a, double[] b)
    {
        double[] c = term_to_term_product(a, b);
        double[] ac = term_to_term_product(a, c);
        double[] aac = VectorOperation.normalize(VectorOperation.add(a, ac));
        double[] bc = term_to_term_product(b, c);
        double[] bbc = VectorOperation.normalize(VectorOperation.add(b, bc));
        return VectorOperation.dot_product(aac, bbc);
    }

    private Collection<String> getMostSynonymWords(double[] zeWord, int topN) 
    {
        Stuff[] zenearests = new Stuff[topN];
        for (int i = 0 ; i < topN ; i++) zenearests[i] = new Stuff(0.0, 0);
        int nbOfVectors = vectors.length;
        for (int j = 0 ; j < nbOfVectors ; j++) 
        {
            double[] v = vectors[j];
            double sim = absolute_synonymy(zeWord, v);
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
    
    public static String readString(DataInputStream dis) throws IOException
    {
        final int MAX_SIZE = 50;
        byte[] bytes = new byte[MAX_SIZE];
        byte b = dis.readByte();
        int i = -1;
        StringBuilder sb = new StringBuilder();
        while (b != ' ' && b != '\n') 
        {
            i++;
            bytes[i] = b;
            b = dis.readByte();
            if (i == 49) 
            {
                sb.append(new String(bytes));
                i = -1;
                bytes = new byte[MAX_SIZE];
            }
        }
        sb.append(new String(bytes, 0, i + 1));
        return sb.toString();
    }
    
    public static float readFloat(InputStream is) throws IOException
    {
        byte[] bytes = new byte[4];
        is.read(bytes);
        return getFloat(bytes);
    }
    
    public static float getFloat(byte[] b)
    {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }
    
    private static synchronized boolean loadWord2vec(String path, boolean reload)
    {
        if (loaded && !reload) return true;
        try
        {
            FileInputStream fis = new FileInputStream(path);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            int nbWords = Integer.parseInt(readString(dis));
            int vectorDimension = Integer.parseInt(readString(dis));
            words = new String[nbWords];
            wordsIndexes = new HashMap<>();
            vectors = new double[nbWords][vectorDimension];
            int last_percentage = 0;
            for (int i = 0 ; i < nbWords ; i++) {
                int current_percentage = ((int) ((((double) (i + 1)) / ((double) (nbWords))) * 100.0));
                if (current_percentage > last_percentage) System.out.println("Adding words... (" + current_percentage + "%)\r");
                last_percentage = current_percentage;
                words[i] = readString(dis);
                wordsIndexes.put(words[i], i);
                for (int j = 0 ; j < vectorDimension ; j++)
                {
                    vectors[i][j] = readFloat(dis);
                }
                vectors[i] = VectorOperation.normalize(vectors[i]);
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
