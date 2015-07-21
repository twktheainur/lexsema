package org.getalp.lexsema.util.dataitems;


public class QuintupleImpl<U, V, W,X,Y> implements Quintuple<U, V, W,X,Y> {
    U first;
    V second;
    W third;
    X fourth;
    Y fifth;

    public QuintupleImpl(U first, V second, W third, X fourth, Y fifth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
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

    @Override
    public X fourth() {
        return fourth;
    }

    @Override
    public Y fifth() {
        return fifth;
    }


}
