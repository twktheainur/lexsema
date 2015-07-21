package org.getalp.lexsema.util.dataitems;


public class PositionedPairScoreImpl implements PositionedPairScore {
    private final int indexA;
    private final int indexB;
    private final int senseA;
    private final int senseB;
    private final double score;

    public PositionedPairScoreImpl(int indexA, int indexB, int senseA, int senseB, double score) {
        this.indexA = indexA;
        this.indexB = indexB;
        this.senseA = senseA;
        this.senseB = senseB;
        this.score = score;
    }

    @Override
    public int getIndexA() {
        return indexA;
    }

    @Override
    public int getIndexB() {
        return indexB;
    }

    @Override
    public int getSenseA() {
        return senseA;
    }

    @Override
    public int getSenseB() {
        return senseB;
    }

    @Override
    public double getScore() {
        return score;
    }
}
