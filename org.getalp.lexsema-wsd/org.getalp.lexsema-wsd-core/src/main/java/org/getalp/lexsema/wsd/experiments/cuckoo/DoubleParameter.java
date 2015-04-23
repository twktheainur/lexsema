package org.getalp.lexsema.wsd.experiments.cuckoo;

import java.util.Random;

public class DoubleParameter
{
    private static final Random random = new Random();
    
    public double minValue;
    
    public double maxValue;
    
    public double step;
    
    public double currentValue;
    
    public DoubleParameter(double minValue, double maxValue, double step)
    {
        this(minValue, maxValue, step, (random.nextDouble() * (maxValue - minValue)) + minValue);
    }
    
    public DoubleParameter(double minValue, double maxValue, double step, double currentValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
        this.currentValue = currentValue;
    }
    
    public void add(double value)
    {
        currentValue += value * step;
        if (currentValue > maxValue) currentValue = maxValue;
        if (currentValue < minValue) currentValue = minValue;
    }
    
    public DoubleParameter clone()
    {
        return new DoubleParameter(minValue, maxValue, step, currentValue);
    }
}
