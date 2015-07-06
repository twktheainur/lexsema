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

    public ContinuousConfiguration(Document d, int initialization_value)
    {
        document = d;
        documentSize = d.size();
        assignments = new int[documentSize];
        initialize(initialization_value);
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
        for (int i = 0 ; i < documentSize ; i++)
        {
            setSenseRandom(i);
        }
    }
    
    public void makeRandomChanges(int numberOfChanges)
    {
        numberOfChanges = Math.min(numberOfChanges, documentSize);
        for (int i = 0 ; i < numberOfChanges ; i++)
        {
            makeRandomChange();
        }
    }
    
    public void makeRandomChange()
    {
        int randomIndex = random.nextInt(documentSize);
        setSenseRandom(randomIndex);
    }
    
    public void setSenseRandom(int wordIndex)
    {
        if (document.getSenses(wordIndex).isEmpty())
        {
        	assignments[wordIndex] = -1;
        }
        else
        {
        	assignments[wordIndex] = random.nextInt(document.getSenses(wordIndex).size());
        }
    }
    
    public void setSense(int wordIndex, int senseIndex)
    {
    	if (document.getSenses(wordIndex).isEmpty())
    	{
    		assignments[wordIndex] = -1;
    	}
    	else
    	{
	        int sensesNumber = document.getSenses(wordIndex).size();
	        while (senseIndex < 0) senseIndex += sensesNumber;
	        assignments[wordIndex] = senseIndex % sensesNumber;
    	}
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
        for (int i = 0 ; i < documentSize ; i++)
        {
            out += assignments[i] + ", ";
        }
        out += "]";
        return out;
    }

    public void initialize(int value)
    {
        for (int i = 0 ; i < documentSize ; i++)
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

    public Document getDocument()
    {
        return document;
    }

    public void setConfidence(int wordIndex, double confidence)
    {
        
    }

    public double getConfidence(int wordIndex)
    {
        return 0;
    }
    
    public ContinuousConfiguration clone()
    {
        return new ContinuousConfiguration(document, assignments);
    }

}
