package org.getalp.lexsema.wsd.configuration;

import org.getalp.lexsema.similarity.Document;

import java.util.Random;

public class ContinuousConfiguration implements Configuration
{
    private static final Random random = new Random();
    
    private Document document;

    private int documentSize;
    
    private int[] assignments;
    
    public ContinuousConfiguration(Document d)
    {
        document = d;
        documentSize = d.size();
        assignments = new int[documentSize];
        setRandomSenses();
    }

    public ContinuousConfiguration(Document d, int[] senses)
    {
        document = d;
        documentSize = d.size();
        assignments = new int[documentSize];
        setSenses(senses);
    }

    public void setSenses(int[] senses)
    {
        if (senses.length != documentSize) return;
        for (int i = 0 ; i < documentSize ; i++)
        {
            setSense(i, senses[i]);
        }
    }

    public void setRandomSenses()
    {
        Random r = new Random();
        for (int i = 0 ; i < documentSize ; i++)
        {
            setSense(i, r.nextInt(document.getSenses(i).size()));
        }
    }
    
    public void makeRandomChanges(int numberOfChanges)
    {
        for (int i = 0 ; i < numberOfChanges ; i++)
        {
            makeRandomChange();
        }
    }
    
    public void makeRandomChange()
    {
        int randomIndex = random.nextInt(documentSize);
        setSense(randomIndex, random.nextInt(getNumberOfSenses(randomIndex)));
    }
    
    public int getNumberOfSenses(int wordIndex)
    {
        return document.getSenses(wordIndex).size();
    }

    public void setSense(int wordIndex, int senseIndex)
    {
        int sensesNumber = document.getSenses(wordIndex).size();
        while (senseIndex < 0) senseIndex += sensesNumber;
        assignments[wordIndex] = senseIndex % sensesNumber;
    }

    public int getAssignment(int wordIndex)
    {
        return assignments[wordIndex];
    }

    public int size()
    {
        return documentSize;
    }

    public int getStart()
    {
        return 0;
    }

    public int getEnd()
    {
        return documentSize;
    }

    public String toString()
    {
        String out = "[ ";
        for (int i = getStart() ; i < getEnd() ; i++)
        {
            out += assignments[i] + ", ";
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
