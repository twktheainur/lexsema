package org.getalp.lexsema.util.word2vec;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class Word2VecClient
{
    private static final String serviceURL = "http://localhost:8080/org.getalp.lexsema-ws/w2vservice";

    private static String send(String what, Map<String, String> args) {
        try {
            String query = "what=" + what;
            for (String arg : args.keySet()) query += "&" + URLEncoder.encode(arg, "UTF-8") + "=" + URLEncoder.encode(args.get(arg), "UTF-8");
            URL url = new URL(serviceURL);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            writer.write(query); writer.flush();
            InputStream response = con.getInputStream();
            return IOUtils.toString(response, "UTF-8");
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    public static double[] getWordVector(String word) {   
        Map<String, String> args = new HashMap<>(); 
        args.put("word", word);
        String responseStr = send("get_word_vector", args);
        if (responseStr.trim().equals("[]")) return new double[0];
        String[] strValues = responseStr.replace("[", "").replace("]", "").split(", ");
        double[] vectord = new double[strValues.length];
        for (int i = 0 ; i < vectord.length ; i++) vectord[i] = Double.parseDouble(strValues[i]);
        return vectord;
    }

    public static Collection<String> getMostSimilarWords(String word, int topN) {
        return getMostSimilarWords(word, topN, null);
    }

    public static Collection<String> getMostSimilarWords(double[] vector, int topN) {
        return getMostSimilarWords(vector, topN, null);
    }

    public static Collection<String> getMostSimilarWords(String word, int topN, double[] context) {   
        Map<String, String> args = new HashMap<>(); 
        args.put("word", word); 
        args.put("n", Integer.toString(topN));
        if (context != null) args.put("context_vector", Arrays.toString(context));
        String responseStr = send("get_most_similar_words", args);
        return Arrays.asList(responseStr.replace("[", "").replace("]", "").split(", "));
    }

    public static Collection<String> getMostSimilarWords(double[] vector, int topN, double[] context) {
        Map<String, String> args = new HashMap<>(); 
        args.put("vector", Arrays.toString(vector)); 
        args.put("n", Integer.toString(topN));
        if (context != null) args.put("context_vector", Arrays.toString(context));
        String responseStr = send("get_most_similar_words", args);
        return Arrays.asList(responseStr.replace("[", "").replace("]", "").split(", "));
    }

    public static Collection<String> getMostSynonymWords(String word, int topN) {   
        Map<String, String> args = new HashMap<>(); 
        args.put("word", word); 
        args.put("n", Integer.toString(topN));
        String responseStr = send("get_most_synonym_words", args);
        return Arrays.asList(responseStr.replace("[", "").replace("]", "").split(", "));
    }

    public static Collection<String> getMostSynonymWords(double[] vector, int topN) {
        Map<String, String> args = new HashMap<>(); 
        args.put("vector", Arrays.toString(vector)); 
        args.put("n", Integer.toString(topN));
        String responseStr = send("get_most_synonym_words", args);
        return Arrays.asList(responseStr.replace("[", "").replace("]", "").split(", "));
    }
}
