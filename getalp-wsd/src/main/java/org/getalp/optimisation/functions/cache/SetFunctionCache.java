package org.getalp.optimisation.functions.cache;

import java.util.HashMap;
import java.util.Map;

public class SetFunctionCache {

    private Map<String,Double> cache;

    public String keyCode(int index1, int index2) {
        return String.valueOf(index1)+"|"+String.valueOf(index2);

    }

    public SetFunctionCache() {
        cache = new HashMap<String, Double>(10000);
    }

    public boolean contains(int start, int end){
        return cache.containsKey(keyCode(start,end));
    }

    public Double get(int start,int end){
        return cache.get(keyCode(start,end));
    }

    public void put(int start, int end,double value){
        cache.put(keyCode(start,end),value);
    }

}
