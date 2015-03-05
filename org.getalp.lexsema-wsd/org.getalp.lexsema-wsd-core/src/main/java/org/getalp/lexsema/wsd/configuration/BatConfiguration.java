package org.getalp.lexsema.wsd.configuration;

import java.util.Random;
import org.getalp.lexsema.similarity.Document;

public class BatConfiguration implements Configuration
{
	int[] assignments;
	
	Document document;

	public BatConfiguration(Document d)
	{
		document = d;
		assignments = new int[d.size()];
		Random r = new Random();
		for (int i = 0; i < assignments.length ; i++)
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

	@Override
	public void setSense(int wordIndex, int senseIndex)
	{
		int sensesNumber = document.getSenses(wordIndex).size();
		while (senseIndex < 0) senseIndex += sensesNumber;
		assignments[wordIndex] = senseIndex % sensesNumber;
	}

	@Override
	public int getAssignment(int wordIndex)
	{
		return assignments[wordIndex];
	}

	@Override
	public int size()
	{
		return assignments.length;
	}

	@Override
	public int getStart()
	{
		return 0;
	}

	@Override
	public int getEnd()
	{
		return assignments.length;
	}

	@Override
	public String toString()
	{
		String out = "[ ";
		for (int i = getStart(); i < getEnd(); i++) 
		{
			out += assignments[i] + ", ";
		}
		out += "]";
		return out;
	}

	@Override
	public void initialize(int value) {
		for (int i = getStart(); i < getEnd(); i++) 
		{
			setSense(i, value);
		}
	}

	@Override
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

	@Override
	public int[] getAssignments() 
	{
		return assignments;
	}

	@Override
	public void setConfidence(int wordIndex, double confidence) 
	{
		
	}

	@Override
	public double getConfidence(int wordIndex) 
	{
		return 0;
	}

}
