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
import org.nd4j.linalg.ops.transforms.Transforms;

public class Word2VecWebService extends WebServiceServlet
{
    private static WordVectors word2vec = null; 
    
    private static String default_path = "/home/viall/current/data/word2vec/model_large.bin";
    
    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String what = request.getParameter("what");
        if (what.equals("get_word_vector"))
        {
            String word = request.getParameter("word");
            response.getWriter().write("getting vector of word " + word + "...");
            double[] vector = word2vec.getWordVector(word);
            response.getWriter().write(vector.toString());
        }
        else if (what.equals("get_most_similar_words"))
        {
            String word = request.getParameter("word");
            int n = Integer.parseInt(request.getParameter("n"));
            response.getWriter().write("getting " + n + " most similar words to " + word + "...");
            Collection<String> most_similar_words = getMostSimilarWord(word, n);
            response.getWriter().write(most_similar_words.toString());
        }
        else if (what.equals("load_model"))
        {
            String path = request.getParameter("path");
            word2vec = loadWord2vec(path);
            if (word2vec == null)
            {
                response.getWriter().write("fail");
            }
            else
            {
                response.getWriter().write("success");
            }
        }
    }

    private Collection<String> getMostSimilarWord(String zeWordAsString, int topN) 
    {
        return getMostSimilarWord(word2vec.getWordVectorMatrix(zeWordAsString), topN);
    }
    
    private Collection<String> getMostSimilarWord(INDArray zeWord, int topN) 
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
