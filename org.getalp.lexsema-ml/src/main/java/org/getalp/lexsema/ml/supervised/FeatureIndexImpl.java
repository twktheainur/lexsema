package org.getalp.lexsema.ml.supervised;


import java.util.HashMap;
import java.util.Map;

public class FeatureIndexImpl implements FeatureIndex {
    private final Map<String, Integer> map;
    private int currentIndex;

    public FeatureIndexImpl() {
        map = new HashMap<>();
        currentIndex = 0;
    }

    @Override
    public int get(String feature) {
        if (!map.containsKey(feature)) {
            ++currentIndex;
            map.put(feature, currentIndex);
            //System.err.println("feature \"" + feature + "\" " + currentIndex);
        }
        return map.get(feature);
    }
}
