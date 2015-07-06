package org.getalp.lexsema.wsd.parameters.cuckoo;

import java.util.Random;

import org.getalp.lexsema.wsd.parameters.ScalarParameter;
import org.getalp.lexsema.wsd.parameters.method.Parameters;

public class CuckooParameters implements Parameters
{
    public static final Random random = new Random();

    public ScalarParameter levyLocation;
    
    public ScalarParameter levyScale;
    
    public ScalarParameter nestsNumber;
    
    public ScalarParameter destroyedNests;
    
    public CuckooParameters()
    {
        levyLocation = new ScalarParameter(0, 20);
        levyScale = new ScalarParameter(0, 5);
        nestsNumber = new ScalarParameter(1, 100);
        destroyedNests = new ScalarParameter(1, nestsNumber);
    }
    
    public CuckooParameters(double levyLocation, double levyScale, int nestsNumber, int destroyedNests)
    {
        this();
        this.levyLocation.currentValue = levyLocation;
        this.levyScale.currentValue = levyScale;
        this.nestsNumber.currentValue = nestsNumber;
        this.destroyedNests.currentValue = destroyedNests;
    }

    public CuckooParameters clone()
    {
        CuckooParameters ret = new CuckooParameters();
        ret.levyLocation.currentValue = levyLocation.currentValue;
        ret.levyScale.currentValue = levyScale.currentValue;
        ret.nestsNumber.currentValue =  nestsNumber.currentValue;
        ret.destroyedNests.currentValue = destroyedNests.currentValue;
        return ret;
    }
    
    public void makeRandomChanges(double distance)
    {
        double[] parameters = new double[4];
        double max = 0;
        for (int i = 0 ; i < parameters.length ; i++)
        {
            parameters[i] = random.nextGaussian();
            max = Math.max(Math.abs(max), Math.abs(parameters[i]));
        }
        for (int i = 0 ; i < parameters.length ; i++)
        {
            parameters[i] /= max;
        }
        levyLocation.add(parameters[0] * distance);
        levyScale.add(parameters[1] * distance);
        nestsNumber.add(parameters[2] * distance);
        destroyedNests.add(parameters[3] * distance);
        /*
        System.out.println("Move levyLocation : " + parameters[0] * distance * levyLocation.step);
        System.out.println("Move levyScale : " + parameters[1] * distance * levyScale.step);
        System.out.println("Move nestsNumber : " + parameters[2] * distance * nestsNumber.step);
        System.out.println("Move destroyedNests : " + parameters[3] * distance * destroyedNests.step);
        */
    }
    
    public String toString()
    {
        return "levy location = " + levyLocation.currentValue + 
                ", levy scale = " + levyScale.currentValue + 
                ", nests number = " + (int) nestsNumber.currentValue + 
                ", destroyed nests = " + (int) (destroyedNests.currentValue - 1);
    }
}
