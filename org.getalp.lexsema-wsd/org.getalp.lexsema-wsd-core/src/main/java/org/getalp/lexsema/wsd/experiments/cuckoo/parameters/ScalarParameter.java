package org.getalp.lexsema.wsd.experiments.cuckoo.parameters;

import java.util.Random;

public class ScalarParameter
{
    private static final Random random = new Random();
    
    public double minValue;
    
    public double maxValue;
    
    public double step;
    
    public double currentValue;
    
    public ScalarParameter(double minValue, double maxValue, double step)
    {
        this(minValue, maxValue, step, (random.nextDouble() * (maxValue - minValue)) + minValue);
    }
    
    public ScalarParameter(double minValue, double maxValue, double step, double currentValue)
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
    
    public ScalarParameter clone()
    {
        return new ScalarParameter(minValue, maxValue, step, currentValue);
    }
}
