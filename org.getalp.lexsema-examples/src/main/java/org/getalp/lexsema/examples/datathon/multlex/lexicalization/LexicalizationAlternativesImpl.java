package org.getalp.lexsema.examples.datathon.multlex.lexicalization;


import org.getalp.lexsema.util.Language;

import java.util.*;

public class LexicalizationAlternativesImpl implements LexicalizationAlternatives{

    Map<Language,List<String>> lexicalizations = new HashMap<>();

    @Override
    public void registerLexicalization(Language language, String lexicalization) {
        if(!lexicalizations.containsKey(language)){
            lexicalizations.put(language,new ArrayList<String>());
        }
        lexicalizations.get(language).add(lexicalization);
    }

    @Override
    public Map<Language, List<Lexicalization>> computeDistribution() {
        Map<Language, List<Lexicalization>> languageDist  = new HashMap<>();
        for(Language language: lexicalizations.keySet()) {
            Map<String, Double> lexDistribution = new HashMap<>();
            for (String lexicalization : lexicalizations.get(language)) {
                if (!lexDistribution.containsKey(lexicalization)) {
                    lexDistribution.put(lexicalization, 0d);
                }
                lexDistribution.put(lexicalization, lexDistribution.get(lexicalization) + 1);
            }
            List<Lexicalization> lexicalizationList = new ArrayList<>();
            for (String key : lexDistribution.keySet()) {
                lexicalizationList.add(new LexicalizationImpl(key, lexDistribution.get(key) / lexicalizations.get(language).size()));
            }
            Collections.sort(lexicalizationList);
            languageDist.put(language,lexicalizationList);
        }
        return languageDist;
    }

    public int size(){
        return lexicalizations.size();
    }
}
