package org.getalp.lexsema.util.dataitems;

/**
 * Created by tchechem on 20/07/15.
 */
public interface PositionedPairScore {
    int getIndexA();

    int getIndexB();

    int getSenseA();

    int getSenseB();

    double getScore();
}
