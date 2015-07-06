package org.getalp.lexsema.examples.datathon.multlex.lexicalization;

import org.getalp.lexsema.translation.Translator;
import org.getalp.lexsema.util.Language;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractMultilingualLexicalizationGenerator implements MultilingualLexicalizationGenerator{

    private List<Translator> translators = new ArrayList<>();
    private List<Language> languages = new ArrayList<>();

    @Override
    public void registerTranslator(Translator translator) {
        translators.add(translator);
    }

    @Override
    public void registerTargetLanguage(Language language){
        languages.add(language);
    }

    protected Iterable<Translator> translatorIterable(){
        return  translators;
    }

    protected Iterable<Language> languageIterable(){
        return  languages;
    }
}
