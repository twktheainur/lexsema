package org.getalp.lexsema.wsd.method;

public class StopCondition
{
    public enum Condition
    {
        ITERATIONS,
        MILLISECONDS,
        SCORERCALLS
    }
    
    public StopCondition(Condition condition, long value)
    {
        this.condition = condition;
        this.value = value;
        reset();
    }
    
    public void incrementIterations()
    {
        if (condition == Condition.ITERATIONS)
        {
            current++;
        }
    }

    public void incrementScorerCalls()
    {
        if (condition == Condition.SCORERCALLS)
        {
            current++;
        }
    }
    
    public void updateMilliseconds()
    {
        if (condition == Condition.MILLISECONDS)
        {
            current = System.currentTimeMillis();
        }
    }
    
    public void reset()
    {
        if (condition == Condition.MILLISECONDS) begin = System.currentTimeMillis();
        else begin = 0;
        current = begin;
        end = begin + value;
    }
    
    public boolean stop()
    {
        return current >= end;
    }
    
    public double getRemainingPercentage()
    {
        return ((double) (current - begin)) / ((double) (end - begin));
    }
    
    public Condition getCondition()
    {
        return condition;
    }
    
    public long getValue()
    {
        return value;
    }
    
    private Condition condition;
    
    private long value;
    
    private long begin;
    
    private long end;

    private long current;
}
