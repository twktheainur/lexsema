package org.getalp.lexsema.wsd.experiments.cuckoo.parameters.annealing;

import java.util.Random;

import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.experiments.cuckoo.parameters.ScalarParameter;

public class AnnealingParameters implements CuckooSolution
{
    public static final Random random = new Random();
    
    public ScalarParameter coolingRate;
    
    public ScalarParameter convergenceThreshold;
    
    public ScalarParameter iterationsNumber;
    
    public AnnealingParameters()
    {
        coolingRate = new ScalarParameter(0.1, 0.95);
        convergenceThreshold = new ScalarParameter(1, 100);
        iterationsNumber = new ScalarParameter(0, 1);
    }

    public AnnealingParameters clone()
    {
        AnnealingParameters ret = new AnnealingParameters();
        ret.coolingRate.currentValue = coolingRate.currentValue;
        ret.convergenceThreshold.currentValue =  convergenceThreshold.currentValue;
        ret.iterationsNumber.currentValue = iterationsNumber.currentValue;
        return ret;
    }
    
    public void makeRandomChanges(double distance)
    {
        double x = random.nextGaussian();
        double y = random.nextGaussian();
        double z = random.nextGaussian();
        double norm = Math.sqrt(x*x + y*y + z*z);
        x /= norm;
        y /= norm;
        z /= norm;
        coolingRate.add(x * distance);
        convergenceThreshold.add(y * distance);
        iterationsNumber.add(z * distance);
        System.out.println("Move coolingRate : " + x * distance * coolingRate.step);
        System.out.println("Move convergenceThreshold : " + y * distance * convergenceThreshold.step);
        System.out.println("Move iterationsNumber : " + z * distance * iterationsNumber.step);
    }
    
    public String toString()
    {
        return "coolingRate = " + coolingRate.currentValue + 
                ", convergenceThreshold = " + (int) convergenceThreshold.currentValue + 
                ", iterationsNumber = " + (int) iterationsNumber.currentValue;
    }
}
