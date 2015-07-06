package org.getalp.lexsema.examples.datathon.multlex.lexicalization;

import org.getalp.lexsema.util.Language;

import java.util.List;
import java.util.Map;

public interface LexicalizationAlternatives {
    public void registerLexicalization(Language language, String lexicalization);
    public Map<Language, List<Lexicalization>> computeDistribution();
    public int size();

}
