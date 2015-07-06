package org.getalp.lexsema.examples.datathon.multlex.lexicalization;


import org.getalp.lexsema.translation.Translator;
import org.getalp.lexsema.util.Language;

public interface MultilingualLexicalizationGenerator {
    public LexicalizationAlternatives computeLexicalizations(String writtenForm);
    public void registerTranslator(Translator translator);
    public void registerTargetLanguage(Language language);
}
