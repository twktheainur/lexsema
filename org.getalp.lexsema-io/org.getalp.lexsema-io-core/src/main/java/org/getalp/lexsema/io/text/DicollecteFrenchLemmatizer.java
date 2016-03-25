package org.getalp.lexsema.io.text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class DicollecteFrenchLemmatizer
{
    private String pathToDico;

    private Map<String, String> toLemmaMap;

    public DicollecteFrenchLemmatizer(String pathToDicollecteTxt)
    {
        this.pathToDico = pathToDicollecteTxt;
        parseDico();
    }
    
    public String getLemma(String token)
    {
        String lemma = toLemmaMap.get(token);
        if (lemma != null) return lemma;
        return token;
    }

    private void parseDico()
    {
        this.toLemmaMap = new HashMap<>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(this.pathToDico)); 
            boolean inHeader = true;
            for(String line; (line = br.readLine()) != null; ) 
            {
                if (inHeader)
                {
                    if (line.substring(0, 2).equals("id"))
                    {
                        inHeader = true;
                    }
                    else
                    {
                        inHeader = false;
                    }
                }
                else
                {
                    String[] tokens = line.split("\\s+");
                    String flexion = tokens[1];
                    String lemme = tokens[2];
                    this.toLemmaMap.put(flexion, lemme);
                }
            }
            br.close();
        }
        catch (Exception e)
        {
            // just because I'm against checked exceptions
            throw new RuntimeException(e);
        }
    }
}
