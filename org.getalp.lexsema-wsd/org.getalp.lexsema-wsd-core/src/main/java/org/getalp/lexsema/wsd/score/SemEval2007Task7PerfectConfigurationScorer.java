package org.getalp.lexsema.wsd.score;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;

public class SemEval2007Task7PerfectConfigurationScorer implements ConfigurationScorer
{
    private String scorerScriptPath;
    
    public SemEval2007Task7PerfectConfigurationScorer(String scorerScriptPath)
    {
        this.scorerScriptPath = scorerScriptPath;
    }
    
    public double computeScore(Document document, Configuration configuration)
    {
        try
        {
            Process process = Runtime.getRuntime().exec("mktemp");
            BufferedReader stdin = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process.waitFor();
            String randomFilePath = stdin.readLine();

            SemevalWriter sw = new SemevalWriter(randomFilePath);
            sw.write(document, configuration.getAssignments());
            
            process = Runtime.getRuntime().exec(new String[]{scorerScriptPath, randomFilePath});
            stdin = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process.waitFor();
            double score = Double.valueOf(stdin.readLine());
            
            Runtime.getRuntime().exec(new String[]{"rm", randomFilePath});
            
            return score;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return 0;
    }

    public void release()
    {
        
    }
}
