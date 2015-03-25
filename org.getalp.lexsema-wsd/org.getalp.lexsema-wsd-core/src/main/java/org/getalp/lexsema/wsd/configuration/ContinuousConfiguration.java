package org.getalp.lexsema.wsd.configuration;

import org.getalp.lexsema.similarity.Document;

import java.util.Random;

public class ContinuousConfiguration implements Configuration
{
    private Document document;

    private int size;
    
    private double[] continuousAssignments;
    
    private int[] discreteAssignments;
    
    public ContinuousConfiguration(Document d)
    {
        document = d;
        size = d.size();
        continuousAssignments = new double[size];
        discreteAssignments = new int[size];
        setRandomSenses();
    }

    public ContinuousConfiguration(Document d, double[] senses)
    {
        document = d;
        size = d.size();
        continuousAssignments = new double[size];
        discreteAssignments = new int[size];
        setSenses(senses);
    }

    public void setSenses(int[] senses)
    {
        if (senses.length != size) return;
        for (int i = 0 ; i < size ; i++)
        {
            setSense(i, senses[i]);
        }
    }
    
    public void setSenses(double[] senses)
    {
        if (senses.length != size) return;
        for (int i = 0 ; i < size ; i++)
        {
            setSense(i, senses[i]);
        }
    }
    
    public void setRandomSenses()
    {
        Random r = new Random();
        for (int i = 0 ; i < size ; i++)
        {
            setSense(i, r.nextInt(document.getSenses(i).size()));
        }
    }

    public void setSense(int wordIndex, int senseIndex)
    {
        int sensesNumber = document.getSenses(wordIndex).size();
        while (senseIndex < 0) senseIndex += sensesNumber;
        continuousAssignments[wordIndex] = senseIndex % sensesNumber;
        discreteAssignments[wordIndex] = senseIndex % sensesNumber;
    }

    public void setSense(int wordIndex, double senseIndex)
    {
        int sensesNumber = document.getSenses(wordIndex).size();
        while (senseIndex < 0) senseIndex += sensesNumber;
        continuousAssignments[wordIndex] = senseIndex % sensesNumber;
        discreteAssignments[wordIndex] = (int) (senseIndex % sensesNumber);
    }

    public int getAssignment(int wordIndex)
    {
        return discreteAssignments[wordIndex];
    }

    public int size()
    {
        return this.size;
    }

    public int getStart()
    {
        return 0;
    }

    public int getEnd()
    {
        return size;
    }

    public String toString()
    {
        String out = "[ ";
        for (int i = getStart() ; i < getEnd() ; i++)
        {
            out += discreteAssignments[i] + ", ";
        }
        out += "]";
        return out;
    }

    public void initialize(int value)
    {
        for (int i = getStart() ; i < getEnd(); i++)
        {
            setSense(i, value);
        }
    }

    public int countUnassigned()
    {
        return 0;
    }

    public int[] getAssignments()
    {
        return discreteAssignments;
    }
    
    public double[] getAssignmentsAsDouble()
    {
        return continuousAssignments;
    }

    public void setConfidence(int wordIndex, double confidence)
    {
        
    }

    public double getConfidence(int wordIndex)
    {
        return 0;
    }

}
