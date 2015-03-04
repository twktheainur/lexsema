package org.getalp.lexsema.acceptali.closure;

import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.similarity.Sense;

import java.util.*;

public class LexicalResourceTranslationClosureWithSignatures implements LexicalResourceTranslationClosure<Sense> {
    Map<Language, Map<LexicalEntry, Set<Sense>>> closureData = new HashMap<>();

    public LexicalResourceTranslationClosureWithSignatures() {
    }

    @Override
    public void addSenses(Language language, LexicalEntry lexicalEntry, Collection<Sense> senses) {
        checkAndCreate(language, lexicalEntry);
        closureData.get(language).get(lexicalEntry).addAll(senses);
    }

    @Override
    public void addSense(Language language, LexicalEntry lexicalEntry, Sense sense) {
        checkAndCreate(language, lexicalEntry);
        closureData.get(language).get(lexicalEntry).add(sense);
    }

    private void checkAndCreate(Language language, LexicalEntry lexicalEntry) {
        if (!closureData.containsKey(language)) {
            closureData.put(language, new HashMap<LexicalEntry, Set<Sense>>());
        }
        if (!closureData.get(language).containsKey(lexicalEntry)) {
            closureData.get(language).put(lexicalEntry, new TreeSet<Sense>());
        }
    }

    @Override
    public void importClosure(LexicalResourceTranslationClosure closure) {
        Map<Language, Map<LexicalEntry, Set<Sense>>> otherClosureData = closure.senseClosureByLanguageAndEntry();
        for (Language language : otherClosureData.keySet()) {
            for (LexicalEntry localLexicalEntry : otherClosureData.get(language).keySet()) {
                addSenses(language, localLexicalEntry, otherClosureData.get(language).get(localLexicalEntry));
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
        for (Language language : closureData.keySet()) {
            senseClosureByEntry.putAll(closureData.get(language));
        }
        return senseClosureByEntry;
    }

    @Override
    public Map<Language, Set<LexicalEntry>> entryClosureByLanguage() {
        Map<Language, Set<LexicalEntry>> entryClosureByLanguage = new HashMap<>();
        for (Language language : closureData.keySet()) {
            entryClosureByLanguage.put(language, closureData.get(language).keySet());
        }
        return entryClosureByLanguage;
    }

    @Override
    public Set<LexicalEntry> entryFlatClosure() {
        Set<LexicalEntry> entryFlatClosure = new TreeSet<>();
        for (Language language : closureData.keySet()) {
            entryFlatClosure.addAll(closureData.get(language).keySet());
        }
        return entryFlatClosure;
    }

    @Override
    public Set<Sense> senseFlatClosure() {
        Set<Sense> senseFlatClosure = new TreeSet<>();
        for (Language language : closureData.keySet()) {
            for (LexicalEntry localLexicalEntry : closureData.get(language).keySet()) {
                senseFlatClosure.addAll(closureData.get(language).get(localLexicalEntry));
            }
        }
        return senseFlatClosure;
    }

    @Override
    public String toString() {
        String output = "";
        for (Language l : closureData.keySet()) {
            output = String.format("%s%sLANGUAGE:%s", output, System.lineSeparator(), l);
            for (LexicalEntry le : closureData.get(l).keySet()) {
                output = String.format("%s%s\t%s", output, System.lineSeparator(), le);
                for (LexicalSense ls : closureData.get(l).get(le)) {
                    output = String.format("%s%s\t\t%s", output, System.lineSeparator(), ls);
                }
            }
        }
        return output;
    }
}
