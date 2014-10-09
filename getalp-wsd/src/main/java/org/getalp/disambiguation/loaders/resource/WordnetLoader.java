package org.getalp.disambiguation.loaders.resource;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import org.getalp.disambiguation.Sense;
import org.getalp.disambiguation.Word;
import org.getalp.disambiguation.cache.SenseCache;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class WordnetLoader implements LRLoader {
    private final Dictionary dictionary;
    String path;
    private boolean isExtended;
    private boolean shuffle;


    public WordnetLoader(String path, boolean isExtended, boolean shuffle) {
        this.path = path;
        this.isExtended = isExtended;
        this.shuffle = shuffle;

        URL url = null;
        try {
            url = new URL("file", null, path);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dictionary = new Dictionary(url);
        try {
            dictionary.open();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public List<Sense> getSenses(Word w) {
        boolean miss = false;
        List<Sense> senses;
        senses = SenseCache.getInstance().getSenses(w);
        if (senses == null) {
            senses = new ArrayList<Sense>();
            IIndexWord iw = getWord(w.getLemma() + "%" + w.getPos());
            if (w != null) {
                for (int j = 0; j < iw.getWordIDs().size(); j++) {

                    List<String> signature = new ArrayList<String>();
                    List<Double> weights = new ArrayList<Double>();
                    IWord word = dictionary.getWord(iw.getWordIDs().get(j));
                    String def = word.getSynset().getGloss();
                    addToSignature(signature, weights, def);

                    if (isExtended) {
                        Map<IPointer, List<IWordID>> rm = word.getRelatedMap();
                        for (IPointer p : rm.keySet()) {
                            for (IWordID iwd : rm.get(p)) {
                                addToSignature(signature, weights, dictionary.getWord(iwd).getSynset().getGloss());
                            }
                        }
                    }
                    senses.add(new Sense(word.getSenseKey().toString(), signature, weights));
                }
            }
            if(shuffle){
                Collections.shuffle(senses);
            }
            SenseCache.getInstance().addCache(w, senses);
        }

        return senses;
    }

    private void addToSignature(List<String> signature, List<Double> weights, String def) {
        StringTokenizer st = new StringTokenizer(def, " ", false);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            signature.add(token);
            weights.add(1.0);
            //weights.add(1d/((double)numberOfSenses(token)+0.00001d));
            //weights.add((double)numberOfSenses(token));
        }
    }

    private int numberOfSenses(String word){
        IIndexWord w = null;
        int senses = 0;
        w = dictionary.getIndexWord(word, POS.NOUN);
        if(w!=null){
            senses+=w.getWordIDs().size();
        }
        w = dictionary.getIndexWord(word, POS.ADJECTIVE);
        if(w!=null){
            senses+=w.getWordIDs().size();
        }
        w = dictionary.getIndexWord(word, POS.ADVERB);
        if(w!=null){
            senses+=w.getWordIDs().size();
        }
        w = dictionary.getIndexWord(word, POS.VERB);
        if(w!=null){
            senses+=w.getWordIDs().size();
        }
        return 0;
    }

    private IIndexWord getWord(String sid) {
        String lemme = "";
        String pos = "";
        StringTokenizer st = new StringTokenizer(sid, "%");
        if (sid.contains("%%n")) {
            lemme = "%";
            pos = "n";
        } else {
            lemme = st.nextToken();
            pos = st.nextToken();
        }
        IIndexWord w = null;
        if (pos.equals("n")) {
            w = dictionary.getIndexWord(lemme, POS.NOUN);
        } else if (pos.equals("v")) {
            w = dictionary.getIndexWord(lemme, POS.VERB);
        } else if (pos.equals("a")) {
            w = dictionary.getIndexWord(lemme, POS.ADJECTIVE);
        }
        if (pos.equals("r")) {
            w = dictionary.getIndexWord(lemme, POS.ADVERB);
        }
        return w;
    }

    @Override
    public List<List<Sense>> getAllSenses(List<Word> wds) {
        List<List<Sense>> senses = new ArrayList<List<Sense>>();
        for (Word w : wds) {
            senses.add(getSenses(w));
        }
        return senses;
    }
}
