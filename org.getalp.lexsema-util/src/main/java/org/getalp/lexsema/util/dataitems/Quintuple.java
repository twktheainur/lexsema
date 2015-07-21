package org.getalp.lexsema.util.dataitems;

/**
 * Created by tchechem on 04/03/15.
 */
public interface Quintuple<U, V, W, X, Y> {
    @SuppressWarnings("PublicMethodNotExposedInInterface")
    U first();

    @SuppressWarnings("PublicMethodNotExposedInInterface")
    V second();

    W third();

    X fourth();

    Y fifth();
}
