package org.getalp.lexsema.acceptali.closure;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.util.Language;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface LexicalResourceTranslationClosure<T extends LexicalSense> {
    public void addSenses(Language language, LexicalEntry lexicalEntry, Collection<T> senses);

    public void addSense(Language language, LexicalEntry lexicalEntry, T sense);

    public void importClosure(LexicalResourceTranslationClosure<T> closure);

    public Map<Language, Map<LexicalEntry, Set<T>>> senseClosureByLanguageAndEntry();

    public Map<LexicalEntry, Set<T>> senseClosureByEntry();

    public Map<Language, Set<LexicalEntry>> entryClosureByLanguage();

    public Set<LexicalEntry> entryFlatClosure();

    public Set<T> senseFlatClosure();

}
