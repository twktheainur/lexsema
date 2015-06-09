package org.getalp.lexsema.wsd.parameters.annealing;

import java.util.Random;

import org.getalp.lexsema.wsd.parameters.ScalarParameter;
import org.getalp.lexsema.wsd.parameters.method.Parameters;

public class AnnealingParameters implements Parameters
{
    public static final Random random = new Random();
    
    public ScalarParameter coolingRate;
    
    public ScalarParameter iterationsNumber;
    
    public AnnealingParameters()
    {
        coolingRate = new ScalarParameter(0.1, 0.95);
        iterationsNumber = new ScalarParameter(0, 100);
    }

    public AnnealingParameters clone()
    {
        AnnealingParameters ret = new AnnealingParameters();
        ret.coolingRate.currentValue = coolingRate.currentValue;
        ret.iterationsNumber.currentValue = iterationsNumber.currentValue;
        return ret;
    }
    
    public void makeRandomChanges(double distance)
    {
        double x = random.nextGaussian();
        double y = random.nextGaussian();
        double max = Math.max(Math.abs(x), Math.abs(y));
        x /= max;
        y /= max;
        coolingRate.add(x * distance);
        iterationsNumber.add(y * distance);
        /*
        System.out.println("Move coolingRate : " + x * distance * coolingRate.step);
        System.out.println("Move iterationsNumber : " + y * distance * iterationsNumber.step);
        */
    }
    
    public String toString()
    {
        return "coolingRate = " + coolingRate.currentValue + 
                ", iterationsNumber = " + (int) iterationsNumber.currentValue;
    }
}
