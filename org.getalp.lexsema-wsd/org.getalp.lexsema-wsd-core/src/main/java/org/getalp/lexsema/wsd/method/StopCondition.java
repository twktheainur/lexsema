package org.getalp.lexsema.wsd.method;

public interface StopCondition
{
    public boolean stop();
    
    public void reset();
    
    public void increment();
    
    public double getRemainingPercentage();
}
