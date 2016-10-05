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


import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;
import com.carrotsearch.hppc.sorting.IndirectComparator;
import org.getalp.lexsema.ml.matrix.Matrices;
import org.getalp.lexsema.ml.matrix.factorization.seeding.RandomSeedingStrategy;
import org.getalp.lexsema.ml.matrix.factorization.seeding.SeedingStrategy;

/**
 * Base functionality for {@link IterativeMatrixFactorization}s.
 */
@SuppressWarnings("deprecation")
abstract class IterativeMatrixFactorizationBase extends MatrixFactorizationBase implements
        IterativeMatrixFactorization {
    protected static final int DEFAULT_MAX_ITERATIONS = 15;
    protected static final SeedingStrategy DEFAULT_SEEDING_STRATEGY = new RandomSeedingStrategy(
            0);
    protected static final boolean DEFAULT_ORDERED = false;
    protected static int DEFAULT_K = 15;
    protected static double DEFAULT_STOP_THRESHOLD = -1.0;
    protected int iterationsCompleted;
    /**
     * The desired number of base vectors
     */
    protected int k;
    /**
     * The maximum number of iterations the algorithm is allowed to run
     */
    protected int maxIterations;
    /**
     * If the percentage decrease in approximation error becomes smaller than
     * <code>stopThreshold</code>, the algorithm will stop. Note: calculation of
     * approximation error is quite costly. Setting the threshold to -1 turns off
     * approximation error calculation and hence makes the algorithm do the maximum number
     * of iterations.
     */
    protected double stopThreshold;
    /**
     * Seeding strategy
     */
    protected SeedingStrategy seedingStrategy;
    /**
     * Order base vectors according to their 'activity'?
     */
    protected boolean ordered;
    /**
     * Current approximation error
     */
    protected double approximationError;

    /**
     * Approximation errors during subsequent iterations
     */
    protected double[] approximationErrors;

    /**
     * Iteration counter
     */
    protected int iterationsleted;

    /**
     * Sorting aggregates
     */
    protected double[] aggregates;

    /**
     * @param A
     */
    public IterativeMatrixFactorizationBase(DoubleMatrix2D A) {
        super(A);

        k = DEFAULT_K;
        maxIterations = DEFAULT_MAX_ITERATIONS;
        stopThreshold = DEFAULT_STOP_THRESHOLD;
        seedingStrategy = DEFAULT_SEEDING_STRATEGY;
        ordered = DEFAULT_ORDERED;
        approximationErrors = null;
        approximationError = -1;
        iterationsCompleted = 0;
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
     * @return true if the decrease in the approximation error is smaller than the
     * <code>stopThreshold</code>
     */
    protected boolean updateApproximationError() {
        if (approximationErrors == null) {
            approximationErrors = new double[maxIterations + 1];
        }

        // Approximation error
        double newApproximationError = Matrices.frobeniusNorm(U.zMult(V, null, 1, 0,
                false, true).assign(A, DoubleFunctions.minus));

        approximationErrors[iterationsCompleted] = newApproximationError;

        if ((approximationError - newApproximationError) / approximationError < stopThreshold) {
            approximationError = newApproximationError;
            return true;
        } else {
            approximationError = newApproximationError;
            return false;
        }
    }

    /**
     * Orders U and V matrices according to the 'activity' of base vectors.
     */
    protected void order() {
        DoubleMatrix2D VT = V.viewDice();
        aggregates = new double[VT.rows()];

        for (int i = 0; i < aggregates.length; i++) {
            aggregates[i] = VT.viewRow(i).aggregate(DoubleFunctions.plus, DoubleFunctions.square);
        }

        final IndirectComparator.DescendingDoubleComparator comparator = new IndirectComparator.DescendingDoubleComparator(
                aggregates);
        V = Matrices.sortedRowsView(VT, comparator).viewDice();
        U = Matrices.sortedRowsView(U.viewDice(), comparator).viewDice();
    }

    /**
     * Returns current {@link SeedingStrategy}.
     */
    public SeedingStrategy getSeedingStrategy() {
        return seedingStrategy;
    }

    /**
     * Sets new {@link SeedingStrategy}.
     */
    public void setSeedingStrategy(SeedingStrategy seedingStrategy) {
        this.seedingStrategy = seedingStrategy;
    }

    /**
     * Returns the maximum number of iterations the algorithm is allowed to run.
     */
    public int getMaxIterations() {
        return maxIterations;
    }

    /**
     * Sets the maximum number of iterations the algorithm is allowed to run.
     */
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    /**
     * Returns the algorithms <code>stopThreshold</code>. If the percentage decrease in
     * approximation error becomes smaller than <code>stopThreshold</code>, the algorithm
     * will stop.
     */
    public double getStopThreshold() {
        return stopThreshold;
    }

    /**
     * Sets the algorithms <code>stopThreshold</code>. If the percentage decrease in
     * approximation error becomes smaller than <code>stopThreshold</code>, the algorithm
     * will stop.
     * <p/>
     * Note: calculation of approximation error is quite costly. Setting the threshold to
     * -1 turns off calculation of the approximation error and hence makes the algorithm
     * do the maximum allowed number of iterations.
     */
    public void setStopThreshold(double stopThreshold) {
        this.stopThreshold = stopThreshold;
    }

    /**
     * Returns final approximation error or -1 if the approximation error calculation has
     * been turned off (see {@link #setMaxIterations(int)}.
     *
     * @return final approximation error or -1
     */
    public double getApproximationError() {
        return approximationError;
    }

    /**
     * Returns an array of approximation errors during after subsequent iterations of the
     * algorithm. Element 0 of the array contains the approximation error before the first
     * iteration. The array is <code>null</code> if the approximation error calculation
     * has been turned off (see {@link #setMaxIterations(int)}.
     */
    public double[] getApproximationErrors() {
        return approximationErrors;
    }

    public int getIterationsCompleted() {
        return iterationsCompleted;
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
     */
    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    /**
     * Returns column aggregates for a sorted factorization, and <code>null</code> for an
     * unsorted factorization.
     */
    public double[] getAggregates() {
        return aggregates;
    }
}
