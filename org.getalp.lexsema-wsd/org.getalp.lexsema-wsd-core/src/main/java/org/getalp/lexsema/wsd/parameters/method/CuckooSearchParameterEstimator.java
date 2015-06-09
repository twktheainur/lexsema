package org.getalp.lexsema.wsd.parameters.method;

import org.apache.commons.math3.distribution.LevyDistribution;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.getalp.lexsema.wsd.method.StopCondition;

public class CuckooSearchParameterEstimator
{
    private class Nest
    {
        public Parameters solution;
        
        public double[] scores;
        
        public double meanScore;
        
        public Nest()
        {
            this.solution = solutionFactory.createRandomSolution();
            this.scores = solutionScorer.computeScore(solution);
            this.meanScore = computeMeanScore(scores);
            stopCondition.incrementScorerCalls();
        }
        
        public Nest(Parameters solution, double[] scores, double meanScore)
        {
            this.solution = solution;
            this.scores = scores;
            this.meanScore = meanScore;
        }
        
        public Nest clone()
        {
            double[] scoresCopy = new double[scores.length];
            for (int i = 0 ; i < scores.length ; i++) scoresCopy[i] = scores[i];
            return new Nest(solution.clone(), scoresCopy, meanScore);
        }
        
        public void randomFly()
        {
            double distance = levyDistribution.sample();
            solution.makeRandomChanges(distance);
            scores = solutionScorer.computeScore(solution);
            meanScore = computeMeanScore(scores);
            stopCondition.incrementScorerCalls();
        }
        
        private double computeMeanScore(double[] scores)
        {
            double sum = 0;
            double scoresLength = scores.length;
            for (double tmp : scores) sum += tmp;
            return sum / scoresLength;
        }
    }
    
    private static final MannWhitneyUTest mannTest = new MannWhitneyUTest();

    private StopCondition stopCondition;
        
    private LevyDistribution levyDistribution;
    
    private ParametersScorer solutionScorer;
    
    private ParametersFactory solutionFactory;

    private Nest nest;
    
    private boolean verbose;

    public CuckooSearchParameterEstimator(int iterations, double levyLocation, double levyScale, ParametersScorer solutionScorer, ParametersFactory solutionFactory, boolean verbose)
    {
        this(new StopCondition(StopCondition.Condition.ITERATIONS, iterations), levyLocation, levyScale, solutionScorer, solutionFactory, verbose);
    }

    public CuckooSearchParameterEstimator(StopCondition stopCondition, double levyLocation, double levyScale, ParametersScorer solutionScorer, ParametersFactory solutionFactory, boolean verbose)
    {
        this.stopCondition = stopCondition;
        this.levyDistribution = new LevyDistribution(levyLocation, levyScale);
        this.solutionScorer = solutionScorer;
        this.solutionFactory = solutionFactory;
        this.verbose = verbose;
    }

    public Parameters run()
    {
        stopCondition.reset();
        nest = new Nest();
        if (verbose)
        {
            System.out.println("Cuckoo Progress : " + 0 + "% - " +
                               "Current best : " + nest.meanScore + 
                               " [" + nest.solution + "]");
        }
        while (!stopCondition.stop())
        {
            int progress = (int)(stopCondition.getRemainingPercentage() * 100);
            double progressPercent = (double)progress / 100.0;
            Nest newNest = nest.clone();
            newNest.randomFly();
            if (newNest.meanScore > nest.meanScore && mannTest.mannWhitneyUTest(newNest.scores, nest.scores) < 0.01)
            {
                nest = newNest;
            }
            if (verbose)
            {
                System.out.println("Cuckoo Progress : " + progressPercent + "% - " +
                                   "Current best : " + nest.meanScore + 
                                   " [" + nest.solution + "]");
            }
            stopCondition.incrementIterations();
            stopCondition.updateMilliseconds();
        }
        return nest.solution;
    }
}
