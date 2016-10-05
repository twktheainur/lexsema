/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2015, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.getalp.lexsema.ml.matrix.factorization;


import org.getalp.lexsema.ml.matrix.factorization.seeding.RandomSeedingStrategy;
import org.getalp.lexsema.ml.matrix.factorization.seeding.RandomSeedingStrategyFactory;
import org.getalp.lexsema.ml.matrix.factorization.seeding.SeedingStrategy;
import org.getalp.lexsema.ml.matrix.factorization.seeding.SeedingStrategyFactory;

/**
 * A factory for {@link MatrixFactorization}s.
 */
public abstract class IterativeMatrixFactorizationFactory implements
        MatrixFactorizationFactory {
    /**
     * The default number of base vectors
     */
    protected final static int DEFAULT_K = 15;
    /**
     * The default number of maximum iterations
     */
    protected final static int DEFAULT_MAX_ITERATIONS = 15;
    /**
     * The default stop threshold
     */
    protected final static double DEFAULT_STOP_THRESHOLD = -1;
    /**
     * Default matrix seeding strategy factory
     */
    protected final static SeedingStrategyFactory DEFAULT_SEEDING_FACTORY = new RandomSeedingStrategyFactory(
            0);
    protected static final boolean DEFAULT_ORDERED = true;
    /**
     * The number of base vectors
     */
    protected int k;
    /**
     * The maximum number of iterations the algorithm is allowed to complete
     */
    protected int maxIterations;
    /**
     * The algorithm's stop threshold
     */
    protected double stopThreshold;
    /**
     * Matrix seeding strategy factory
     */
    protected SeedingStrategyFactory seedingFactory;
    /**
     * Order base vectors according to their 'activity'
     */
    protected boolean ordered;

    public IterativeMatrixFactorizationFactory() {
        k = DEFAULT_K;
        maxIterations = DEFAULT_MAX_ITERATIONS;
        stopThreshold = DEFAULT_STOP_THRESHOLD;
        seedingFactory = DEFAULT_SEEDING_FACTORY;
        ordered = DEFAULT_ORDERED;
    }

    /**
     * Returns the number of base vectors <i>k </i>.
     */
    public int getK() {
        return k;
    }

    /**
     * Sets the number of base vectors <i>k </i>.
     *
     * @param k the number of base vectors
     */
    public void setK(int k) {
        this.k = k;
    }

    /**
     * Returns {@link RandomSeedingStrategy} with constant seed.
     */
    protected SeedingStrategy createSeedingStrategy() {
        return seedingFactory.createSeedingStrategy();
    }

    /**
     * Returns the maximum number of iterations used by this factory.
     */
    public int getMaxIterations() {
        return maxIterations;
    }

    /**
     * Sets the maximum number of iterations to be used by this factory.
     */
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    /**
     * Returns the stop threshold used by this factory.
     */
    public double getStopThreshold() {
        return stopThreshold;
    }

    /**
     * Sets the stop threshold to be used by this factory.
     */
    public void setStopThreshold(double stopThreshold) {
        this.stopThreshold = stopThreshold;
    }

    /**
     * Returns the {@link SeedingStrategyFactory} used by this factory.
     */
    public SeedingStrategyFactory getSeedingFactory() {
        return seedingFactory;
    }

    /**
     * Sets the {@link SeedingStrategyFactory} to be used by this factory.
     *
     * @param seedingFactory
     */
    public void setSeedingFactory(SeedingStrategyFactory seedingFactory) {
        this.seedingFactory = seedingFactory;
    }

    /**
     * Returns <code>true</code> when the factorization is set to generate an ordered
     * basis.
     */
    public boolean isOrdered() {
        return ordered;
    }

    /**
     * Set to <code>true</code> to generate an ordered basis.
     *
     * @param ordered
     */
    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }
}
