package org.getalp.lexsema.io.text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
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
                System.out.println(line);
                if (inHeader)
                {
                    if (line.length() > 2 && line.substring(0, 2).equals("id"))
                    {
                        inHeader = false;
                    }
                    else
                    {
                        inHeader = true;
                    }
                }
                else
                {
                    String[] tokens = line.split("\\s+");
                    System.out.println(Arrays.toString(tokens));
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
