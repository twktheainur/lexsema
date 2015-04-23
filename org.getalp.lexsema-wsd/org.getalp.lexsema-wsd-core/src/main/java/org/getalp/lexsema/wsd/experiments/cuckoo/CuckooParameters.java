package org.getalp.lexsema.wsd.experiments.cuckoo;

import java.util.Random;

public class CuckooParameters
{
    public static final Random random = new Random();
    
    public DoubleParameter levyScale;
    
    public IntegerParameter nestsNumber;
    
    public DoubleParameter destroyedNests;
    
    public CuckooParameters()
    {
        levyScale = new DoubleParameter(0, 5, 0.1);
        nestsNumber = new IntegerParameter(5, 100, 5);
        destroyedNests = new DoubleParameter(0, 0.8, 0.1);
    }

    public CuckooParameters(double levyScale, int nestsNumber, double destroyedNests)
    {
        this.levyScale = new DoubleParameter(0, 5, 0.1, levyScale);
        this.nestsNumber = new IntegerParameter(5, 100, 5, nestsNumber);
        this.destroyedNests = new DoubleParameter(0, 0.8, 0.1, destroyedNests);
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
        //System.out.println("Add " + x * distance + " to ");
        levyScale.add(x * distance);
        nestsNumber.add((int)(y * distance));
        destroyedNests.add(z * distance);
    }
    
    public String toString()
    {
        return "levy scale = " + levyScale.currentValue + 
                ", nests number = " + nestsNumber.currentValue + 
                ", destroyed nests = " + destroyedNests.currentValue * nestsNumber.currentValue;
    }
}
