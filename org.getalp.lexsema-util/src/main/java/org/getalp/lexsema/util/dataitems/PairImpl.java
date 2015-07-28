package org.getalp.lexsema.util.dataitems;


public class PairImpl<U, V> implements Pair<U, V> {
    U first;
    V second;

    public PairImpl(U first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PairImpl<?, ?> pair = (PairImpl<?, ?>) o;

        if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
        return !(second != null ? !second.equals(pair.second) : pair.second != null);

    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
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
