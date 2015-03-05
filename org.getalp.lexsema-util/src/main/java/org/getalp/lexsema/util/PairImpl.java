package org.getalp.lexsema.util;


public class PairImpl<U, V> implements Pair<U, V> {
    U first;
    V second;

    public PairImpl(U first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    @SuppressWarnings("PublicMethodNotExposedInInterface")
    public U first() {
        return first;
    }

    @Override
    @SuppressWarnings("PublicMethodNotExposedInInterface")
    public V second() {
        return second;
    }
}
