package org.getalp.lexsema.acceptali.closure;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.util.Language;

import java.util.*;

public class LexicalResourceTranslationClosureImpl implements LexicalResourceTranslationClosure<LexicalSense> {
    private final Map<Language, Map<LexicalEntry, Set<LexicalSense>>> closureData = new HashMap<>();

    @Override
    public void addSenses(Language language, LexicalEntry lexicalEntry, Collection<LexicalSense> senses) {
        create(language, lexicalEntry);
        closureData.get(language).get(lexicalEntry).addAll(senses);
    }

    @Override
    public void addSense(Language language, LexicalEntry lexicalEntry, LexicalSense sense) {
        create(language, lexicalEntry);
        closureData.get(language).get(lexicalEntry).add(sense);
    }

    private void create(Language language, LexicalEntry lexicalEntry) {
        if (!closureData.containsKey(language)) {
            closureData.put(language, new HashMap<>());
        }
        if (!closureData.get(language).containsKey(lexicalEntry)) {
            closureData.get(language).put(lexicalEntry, new TreeSet<>());
        }
    }

    @Override
    public void importClosure(LexicalResourceTranslationClosure closure) {
        @SuppressWarnings("all")
        Map<Language, Map<LexicalEntry, Set<LexicalSense>>> otherClosureData = closure.senseClosureByLanguageAndEntry();
        for (Map.Entry<Language, Map<LexicalEntry, Set<LexicalSense>>> languageMapEntry : otherClosureData.entrySet()) {
            for (LexicalEntry localLexicalEntry : languageMapEntry.getValue().keySet()) {
                addSenses(languageMapEntry.getKey(), localLexicalEntry, languageMapEntry.getValue().get(localLexicalEntry));
            }
        }
    }

    @Override
    public Map<Language, Map<LexicalEntry, Set<LexicalSense>>> senseClosureByLanguageAndEntry() {
        return Collections.unmodifiableMap(closureData);
    }

    @Override
    public Map<LexicalEntry, Set<LexicalSense>> senseClosureByEntry() {
        Map<LexicalEntry, Set<LexicalSense>> senseClosureByEntry = new HashMap<>();
        for (Map.Entry<Language, Map<LexicalEntry, Set<LexicalSense>>> languageMapEntry : closureData.entrySet()) {
            senseClosureByEntry.putAll(languageMapEntry.getValue());
        }
        return senseClosureByEntry;
    }

    @Override
    public Map<Language, Set<LexicalEntry>> entryClosureByLanguage() {
        Map<Language, Set<LexicalEntry>> entryClosureByLanguage = new HashMap<>();
        for (Map.Entry<Language, Map<LexicalEntry, Set<LexicalSense>>> languageMapEntry : closureData.entrySet()) {
            entryClosureByLanguage.put(languageMapEntry.getKey(), languageMapEntry.getValue().keySet());
        }
        return entryClosureByLanguage;
    }

    @Override
    public Set<LexicalEntry> entryFlatClosure() {
        Set<LexicalEntry> entryFlatClosure = new TreeSet<>();
        for (Map.Entry<Language, Map<LexicalEntry, Set<LexicalSense>>> languageMapEntry : closureData.entrySet()) {
            entryFlatClosure.addAll(languageMapEntry.getValue().keySet());
        }
        return entryFlatClosure;
    }

    @Override
    public Set<LexicalSense> senseFlatClosure() {
        Set<LexicalSense> senseFlatClosure = new TreeSet<>();
        for (Map.Entry<Language, Map<LexicalEntry, Set<LexicalSense>>> languageMapEntry : closureData.entrySet()) {
            for (LexicalEntry localLexicalEntry : languageMapEntry.getValue().keySet()) {
                senseFlatClosure.addAll(languageMapEntry.getValue().get(localLexicalEntry));
            }
        }
        return senseFlatClosure;
    }

    @Override
    public String toString() {
        String output = "";
        for (Map.Entry<Language, Map<LexicalEntry, Set<LexicalSense>>> languageMapEntry : closureData.entrySet()) {
            output = String.format("%s%sLANGUAGE:%s", output, System.lineSeparator(), languageMapEntry.getKey());
            for (LexicalEntry le : languageMapEntry.getValue().keySet()) {
                output = String.format("%s%s\t%s", output, System.lineSeparator(), le);
                for (LexicalSense ls : languageMapEntry.getValue().get(le)) {
                    output = String.format("%s%s\t\t%s", output, System.lineSeparator(), ls);
                }
            }
        }
        return output;
    }
}
