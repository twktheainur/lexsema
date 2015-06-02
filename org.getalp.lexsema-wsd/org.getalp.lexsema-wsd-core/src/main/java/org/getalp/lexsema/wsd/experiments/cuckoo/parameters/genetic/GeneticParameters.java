package org.getalp.lexsema.wsd.experiments.cuckoo.parameters.genetic;

import java.util.Random;

import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.experiments.cuckoo.parameters.ScalarParameter;

public class GeneticParameters implements CuckooSolution
{
    public static final Random random = new Random();
    
    public ScalarParameter levyScale;
    
    public ScalarParameter nestsNumber;
    
    public ScalarParameter destroyedNests;
    
    public GeneticParameters()
    {
        levyScale = new ScalarParameter(0, 5);
        nestsNumber = new ScalarParameter(1, 100);
        destroyedNests = new ScalarParameter(0, 1);
    }

    public GeneticParameters clone()
    {
        GeneticParameters ret = new GeneticParameters();
        ret.levyScale.currentValue = levyScale.currentValue;
        ret.nestsNumber.currentValue =  nestsNumber.currentValue;
        ret.destroyedNests.currentValue = destroyedNests.currentValue;
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
        levyScale.add(x * distance);
        nestsNumber.add(y * distance);
        destroyedNests.add(z * distance);
        System.out.println("Move levyScale : " + x * distance * levyScale.step);
        System.out.println("Move nestsNumber : " + y * distance * nestsNumber.step);
        System.out.println("Move destroyedNests : " + z * distance * destroyedNests.step);
    }
    
    public String toString()
    {
        return "levy scale = " + levyScale.currentValue + 
                ", nests number = " + (int) nestsNumber.currentValue + 
                ", destroyed nests = " + (int) (destroyedNests.currentValue * nestsNumber.currentValue);
    }
}
