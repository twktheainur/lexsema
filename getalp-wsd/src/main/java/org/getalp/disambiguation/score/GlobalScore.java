package org.getalp.disambiguation.score;

import org.getalp.disambiguation.configuration.Configuration;

/**
 * Created by tchechem on 9/16/14.
 */
public interface GlobalScore {
    public double computeScore(Configuration c);
    public double computeScore(Configuration c, double[] window);
}
