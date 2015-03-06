package org.getalp.lexsema.wsd.configuration;

<<<<<<< HEAD
import org.getalp.lexsema.similarity.Document;

import java.util.Random;

public class BatConfiguration implements Configuration {
	
=======
import java.util.Random;
import org.getalp.lexsema.similarity.Document;

public class BatConfiguration implements Configuration
{
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
	int[] assignments;
	
	Document document;

<<<<<<< HEAD
	public BatConfiguration(Document d) {
		document = d;
		assignments = new int[d.size()];
		Random r = new Random();
		for (int i = 0; i < assignments.length ; i++) {
			setSense(i, r.nextInt());
		}
	}
	
	public void setSenses(int[] senses) {
		for (int i = 0 ; i < Math.min(senses.length, assignments.length) ; i++) {
=======
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
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
			setSense(i, senses[i]);
		}
	}

	@Override
<<<<<<< HEAD
	public void setSense(int wordIndex, int senseIndex) {
=======
	public void setSense(int wordIndex, int senseIndex)
	{
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
		int sensesNumber = document.getSenses(wordIndex).size();
		while (senseIndex < 0) senseIndex += sensesNumber;
		assignments[wordIndex] = senseIndex % sensesNumber;
	}

	@Override
<<<<<<< HEAD
	public int getAssignment(int wordIndex) {
=======
	public int getAssignment(int wordIndex)
	{
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
		return assignments[wordIndex];
	}

	@Override
<<<<<<< HEAD
	public int size() {
=======
	public int size()
	{
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
		return assignments.length;
	}

	@Override
<<<<<<< HEAD
	public int getStart() {
=======
	public int getStart()
	{
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
		return 0;
	}

	@Override
<<<<<<< HEAD
	public int getEnd() {
=======
	public int getEnd()
	{
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
		return assignments.length;
	}

	@Override
<<<<<<< HEAD
	public String toString() {
		String out = "[ ";
		for (int i = getStart(); i < getEnd(); i++) {
=======
	public String toString()
	{
		String out = "[ ";
		for (int i = getStart(); i < getEnd(); i++) 
		{
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
			out += assignments[i] + ", ";
		}
		out += "]";
		return out;
	}

	@Override
	public void initialize(int value) {
<<<<<<< HEAD
		for (int i = getStart(); i < getEnd(); i++) {
=======
		for (int i = getStart(); i < getEnd(); i++) 
		{
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
			setSense(i, value);
		}
	}

	@Override
<<<<<<< HEAD
	public int countUnassigned() {
		int unassignedCount = 0;
		for (int assignment : assignments) {
			if (assignment == -1) {
=======
	public int countUnassigned() 
	{
		int unassignedCount = 0;
		for (int assignment : assignments) 
		{
			if (assignment == -1) 
			{
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
				unassignedCount++;
			}
		}
		return unassignedCount;
	}

	@Override
<<<<<<< HEAD
	public int[] getAssignments() {
=======
	public int[] getAssignments() 
	{
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
		return assignments;
	}

	@Override
<<<<<<< HEAD
	public void setConfidence(int wordIndex, double confidence) {
=======
	public void setConfidence(int wordIndex, double confidence) 
	{
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
		
	}

	@Override
<<<<<<< HEAD
	public double getConfidence(int wordIndex) {
=======
	public double getConfidence(int wordIndex) 
	{
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
		return 0;
	}

}
