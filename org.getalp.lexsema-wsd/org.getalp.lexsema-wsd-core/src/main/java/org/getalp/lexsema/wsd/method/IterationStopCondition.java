package org.getalp.lexsema.wsd.method;

public class IterationStopCondition implements StopCondition
{
    private int currentIteration;
    
    private int iterationsNumber;
    
    public IterationStopCondition(int iterationsNumber)
    {
        this.iterationsNumber = iterationsNumber;
        reset();
    }
    
    public boolean stop()
    {
        return currentIteration >= iterationsNumber;
    }

    public void reset()
    {
        currentIteration = 0;
    }
    
    public void increment()
    {
        currentIteration++;
    }
    
    public double getRemainingPercentage()
    {
        return ((double) currentIteration) * 100.0 / ((double)(iterationsNumber));
    }
}
