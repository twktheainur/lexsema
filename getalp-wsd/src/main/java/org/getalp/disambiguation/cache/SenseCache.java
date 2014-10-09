package org.getalp.disambiguation.cache;

import org.getalp.disambiguation.Sense;
import org.getalp.disambiguation.Word;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tchechem on 9/16/14.
 */
public class SenseCache {

    Map<Word,List<Sense>> cache;

    private static SenseCache instance;

    public static SenseCache getInstance(){
        if(instance==null){
            instance = new SenseCache();
        }
        return instance;
    }

    private SenseCache(){
        cache = new HashMap<Word, List<Sense>>(1000000);
    }

    public List<Sense> getSenses(Word w){
        return cache.get(w);
    }

    public void addCache(Word w, List<Sense> ls){
        cache.put(w,ls);
    }
}
