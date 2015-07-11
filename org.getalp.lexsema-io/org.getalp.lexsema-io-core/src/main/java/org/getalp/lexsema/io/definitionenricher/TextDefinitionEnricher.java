package org.getalp.lexsema.io.definitionenricher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.util.StopList;

/**
 * This class provides a tool to extend the definition of a word
 * based on the words in the same sentences.
 */
public class TextDefinitionEnricher
{
    private Map<String, Map<String, Integer>> map;
    
    /**
     * Creates a TextDefinitionEnricher which will use the text
     * given in parameter.
     */
    public TextDefinitionEnricher(TextLoader texts)
    {
        texts.load();
        map = initMap(texts);
    }
    
    /**
     * Returns the "n" most frequently used words in the same sentence than 
     * the giving word represented by its semantig tag
     */
    public String[] getRelatedWords(String semanticTag, int n)
    {
        if (!map.containsKey(semanticTag)) return new String[0];
        Map<String, Integer> wordMap = sortByValue(map.get(semanticTag));
        ArrayList<String> ret = new ArrayList<String>();
        int i = 0;
        for (String word2 : wordMap.keySet())
        {
            if (i >= n) break;
            ret.add(word2);
            i++;
        }
        return ret.toArray(new String[ret.size()]);
    }
    
    /**
     * Returns a map such that :
     * for each word in the given texts :
     *   we keep all the words that are in the same sentence, 
     *   associated with their occurrence values
     */
    private static Map<String, Map<String, Integer>> initMap(TextLoader texts)
    {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        for (Text txt : texts)
        {
            for (Sentence stc : txt.sentences())
            {
                for (Word w : stc)
                {
                    if (w.getLemma() != null && w.getSemanticTag() != null)
                    {
                        String wordStr = w.getLemma() + "%" + w.getSemanticTag();
                        if (!map.containsKey(wordStr))
                        {
                            map.put(wordStr, new HashMap<String, Integer>());
                        }
                        Map<String, Integer> wordMap = map.get(wordStr);
                        for (Word w2 : stc)
                        {
                            String word2Str = w2.getSurfaceForm();
                            if (w != w2 && !StopList.isStopWord(word2Str))
                            {
                                if (!wordMap.containsKey(word2Str))
                                {
                                    wordMap.put(word2Str, 0);
                                }
                                wordMap.put(word2Str, wordMap.get(word2Str).intValue() + 1);
                            }
                        }
                    }
                }
            }
        }
        return map;
    }
    
    /**
     * Sorts a map following a descendant order (from the biggest to the smallest value)
     * regarding its values (not its keys)
     */
    private static Map<String, Integer> sortByValue(Map<String, Integer> map)
    {
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}