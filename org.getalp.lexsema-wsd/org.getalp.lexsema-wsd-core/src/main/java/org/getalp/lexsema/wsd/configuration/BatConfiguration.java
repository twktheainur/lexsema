package org.getalp.lexsema.wsd.configuration;

import org.getalp.lexsema.similarity.Document;

import java.util.Random;

public class BatConfiguration implements Configuration
{
    int[] assignments;
    
    Document document;

    public BatConfiguration(Document d)
    {
        document = d;
        assignments = new int[d.size()];
        Random r = new Random();
        for (int i = 0; i < assignments.length; i++)
        {
            setSense(i, r.nextInt(d.getSenses(i).size()));
        }
    }

    public void setSenses(int[] senses)
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

    @Override
    public void setSenseId(int wordIndex, String senseId) {

    }

    public int getAssignment(int wordIndex)
    {
        return assignments[wordIndex];
    }

    @Override
    public String getSenseId(int wordIndex) {
        return null;
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
            out += assignments[i] + ", ";
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
        int unassignedCount = 0;
        for (int assignment : assignments)
        {
            if (assignment == -1)
            {
                unassignedCount++;
            }
        }
        return unassignedCount;
    }

    public int[] getAssignments()
    {
        return assignments;
    }

    @Override
    public String[] getIdAssignments() {
        return null;
    }

    @Override
    public Document getDocument() {
        return document;
    }

    public void setConfidence(int wordIndex, double confidence)
    {
        
    }

    public double getConfidence(int wordIndex)
    {
        return 0;
    }
}
