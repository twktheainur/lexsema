package org.getalp.lexsema.wsd.method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;

public class VoteDisambiguator implements Disambiguator
{
    private List<Disambiguator> disambiguators;
    
    private int n;
    
    public VoteDisambiguator(Disambiguator disambiguator, int n)
    {
        this.disambiguators = new ArrayList<>();
        this.disambiguators.add(disambiguator);
        this.n = n;
    }

    public VoteDisambiguator(List<Disambiguator> disambiguators, int n)
    {
        this.disambiguators = new ArrayList<>(disambiguators);
        this.n = n;
    }

    public Configuration disambiguate(Document[] documents)
    {
        int nbWords = documents[0].size();
        int l = documents.length;
        int m = disambiguators.size();
        Configuration[] configurations = new Configuration[n * m * l];
        for (int k = 0 ; k < l ; k++)
        {
            for (int i = 0 ; i < m ; i++)
            {
                for (int j = 0 ; j < n ; j++)
                {
                    System.out.println("" + (j + (i * n) + (k * m * n)) + "/" + (n * m * l) + "...");
                    configurations[j + (i * n) + (k * m * n)] = disambiguators.get(i).disambiguate(documents[k]);
                }
            }
        }

        int[] finalSenses = new int[nbWords];
        for (int i = 0 ; i < nbWords ; i++)
        {
            HashMap<Integer, Integer> candidates = new HashMap<>();
            for (int j = 0 ; j < configurations.length ; j++)
            {
                int assignment = configurations[j].getAssignment(i);
                if (candidates.containsKey(assignment))
                {
                    int oldValue = candidates.get(assignment);
                    candidates.put(assignment, oldValue + 1);
                }
                else
                {
                    candidates.put(assignment, 0);
                }
            }
            int maxKey = -1;
            int maxValue = -1;
            for (Integer j : candidates.keySet())
            {
                if (candidates.get(j) > maxValue)
                {
                    maxKey = j;
                    maxValue = candidates.get(j);
                }
            }
            finalSenses[i] = maxKey;
        }
        
        Configuration finalConfiguration = new ContinuousConfiguration(documents[0], finalSenses);
        return finalConfiguration;
    }
    
    public Configuration disambiguate(Document document)
    {
        return disambiguate(new Document[]{document});
    }
    
    public Configuration disambiguate(Document document, Configuration c)
    {
        return disambiguate(document);
    }
    
    public void release()
    {
        for (Disambiguator disambiguator : disambiguators)
        {
            disambiguator.release();
        }
    }
}
