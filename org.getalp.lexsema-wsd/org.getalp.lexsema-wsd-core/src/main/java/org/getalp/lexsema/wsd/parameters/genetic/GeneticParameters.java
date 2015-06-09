package org.getalp.lexsema.wsd.parameters.genetic;

import java.util.Random;

import org.getalp.lexsema.wsd.parameters.ScalarParameter;
import org.getalp.lexsema.wsd.parameters.method.Parameters;

public class GeneticParameters implements Parameters
{
    public static final Random random = new Random();
    
    public ScalarParameter population;
    
    public ScalarParameter crossoverRate;
    
    public ScalarParameter mutationRate;
    
    public GeneticParameters()
    {
        population = new ScalarParameter(2, 100);
        crossoverRate = new ScalarParameter(0, 1);
        mutationRate = new ScalarParameter(0, 1);
    }

    public GeneticParameters clone()
    {
        GeneticParameters ret = new GeneticParameters();
        ret.population.currentValue = population.currentValue;
        ret.crossoverRate.currentValue =  crossoverRate.currentValue;
        ret.mutationRate.currentValue = mutationRate.currentValue;
        return ret;
    }
    
    public void makeRandomChanges(double distance)
    {
        double x = random.nextGaussian();
        double y = random.nextGaussian();
        double z = random.nextGaussian();
        double max = Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z));
        x /= max;
        y /= max;
        z /= max;
        population.add(x * distance);
        crossoverRate.add(y * distance);
        mutationRate.add(z * distance);
        /*
        System.out.println("Move levyScale : " + x * distance * population.step);
        System.out.println("Move nestsNumber : " + y * distance * crossoverRate.step);
        System.out.println("Move destroyedNests : " + z * distance * mutationRate.step);
        */
    }
    
    public String toString()
    {
        return "population = " + (int) population.currentValue + 
                ", crossover rate = " + crossoverRate.currentValue + 
                ", mutation rate = " + mutationRate.currentValue;
    }
}
