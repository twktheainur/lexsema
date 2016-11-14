package org.getalp.lexsema.io.thesaurus;

import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.util.StopList;

import java.util.*;
import java.util.regex.Pattern;

/**
 * This class provides the N most frequent words used in sentences
 * where a particular sense tag appears
 * based on the words in the same sentences.
 */
@SuppressWarnings("ClassWithoutLogger")
public class AnnotatedTextThesaurusImpl implements AnnotatedTextThesaurus {

    private static final Pattern NOT_ALPHABETIC_PATTERN = Pattern.compile("[^\\p{L} ]");
    private final Map<String, Map<String, Integer>> map;

    private final int n;

    /**
     * Creates a AnnotatedTextThesaurusImpl which will use the text
     * given in parameter.
     */
    public AnnotatedTextThesaurusImpl(Iterable<Text> texts, int n) {
        map = initMap(texts);
        this.n = n;
    }

    /**
     * Returns the "n" most frequently used words in the same sentence than
     * the giving word represented by its semantic tag
     */
    @Override
    public List<String> getRelatedWords(String semanticTag) {
        if (map.containsKey(semanticTag)) {
            Map<String, Integer> wordMap = sortByValue(map.get(semanticTag));
            List<String> ret = new ArrayList<>();
            int i = 0;
            for (String word2 : wordMap.keySet()) {
                if (i >= n) break;
                ret.add(word2);
                i++;
            }
            return ret;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Returns a map such that :
     * for each word in the given texts :
     * we keep all the words that are in the same sentence,
     * associated with their occurrence values
     */
    private static Map<String, Map<String, Integer>> initMap(Iterable<Text> texts) {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        for (Text txt : texts) {
            for (Sentence stc : txt.sentences()) {
                for (Word w : stc) {
                    if (w.getLemma() != null && w.getSenseAnnotation() != null) {
                        String wordStr = String.format("%s%%%s", w.getLemma(), w.getSenseAnnotation());
                        if (!map.containsKey(wordStr)) {
                            map.put(wordStr, new HashMap<String, Integer>());
                        }
                        Map<String, Integer> wordMap = map.get(wordStr);
                        for (Word w2 : stc) {
                            String word2Str = NOT_ALPHABETIC_PATTERN.matcher(w2.getSurfaceForm()).replaceAll("");
                            if (w != w2 && !StopList.isStopWord(word2Str)) {
                                if (!wordMap.containsKey(word2Str)) {
                                    wordMap.put(word2Str, 0);
                                }
                                wordMap.put(word2Str, wordMap.get(word2Str) + 1);
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
    private static Map<String, Integer> sortByValue(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}