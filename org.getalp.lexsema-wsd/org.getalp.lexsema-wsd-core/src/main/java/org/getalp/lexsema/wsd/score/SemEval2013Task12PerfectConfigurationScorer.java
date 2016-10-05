package org.getalp.lexsema.wsd.score;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.wsd.configuration.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;

public class SemEval2013Task12PerfectConfigurationScorer implements PerfectConfigurationScorer
{
	String scorerProgramPath;
	
	String keyFilePath;
	
    public SemEval2013Task12PerfectConfigurationScorer(String scorerProgramPath, String keyFilePath)
    {
		this.scorerProgramPath = scorerProgramPath;
		this.keyFilePath = keyFilePath;
    }
    
    public double computeScore(Document d, Configuration c)
    {  	
    	File tmp = null;
        PrintStream ps = null;
        try { tmp = File.createTempFile("tmp", ".tmp");
        	ps = new PrintStream(tmp); } catch (IOException e) { throw new RuntimeException(e); }
        for (int j = 0 ; j < d.size() ; j++) 
        {
            if (d.getWord(j).getId() != null && !d.getWord(j).getId().isEmpty()) 
            {
                if (c.getAssignment(j) >= 0 && !d.getSenses(j).isEmpty())
                {
                    ps.printf("%s %s %s \n", d.getId(), d.getWord(j).getId(), d.getSenses(j).get(c.getAssignment(j)).getId());
                }
            }
        }
        ps.close();
        double score = 0;
        try {
        	ProcessBuilder pb = new ProcessBuilder(scorerProgramPath, tmp.getAbsolutePath(), keyFilePath);
        	Process pr = pb.start();  
        	BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        	String line;
        	while ((line = in.readLine()) != null) 
        	{
        		if (line.contains("precision:"))
        		{
        			score = Double.valueOf(line.substring(line.indexOf("precision:") + 11, line.indexOf(" (")));
        			break;
        		}
        	}
        	pr.waitFor();
        	in.close();
			return score;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    public double computeTotalScore(List<Document> documents, List<Configuration> configurations)
    {
    	File tmp = null;
        PrintStream ps = null;
        try { tmp = File.createTempFile("tmp", ".tmp");
        	ps = new PrintStream(tmp); } catch (IOException e) { throw new RuntimeException(e); }
        for (int i = 0 ; i < documents.size() ; i++)
        {
        	Document d = documents.get(i);
        	Configuration c = configurations.get(i);
	        for (int j = 0 ; j < d.size() ; j++) 
	        {
	            if (d.getWord(j).getId() != null && !d.getWord(j).getId().isEmpty()) 
	            {
	                if (c.getAssignment(j) >= 0 && !d.getSenses(j).isEmpty())
	                {
	                    ps.printf("%s %s %s \n", d.getId(), d.getWord(j).getId(), d.getSenses(j).get(c.getAssignment(j)).getId());
	                }
	            }
	        }
        }
        ps.close();
        double score = 0;
        try {
        	ProcessBuilder pb = new ProcessBuilder(scorerProgramPath, tmp.getAbsolutePath(), keyFilePath);
        	Process pr = pb.start();  
        	BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        	String line;
        	while ((line = in.readLine()) != null) 
        	{
        		if (line.contains("precision:"))
        		{
        			score = Double.valueOf(line.substring(line.indexOf("precision:") + 11, line.indexOf(" (")));
        			break;
        		}
        	}
        	pr.waitFor();
        	in.close();
			return score;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }


    public void release()
    {
        
    }
}
