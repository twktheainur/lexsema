package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.BatConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BatAlgorithm implements Disambiguator {

	private static final int batsNumber = 10;
	
	private static final int iterationsNumber = 10;
	
	private static final float minFrequency = 0;
	
	private static final float maxFrequency = 100;
	
	private static final float minLoudness = 0;
	
	private static final float maxLoudness = 100;
	
	private static final float minRate = 0;
	
	private static final float maxRate = 1;
	
	private static final float alpha = 0.9f;
	
	private static final float gamma = 0.9f;

	private static final Random random = new Random();

	
	private Document currentDocument;
	
	private ConfigurationScorer configurationScorer;
	
	private List<Bat> bats = new ArrayList<Bat>();
	
	private Bat bestBat;
	
	private class Bat {
		
		public BatConfiguration configuration;
		public int[] position;
		public int[] velocity;
		public float frequency;
		public float initialRate;
		public float rate;
		public float loudness;
		public int[] previousPosition;
		public int[] previousVelocity;
		double score;
		
		public void initialize(Document document) {
			
			configuration = new BatConfiguration(document);
			position = new int[document.size()];
			for (int i = 0 ; i < position.length ; i++) {
				position[i] = configuration.getAssignment(i);
			}
			velocity = new int[document.size()];
			for (int i = 0 ; i < velocity.length ; i++) {
				velocity[i] = 0;
			}
			frequency = randomFloatInRange(minFrequency, maxFrequency);
			initialRate = randomFloatInRange(minRate, maxRate);
			rate = initialRate;
			loudness = randomFloatInRange(minLoudness, maxLoudness);
			previousPosition = position.clone();
			previousVelocity = velocity.clone();
		}
		
	}
	
    public BatAlgorithm(SimilarityMeasure similarityMeasure) {
    	
    	int threadsNumber = Runtime.getRuntime().availableProcessors();
    	configurationScorer = new TverskyConfigurationScorer(similarityMeasure, threadsNumber);
    	for (int i = 0 ; i < batsNumber ; i++) {
    		bats.add(new Bat());
    	}
	}

	@Override
    public Configuration disambiguate(Document document) {
		
		currentDocument = document;
		
    	for (int i = 0 ; i < batsNumber ; i++) {
    		bats.get(i).initialize(document);
    	}
    	computeBestBat();
    	
    	for (int currentIteration = 0 ; currentIteration < iterationsNumber ; currentIteration++) {
    		
    		int progress = (int) (((float)currentIteration / (float)iterationsNumber) * 100);
    		System.out.println("Progress : " + progress + "%");
    		
    		for (int i = 0 ; i < batsNumber ; i++) {

    			Bat currentBat = bats.get(i);

    			currentBat.frequency = randomFloatInRange(minFrequency, maxFrequency);
    			
    			currentBat.previousPosition = currentBat.position.clone();
    			currentBat.previousVelocity = currentBat.velocity.clone();
    			
    			currentBat.velocity = add(currentBat.velocity, 
    								      multiply(substract(currentBat.position, bestBat.position), 
    								    		   currentBat.frequency));
    			currentBat.position = add(currentBat.position, currentBat.velocity);
    			
    			if (randomFloatInRange(0, 1) > currentBat.rate)
    			{
    				// fly randomly around best solutions ?
    			}
    			
    			currentBat.position = add(currentBat.position, randomFloatInRange(-1, 1) * computeAverageLoudness());
    			
    			if (randomFloatInRange(minLoudness, maxLoudness) < currentBat.loudness &&
    				computeScoreOfBat(currentBat) > bestBat.score)
    			{
        			currentBat.configuration.setSenses(currentBat.position);
        			currentBat.loudness *= alpha;
        			currentBat.rate = (float) (currentBat.initialRate * (1 - Math.exp(-gamma * currentIteration)));
    			}
    			else
    			{
    				currentBat.position = currentBat.previousPosition.clone();
    				currentBat.velocity = currentBat.previousVelocity.clone();
    			}
    			
    			computeBestBat();
    		}
    	}

    	return bestBat.configuration;
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c) {

    	return disambiguate(document);
    }

    @Override
    public void release() {
    	
    	configurationScorer.release();
    }
    
    private float randomFloatInRange(float min, float max){
    	return min + (max - min) * random.nextFloat();
    }
    
    private int[] substract(int[] leftOperand, int[] rightOperand)
    {
    	int[] result = new int[Math.min(leftOperand.length, rightOperand.length)];
    	for (int i = 0 ; i < result.length ; i++)
    	{
    		result[i] = leftOperand[i] - rightOperand[i];
    	}
    	return result;
    }
    
    private int[] multiply(int[] leftOperand, float rightOperand)
    {
    	int[] result = new int[leftOperand.length];
    	for (int i = 0 ; i < result.length ; i++)
    	{
    		result[i] = (int)(leftOperand[i] * rightOperand);
    	}
    	return result;
    }
    
    private int[] add(int[] leftOperand, int[] rightOperand)
    {
    	int[] result = new int[Math.min(leftOperand.length, rightOperand.length)];
    	for (int i = 0 ; i < result.length ; i++)
    	{
    		result[i] = leftOperand[i] + rightOperand[i];
    	}
    	return result;
    }
    
    private int[] add(int[] leftOperand, float rightOperand)
    {
    	int[] result = new int[leftOperand.length];
    	for (int i = 0 ; i < result.length ; i++)
    	{
    		result[i] = (int) (leftOperand[i] + rightOperand);
    	}
    	return result;
    }
   
    private void computeBestBat()
    {
    	double bestScore = Double.MIN_VALUE;
    	for (int i = 0 ; i < batsNumber ; i++)
    	{
    		double score = computeScoreOfBat(bats.get(i));
    		if (score > bestScore)
    		{
    			bestScore = score;
    			bestBat = bats.get(i);
    		}
    	}
    }
    
    private float computeAverageLoudness()
    {
    	float loudnessesSum = 0;
    	for (int i = 0 ; i < batsNumber ; i++)
    	{
    		loudnessesSum += bats.get(i).loudness;
    	}
    	return loudnessesSum / (float)batsNumber;
    }
    
    private double computeScoreOfBat(Bat bat)
    {
    	bat.score = configurationScorer.computeScore(currentDocument, bat.configuration);
    	return bat.score;
    }
}
