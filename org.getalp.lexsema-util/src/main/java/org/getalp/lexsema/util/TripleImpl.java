package org.getalp.lexsema.util;


public class TripleImpl<U, V, W> implements Triple<U, V, W> {
    U first;
    V second;
    W third;
    public TripleImpl(U first, V second, W third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public U first() {
        return first;
    }

    @Override
    public V second() {
        return second;
    }

    @Override
    public W third(){
        return third;
    }

}
