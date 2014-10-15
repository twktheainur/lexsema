package org.getalp.disambiguation.method.weka;

/**
 * Created by tchechem on 10/15/14.
 */
import java.util.HashMap;
import java.util.Map;
public class FeatureIndex {
    private Map<String,Integer> map;
    private int currentIndex;

    public FeatureIndex() {
        map = new HashMap<String, Integer>();
        currentIndex = 0;
    }
    public int get(String feature){
        if(!map.containsKey(feature)){
            map.put(feature,++currentIndex);
        }
        return map.get(feature);
    }
}
