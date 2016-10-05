package org.getalp.lexsema.axalign.closure;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.util.Language;

import java.util.*;

public class LexicalResourceTranslationClosureWithSignatures implements LexicalResourceTranslationClosure<Sense> {
    private final Map<Language, Map<LexicalEntry, Set<Sense>>> closureData = new HashMap<>();

    @Override
    public void addSenses(Language language, LexicalEntry lexicalEntry, Collection<Sense> senses) {
        create(language, lexicalEntry);
        closureData.get(language).get(lexicalEntry).addAll(senses);
    }

    @Override
    public void addSense(Language language, LexicalEntry lexicalEntry, Sense sense) {
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
        @SuppressWarnings("All")
        Map<Language, Map<LexicalEntry, Set<Sense>>> otherClosureData = closure.senseClosureByLanguageAndEntry();
        for (Map.Entry<Language, Map<LexicalEntry, Set<Sense>>> languageMapEntry : otherClosureData.entrySet()) {
            for (LexicalEntry localLexicalEntry : languageMapEntry.getValue().keySet()) {
                addSenses(languageMapEntry.getKey(), localLexicalEntry, languageMapEntry.getValue().get(localLexicalEntry));
            }
        }
    }

    @Override
    public Map<Language, Map<LexicalEntry, Set<Sense>>> senseClosureByLanguageAndEntry() {
        return Collections.unmodifiableMap(closureData);
    }

    @Override
    public Map<LexicalEntry, Set<Sense>> senseClosureByEntry() {
        Map<LexicalEntry, Set<Sense>> senseClosureByEntry = new HashMap<>();
        for (Map.Entry<Language, Map<LexicalEntry, Set<Sense>>> languageMapEntry : closureData.entrySet()) {
            senseClosureByEntry.putAll(languageMapEntry.getValue());
        }
        return senseClosureByEntry;
    }

    @Override
    public Map<Language, Set<LexicalEntry>> entryClosureByLanguage() {
        Map<Language, Set<LexicalEntry>> entryClosureByLanguage = new HashMap<>();
        for (Map.Entry<Language, Map<LexicalEntry, Set<Sense>>> languageMapEntry : closureData.entrySet()) {
            entryClosureByLanguage.put(languageMapEntry.getKey(), languageMapEntry.getValue().keySet());
        }
        return entryClosureByLanguage;
    }

    @Override
    public Set<LexicalEntry> entryFlatClosure() {
        Set<LexicalEntry> entryFlatClosure = new TreeSet<>();
        for (Map.Entry<Language, Map<LexicalEntry, Set<Sense>>> languageMapEntry : closureData.entrySet()) {
            entryFlatClosure.addAll(languageMapEntry.getValue().keySet());
        }
        return entryFlatClosure;
    }

    @Override
    public Set<Sense> senseFlatClosure() {
        Set<Sense> senseFlatClosure = new TreeSet<>();
        for (Map.Entry<Language, Map<LexicalEntry, Set<Sense>>> languageMapEntry : closureData.entrySet()) {
            for (LexicalEntry localLexicalEntry : languageMapEntry.getValue().keySet()) {
                senseFlatClosure.addAll(languageMapEntry.getValue().get(localLexicalEntry));
            }
        }
        return senseFlatClosure;
    }

    @Override
    public String toString() {
        String output = "";
        for (Map.Entry<Language, Map<LexicalEntry, Set<Sense>>> languageMapEntry : closureData.entrySet()) {
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
