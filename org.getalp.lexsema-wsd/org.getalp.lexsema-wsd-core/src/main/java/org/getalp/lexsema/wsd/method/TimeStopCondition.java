package org.getalp.lexsema.wsd.method;

public class TimeStopCondition implements StopCondition
{
    private long milliSeconds;
    
    private long endingTime;
    
    public TimeStopCondition(long milliSeconds)
    {
        this.milliSeconds = milliSeconds;
        reset();
    }
    
    public boolean stop()
    {
        return System.currentTimeMillis() >= endingTime;
    }
    
    public void reset()
    {
        endingTime = System.currentTimeMillis() + milliSeconds;
    }

    public void increment()
    {
        
    }

    public double getRemainingPercentage()
    {
        return ((double) System.currentTimeMillis()) * 100.0 / ((double)(endingTime));
    }
    
    public long getMilliSeconds()
    {
        return milliSeconds;
    }
}
