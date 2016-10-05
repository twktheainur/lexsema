package org.getalp.lexsema.axalign.closure;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.util.Language;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface LexicalResourceTranslationClosure<T extends LexicalSense> {
    void addSenses(Language language, LexicalEntry lexicalEntry, Collection<T> senses);

    void addSense(Language language, LexicalEntry lexicalEntry, T sense);

    void importClosure(LexicalResourceTranslationClosure<T> closure);

    Map<Language, Map<LexicalEntry, Set<T>>> senseClosureByLanguageAndEntry();

    Map<LexicalEntry, Set<T>> senseClosureByEntry();

    Map<Language, Set<LexicalEntry>> entryClosureByLanguage();

    Set<LexicalEntry> entryFlatClosure();

    Set<T> senseFlatClosure();

}
