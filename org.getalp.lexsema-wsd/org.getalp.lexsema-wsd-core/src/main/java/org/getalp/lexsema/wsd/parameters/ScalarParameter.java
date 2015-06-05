package org.getalp.lexsema.wsd.parameters;

import java.util.Random;

public class ScalarParameter
{
    private static final Random random = new Random();
    
    private double minValue;
    
    private ScalarParameter minValueAsScalarParameter;
    
    private double maxValue;

    private ScalarParameter maxValueAsScalarParameter;
    
    public double step;
    
    public double currentValue;
    
    public ScalarParameter(double minValue, ScalarParameter minValueAsScalarParameter, 
                           double maxValue, ScalarParameter maxValueAsScalarParameter)
    {
        this.minValue = minValue;
        this.minValueAsScalarParameter = minValueAsScalarParameter;
        this.maxValue = maxValue;
        this.maxValueAsScalarParameter = maxValueAsScalarParameter;
        adjustStepMinMax();
        this.currentValue = randomDoubleBetween(minValue, maxValue);
        adjustCurrentValue();
    }
    
    public ScalarParameter(double minValue, double maxValue)
    {
        this(minValue, null, maxValue, null);
    }

    public ScalarParameter(ScalarParameter minValue, double maxValue)
    {
        this(minValue.currentValue, minValue, maxValue, null);
    }

    public ScalarParameter(double minValue, ScalarParameter maxValue)
    {
        this(minValue, null, maxValue.currentValue, maxValue);
    }

    public ScalarParameter(ScalarParameter minValue, ScalarParameter maxValue)
    {
        this(minValue.currentValue, minValue, maxValue.currentValue, maxValue);
    }
    
    public void setMinValueAsScalarParameter(ScalarParameter value)
    {
        this.minValueAsScalarParameter = value;
        adjustStepMinMax();
        adjustCurrentValue();
    }

    public void setMaxValueAsScalarParameter(ScalarParameter value)
    {
        this.maxValueAsScalarParameter = value;
        adjustStepMinMax();
        adjustCurrentValue();
    }
    
    public void add(double value)
    {
        adjustStepMinMax();
        currentValue += value * step;
        adjustCurrentValue();
    }
    
    public ScalarParameter clone()
    {
        ScalarParameter ret = new ScalarParameter(minValue, minValueAsScalarParameter, 
                                                  maxValue, maxValueAsScalarParameter);
        ret.currentValue = currentValue;
        return ret;
    }

    private void adjustStepMinMax()
    {
        if (minValueAsScalarParameter != null) minValue = minValueAsScalarParameter.currentValue;
        if (maxValueAsScalarParameter != null) maxValue = maxValueAsScalarParameter.currentValue;
        step = (maxValue - minValue) / 100;
    }
    
    private void adjustCurrentValue()
    {
        if (currentValue > maxValue) currentValue = maxValue;
        if (currentValue < minValue) currentValue = minValue;
    }

    private static double randomDoubleBetween(double min, double max)
    {
        return (random.nextDouble() * (max - min)) + min;
    }
}
