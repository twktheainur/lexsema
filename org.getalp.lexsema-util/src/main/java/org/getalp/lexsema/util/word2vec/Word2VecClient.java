package org.getalp.lexsema.util.word2vec;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.io.IOUtils;

public class Word2VecClient
{
    private static final String serviceURL = "http://localhost:8080/org.getalp.lexsema-ws/w2vservice";

    public static double[] getWordVector(String word) {   
        try {
            String wordURL = URLEncoder.encode(word, "UTF-8");
            String query = "what=get_word_vector&word=" + wordURL;
            URL url = new URL(serviceURL + "?" + query);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream response = con.getInputStream();
            String responseStr = IOUtils.toString(response, "UTF-8");
            if (responseStr.trim().equals("[]")) return new double[0];
            String[] strValues = responseStr.replace("[", "").replace("]", "").split(", ");
            double[] vectord = new double[strValues.length];
            for (int i = 0 ; i < vectord.length ; i++) vectord[i] = Double.parseDouble(strValues[i]);
            return vectord;
        } catch (Exception e) {
            e.printStackTrace();
            return new double[0];
        }
    }

    public static Collection<String> getMostSimilarWords(String word, int topN) {   
        try {
            String wordURL = URLEncoder.encode(word, "UTF-8");
            String nURL = URLEncoder.encode(Integer.toString(topN), "UTF-8");
            String query = "what=get_most_similar_words&word=" + wordURL + "&n=" + nURL;
            URL url = new URL(serviceURL + "?" + query);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream response = con.getInputStream();
            String responseStr = IOUtils.toString(response, "UTF-8");
            return Arrays.asList(responseStr.replace("[", "").replace("]", "").split(", "));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static Collection<String> getMostSimilarWords(double[] vector, int topN) {
        try {
            String vectorURL = URLEncoder.encode(Arrays.toString(vector), "UTF-8");
            String nURL = URLEncoder.encode(Integer.toString(topN), "UTF-8");
            String query = "what=get_most_similar_words&vector=" + vectorURL + "&n=" + nURL;
            URL url = new URL(serviceURL + "?" + query);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream response = con.getInputStream();
            String responseStr = IOUtils.toString(response, "UTF-8");
            return Arrays.asList(responseStr.replace("[", "").replace("]", "").split(", "));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
