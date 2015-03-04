package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.BatConfiguration;
import org.getalp.lexsema.wsd.configuration.ConfidenceConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BatAlgorithm implements Disambiguator {

	private static final int batsNumber = 10;
	
	private static final int maxIterations = 10;
	
	private static final float minFrequency = 0;
	
	private static final float maxFrequency = 100;
	
	private static final float minLoudness = 0;
	
	private static final float maxLoudness = 100;
	
	private static final float alpha = 0.9f;
	
	private static final float gamma = alpha;

	private static final Random random = new Random();
	
	private ConfigurationScorer configurationScorer;
	
	private List<Bat> bats = new ArrayList<Bat>();
	
	private Bat bestBat;
	
	private Document document;
	
	private class Bat {
		
		public BatConfiguration configuration;
		public int[] position;
		public int[] velocity;
		public float frequency;
		public float rate;
		public float initialRate;
		public float loudness;
		
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
			initialRate = randomFloatInRange(0, 1);
			loudness = randomFloatInRange(minLoudness, maxLoudness);
		}
		
	}
	
    public BatAlgorithm(int numberThreads, SimilarityMeasure similarityMeasure) {
    	
    	configurationScorer = new TverskyConfigurationScorer(similarityMeasure, numberThreads);
    	for (int i = 0 ; i < batsNumber ; i++) {
    		bats.add(new Bat());
    	}
	}

	@Override
    public Configuration disambiguate(Document document) {
		
    	for (int i = 0 ; i < batsNumber ; i++) {
    		bats.get(i).initialize(document);
    	}
    	computeBestBat();
    	
    	for (int currentIteration = 0 ; currentIteration < maxIterations ; currentIteration++) {
    		for (int i = 0 ; i < batsNumber ; i++) {

    			Bat currentBat = bats.get(i);
    			currentBat.frequency = randomFloatInRange(minFrequency, maxFrequency);
    			currentBat.velocity = add(currentBat.velocity, 
    								      multiply(substract(currentBat.position, bestBat.position), 
    								    		   currentBat.frequency));
    			currentBat.position = add(currentBat.position, currentBat.velocity);
    			currentBat.configuration.setSenses(currentBat.position);
    			if (random.nextFloat() > currentBat.rate)
    			{
    				
    			}
    		}
    	}
    	
    	
    	/*
	 	Objective function f (x), x = (x 1 , ..., x d ) T
		Initialize the bat population x i (i = 1, 2, ..., n) and v i
		Define pulse frequency f i at x i
		Initialize pulse rates r i and the loudness A i
		while (t <Max number of iterations)
			Generate new solutions by adjusting frequency,
			and updating velocities and locations/solutions [equations (2) to (4)]
			if (rand > r i )
				Select a solution among the best solutions
				Generate a local solution around the selected best solution
			end if
			Generate a new solution by flying randomly
			if (rand < A i & f (x i ) < f (x ∗ ))
				Accept the new solutions
				Increase r i and reduce A i
			end if
			Rank the bats and find the current best x ∗
		 end while
		 Postprocess results and visualization
    	 */
	
	
    	return c;
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c) {

    	return disambiguate(document);
    }

    @Override
    public void release() {
    	
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
    
    private void computeBestBat()
    {
    	double bestScore = Double.MIN_VALUE;
    	for (int i = 0 ; i < batsNumber ; i++)
    	{
    		double score = configurationScorer.computeScore(document, bats.get(i).configuration);
    		if (score > bestScore)
    		{
    			bestScore = score;
    			bestBat = bats.get(i);
    		}
    	}
    }
}
