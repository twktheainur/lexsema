package org.getalp.lexsema.io.text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicollecteFrenchLemmatizer
{
    private String pathToDico;

    private Map<String, List<String>> toLemmaMap;

    public DicollecteFrenchLemmatizer(String pathToDicollecteTxt)
    {
        this.pathToDico = pathToDicollecteTxt;
        parseDico();
    }
    
    public String getLemma(String token)
    {
        List<String> lemmas = toLemmaMap.get(token);
        if (lemmas == null || lemmas.isEmpty()) return token;
        return lemmas.get(0);
    }

    public List<String> getLemmas(String token)
    {
        List<String> lemmas = toLemmaMap.get(token);
        if (lemmas == null || lemmas.isEmpty()) return asNewList(token);
        else return asNewList(lemmas);
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
                    String flexion = tokens[1];
                    String lemme = tokens[2];
                    List<String> lemmas = toLemmaMap.get(flexion);
                    if (lemmas == null)
                    {
                        toLemmaMap.put(flexion, asNewList(lemme));
                    }
                    else if (!lemmas.contains(lemme))
                    {
                        lemmas.add(lemme);
                    }
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
    
    private static List<String> asNewList(List<String> list)
    {
        List<String> ret = new ArrayList<>();
        for (String str : list) ret.add(str);
        return ret;
    }

    private static List<String> asNewList(String str)
    {
        List<String> ret = new ArrayList<>();
        ret.add(str);
        return ret;
    }
    
    public static void main(String[] args)
    {
        DicollecteFrenchLemmatizer lemmatizer = new DicollecteFrenchLemmatizer("../data/dicollecte/lexique-dicollecte-fr-v5.6.txt");
        lemmatizer.test("folichonne"); // "folichon" (nom) et "folichonner" (verbe)
        lemmatizer.test("patate");
        lemmatizer.test("fût");
        lemmatizer.test("trainassent");
        lemmatizer.test("rassissiez");
    }
    
    private void test(String token)
    {
        List<String> lemmas = getLemmas(token);
        System.out.print("Lemmes de \"" + token + "\" : ");
        for (String lemma : lemmas)
        {
            System.out.print("\"" + lemma + "\" ");
        }
        System.out.println();
    }
}
