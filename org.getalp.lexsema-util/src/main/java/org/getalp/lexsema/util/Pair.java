package org.getalp.lexsema.util;

/**
 * Created by tchechem on 04/03/15.
 */
public interface Pair<U, V> {
    @SuppressWarnings("PublicMethodNotExposedInInterface")
    U first();

    @SuppressWarnings("PublicMethodNotExposedInInterface")
    V second();
}
