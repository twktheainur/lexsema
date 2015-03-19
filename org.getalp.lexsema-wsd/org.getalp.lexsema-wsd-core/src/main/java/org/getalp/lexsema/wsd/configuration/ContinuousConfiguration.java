package org.getalp.lexsema.wsd.configuration;

import org.getalp.lexsema.similarity.Document;

import java.util.Random;

public class ContinuousConfiguration implements Configuration
{
    private double[] assignments;
    
    private Document document;

    public ContinuousConfiguration(Document d)
    {
        document = d;
        assignments = new double[d.size()];
        Random r = new Random();
        for (int i = 0; i < assignments.length; i++)
        {
            setSense(i, r.nextInt(d.getSenses(i).size()));
        }
    }

    public ContinuousConfiguration(Document d, double[] senses)
    {
        document = d;
        assignments = new double[d.size()];
        setSenses(senses);
    }

    public void setSenses(int[] senses)
    {
        for (int i = 0 ; i < Math.min(senses.length, assignments.length) ; i++)
        {
            setSense(i, senses[i]);
        }
    }
    
    public void setSenses(double[] senses)
    {
        for (int i = 0 ; i < Math.min(senses.length, assignments.length) ; i++)
        {
            setSense(i, senses[i]);
        }
    }

    public void setSense(int wordIndex, int senseIndex)
    {
        int sensesNumber = document.getSenses(wordIndex).size();
        while (senseIndex < 0) senseIndex += sensesNumber;
        assignments[wordIndex] = senseIndex % sensesNumber;
    }

    public void setSense(int wordIndex, double senseIndex)
    {
        int sensesNumber = document.getSenses(wordIndex).size();
        while (senseIndex < 0) senseIndex += sensesNumber;
        assignments[wordIndex] = senseIndex % sensesNumber;
    }

    public int getAssignment(int wordIndex)
    {
        return (int) assignments[wordIndex];
    }

    public int size()
    {
        return assignments.length;
    }

    public int getStart()
    {
        return 0;
    }

    public int getEnd()
    {
        return assignments.length;
    }

    public String toString()
    {
        String out = "[ ";
        for (int i = getStart() ; i < getEnd() ; i++)
        {
            out += (int) assignments[i] + ", ";
        }
        out += "]";
        return out;
    }

    public void initialize(int value)
    {
        for (int i = getStart(); i < getEnd(); i++)
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
        int[] ret = new int[assignments.length];
        for (int i = 0 ; i < assignments.length ; i++) ret[i] = (int) assignments[i];
        return ret;
    }
    
    public double[] getAssignmentsAsDouble()
    {
        return assignments;
    }

    public void setConfidence(int wordIndex, double confidence)
    {
        
    }

    public double getConfidence(int wordIndex)
    {
        return 0;
    }

}
