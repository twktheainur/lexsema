package org.getalp.lexsema.wsd.score;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TestScorer implements ConfigurationScorer
{
    private SemEval2007Task7PerfectConfigurationScorer perfectScorer;
    
    private ConfigurationScorer scorerToTest;
    
    PrintWriter out;
    
    public TestScorer(ConfigurationScorer scorerToTest)
    {
        this.perfectScorer = new SemEval2007Task7PerfectConfigurationScorer();
        this.scorerToTest = scorerToTest;
        try
        {
            this.out = new PrintWriter("test.txt");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public double computeScore(Document d, Configuration c)
    {
        double x = perfectScorer.computeScore(d, c);
        double y = scorerToTest.computeScore(d, c);
        out.println("" + x + " " + y);
        out.flush();
        return y;
    }

    public void release()
    {
        perfectScorer.release();
        scorerToTest.release();
        out.close();
    }
}
