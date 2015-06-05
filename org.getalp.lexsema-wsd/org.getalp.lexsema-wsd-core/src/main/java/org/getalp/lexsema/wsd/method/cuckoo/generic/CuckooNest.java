package org.getalp.lexsema.wsd.method.cuckoo.generic;

import org.apache.commons.math3.distribution.LevyDistribution;

public class CuckooNest implements Comparable<CuckooNest>
{
    private CuckooSolutionScorer solutionScorer;

    private LevyDistribution levyDistribution;
    
    public CuckooSolution solution;
    
    public double score;
    
    public CuckooNest(CuckooSolutionScorer solutionScorer, LevyDistribution levyDistribution, CuckooSolutionFactory solutionFactory)
    {
        this.solutionScorer = solutionScorer;
        this.levyDistribution = levyDistribution;
        this.solution = solutionFactory.createRandomSolution();
        this.score = solutionScorer.computeScore(solution);
    }
    
    public CuckooNest(CuckooSolutionScorer solutionScorer, LevyDistribution levyDistribution, CuckooSolution solution, double score)
    {
        this.solutionScorer = solutionScorer;
        this.levyDistribution = levyDistribution;
        this.solution = solution;
        this.score = score;
    }
    
    public int compareTo(CuckooNest other)
    {
        return Double.compare(score, other.score);
    }
    
    public CuckooNest clone()
    {
        return new CuckooNest(solutionScorer, levyDistribution, solution.clone(), score);
    }
    
    public void randomFly()
    {
        double distance = levyDistribution.sample();
        solution.makeRandomChanges(distance);
        score = solutionScorer.computeScore(solution);
    }
}
