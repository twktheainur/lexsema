package org.getalp.lexsema.io.resource;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import org.getalp.lexsema.io.LexicalEntry;
import org.getalp.lexsema.io.Sense;
import org.getalp.lexsema.io.cache.SenseCache;

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
    private boolean hasExtendedSignature;
    private boolean shuffle;


    public WordnetLoader(String path, boolean hasExtendedSignature, boolean shuffle) {
        this.path = path;
        this.hasExtendedSignature = hasExtendedSignature;
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

    private List<Sense> getSenses(String lemma, String pos) {
        List<Sense> senses = new ArrayList<>();
        IIndexWord iw = getWord(lemma + "%" + pos);
        if (iw != null) {
            for (int j = 0; j < iw.getWordIDs().size(); j++) {
                List<String> signature = new ArrayList<>();
                List<Double> weights = new ArrayList<>();
                IWord word = dictionary.getWord(iw.getWordIDs().get(j));
                String def = word.getSynset().getGloss();
                addToSignature(signature, weights, def);

                Sense s = new Sense(word.getSenseKey().toString(), signature, weights);

                Map<IPointer, List<IWordID>> rm = word.getRelatedMap();
                for (IPointer p : rm.keySet()) {
                    for (IWordID iwd : rm.get(p)) {
                        List<String> localSignature = new ArrayList<String>();
                        addToSignature(localSignature, weights, dictionary.getWord(iwd).getSynset().getGloss());
                        if (hasExtendedSignature) {
                            signature.addAll(localSignature);
                        }
                        s.getRelatedSignatures().put(p.getSymbol(), localSignature);
                    }
                }
                if (hasExtendedSignature) {
                    s.setSignature(signature);
                }
                senses.add(s);
            }
        }
        return senses;
    }

    @Override
    public List<Sense> getSenses(LexicalEntry w) {
        List<Sense> senses;
        senses = SenseCache.getInstance().getSenses(w);
        if (senses == null) {
            if (w != null) {
                if (w.getPos() == null || w.getPos().length() == 0) {
                    senses = getSenses(w.getLemma(), "n");
                    senses.addAll(getSenses(w.getLemma(), "r"));
                    senses.addAll(getSenses(w.getLemma(), "a"));
                    senses.addAll(getSenses(w.getLemma(), "v"));
                } else {
                    senses = getSenses(w.getLemma(), w.getPos());
                }
            }
            if (shuffle) {
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
        }
    }

    private int numberOfSenses(String word) {
        IIndexWord w = null;
        int senses = 0;
        w = dictionary.getIndexWord(word, POS.NOUN);
        if (w != null) {
            senses += w.getWordIDs().size();
        }
        w = dictionary.getIndexWord(word, POS.ADJECTIVE);
        if (w != null) {
            senses += w.getWordIDs().size();
        }
        w = dictionary.getIndexWord(word, POS.ADVERB);
        if (w != null) {
            senses += w.getWordIDs().size();
        }
        w = dictionary.getIndexWord(word, POS.VERB);
        if (w != null) {
            senses += w.getWordIDs().size();
        }
        return 0;
    }

    private IIndexWord getWord(String sid) {
        String lemme;
        String pos;
        String[] st = sid.split("%");
        if (sid.contains("%%n")) {
            lemme = "%";
            pos = "n";
        } else {
            lemme = st[0].toLowerCase();
            pos = st[1];
        }
        IIndexWord w = null;
        if (lemme != null && lemme.length() > 0) {
            if (pos.toLowerCase().startsWith("n")) {
                w = dictionary.getIndexWord(lemme, POS.NOUN);
            } else if (pos.toLowerCase().startsWith("v")) {
                w = dictionary.getIndexWord(lemme, POS.VERB);
            } else if (pos.toLowerCase().startsWith("a")) {
                w = dictionary.getIndexWord(lemme, POS.ADJECTIVE);
            } else if (pos.toLowerCase().startsWith("r")) {
                w = dictionary.getIndexWord(lemme, POS.ADVERB);
            }
        }
        return w;
    }

    @Override
    public List<List<Sense>> getAllSenses(List<LexicalEntry> wds) {
        List<List<Sense>> senses = new ArrayList<>();
        for (LexicalEntry w : wds) {
            senses.add(getSenses(w));
        }
        return senses;
    }
}
