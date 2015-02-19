package org.getalp.lexsema.acceptali;

import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface LexicalResourceTranslationClosure {
    public void addSenses(Language language, LexicalEntry lexicalEntry, Collection<LexicalSense> senses);

    public void importClosure(LexicalResourceTranslationClosure closure);

    public Map<Language, Map<LexicalEntry, Set<LexicalSense>>> senseClosureByLanguageAndEntry();

    public Map<LexicalEntry, Set<LexicalSense>> senseClosureByEntry();

    public Map<Language, Set<LexicalEntry>> entryClosureByLanguage();

    public Set<LexicalEntry> entryFlatClosure();

    public Set<LexicalSense> senseFlatClosure();
}
