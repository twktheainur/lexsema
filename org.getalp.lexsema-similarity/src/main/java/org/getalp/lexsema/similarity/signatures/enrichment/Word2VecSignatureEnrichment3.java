package org.getalp.lexsema.similarity.signatures.enrichment;

import org.apache.commons.io.IOUtils;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.Language;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Word2VecSignatureEnrichment3 implements SignatureEnrichment {

    private final int topN;
    
    private static final String serviceURL = "http://localhost:8080/org.getalp.lexsema-ws/w2vservice";
    
    public Word2VecSignatureEnrichment3(int topN) {
        this.topN = topN;
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
        List<double[]> symbolsVectors = new ArrayList<>();
        for (SemanticSymbol semanticSymbol : semanticSignature) {
            double[] symbolVector = getWordVector(semanticSymbol.getSymbol());
            if (symbolVector.length > 0) {
                symbolsVectors.add(symbolVector);
            }
        }
        double[] sum = sum(symbolsVectors.toArray(new double[symbolsVectors.size()][]));
        double[] sumNormalized = normalize(sum);
        Collection<String> nearests = getMostSimilarWord(sumNormalized, topN);
        SemanticSignature newSignature = new SemanticSignatureImpl();
        for (String word : semanticSignature.getStringSymbols()) {
            newSignature.addSymbol(word);
        }
        for (String word : nearests) {
            newSignature.addSymbol(word);
        }
        return newSignature;
    }

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
            throw new Error(e);
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
            throw new Error(e);
        }
    }
    
    public static Collection<String> getMostSimilarWord(double[] vector, int topN) {
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
            throw new Error(e);
        }
    }
    
    private static double norm(double[] v) {
        double ret = 0;
        for (int i = 0 ; i < v.length ; i++) {
            ret += v[i] * v[i];
        }
        return Math.sqrt(ret);
    }
    
    private static double[] normalize(double[] v) {
        double[] ret = new double[v.length];
        double norm = norm(v);
        for (int i = 0 ; i < v.length ; i++) {
            ret[i] = v[i] / norm;
        }
        return ret;
    }
    
    private static double dot_product(double[] a, double[] b) {
        double ret = 0;
        for (int i = 0 ; i < a.length ; i++) {
            ret += a[i] * b[i];
        }
        return ret;
    }
    
    private static double[] truc(double[] a, double[] b) {
        double[] res = new double[a.length];
        System.out.println(norm(a));
        System.out.println(norm(b));
        for (int i = 0 ; i < a.length ; i++) {
            //double tmp = a[i] * b[i];
            //int sign = tmp < 0 ? -1 : 1;
            //reso[i] = sign * Math.sqrt(Math.abs(tmp));
            res[i] = a[i] + b[i];
            //reso[i] += a[i] + b[i];
        }
        return normalize(res);
    }
    
    private static double[] sum(double[]... vectors) {
        double[] ret = new double[vectors[0].length];
        for (int i = 0 ; i < ret.length ; i++) {
            ret[i] = 0;
            for (int j = 0 ; j < vectors.length ; j++) {
                ret[i] += vectors[j][i];
            }
        }
        return ret;
    }
    
    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature, Language language) {
        return null;
    }

    @Override
    public void close() {

    }

}
