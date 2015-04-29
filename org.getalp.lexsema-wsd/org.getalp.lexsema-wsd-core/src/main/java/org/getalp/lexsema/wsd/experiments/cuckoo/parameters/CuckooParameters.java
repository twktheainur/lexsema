package org.getalp.lexsema.wsd.experiments.cuckoo.parameters;

import java.util.Random;

import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolution;

public class CuckooParameters implements CuckooSolution
{
    public static final Random random = new Random();
    
    public ScalarParameter levyScale;
    
    public ScalarParameter nestsNumber;
    
    public ScalarParameter destroyedNests;
    
    public CuckooParameters()
    {
        levyScale = new ScalarParameter(0, 5, 0.1);
        nestsNumber = new ScalarParameter(5, 100, 2);
        destroyedNests = new ScalarParameter(0, 0.8, 0.016);
    }

    public CuckooParameters(double levyScale, double nestsNumber, double destroyedNests)
    {
        this.levyScale = new ScalarParameter(0, 5, 0.1, levyScale);
        this.nestsNumber = new ScalarParameter(5, 100, 2, nestsNumber);
        this.destroyedNests = new ScalarParameter(0, 0.8, 0.016, destroyedNests);
    }
    
    public CuckooParameters clone()
    {
        return new CuckooParameters(levyScale.currentValue, nestsNumber.currentValue, destroyedNests.currentValue);
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
    }
    
    public String toString()
    {
        return "levy scale = " + levyScale.currentValue + 
                ", nests number = " + nestsNumber.currentValue + 
                ", destroyed nests = " + destroyedNests.currentValue * nestsNumber.currentValue;
    }
}
