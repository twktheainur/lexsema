package org.getalp.lexsema.wsd.experiments.cuckoo;

import java.util.Random;

public class IntegerParameter
{
    private static final Random random = new Random();
    
    public int minValue;
    
    public int maxValue;
    
    public int step;

    public int currentValue;
    
    public IntegerParameter(int minValue, int maxValue, int step)
    {
        this(minValue, maxValue, step, random.nextInt(maxValue - minValue) + minValue);
    }

    public IntegerParameter(int minValue, int maxValue, int step, int currentValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
        this.currentValue = currentValue;
    }
    
    public void add(int value)
    {
        currentValue += value * step;
        if (currentValue > maxValue) currentValue = maxValue;
        if (currentValue < minValue) currentValue = minValue;
    }
    
    public IntegerParameter clone()
    {
        return new IntegerParameter(minValue, maxValue, step, currentValue);
    }
}
