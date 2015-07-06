package org.getalp.lexsema.examples.datathon.multlex;

import org.getalp.lexsema.examples.datathon.multlex.lexicalization.AbstractMultilingualLexicalizationGenerator;
import org.getalp.lexsema.examples.datathon.multlex.lexicalization.LexicalizationAlternatives;
import org.getalp.lexsema.translation.Translator;
import org.getalp.lexsema.util.Language;


public class GettyArtistTypeMultilingualLexicalizationGenerator extends AbstractMultilingualLexicalizationGenerator {

    LexicalizationAlternatives lexicalizationAlternatives;

    public GettyArtistTypeMultilingualLexicalizationGenerator(LexicalizationAlternatives lexicalizationAlternatives) {
        super();
        this.lexicalizationAlternatives = lexicalizationAlternatives;
        for(Language language: Language.values()){
            registerTargetLanguage(language);
        }
    }

    @Override
    public LexicalizationAlternatives computeLexicalizations(String writtenForm) {
        if(lexicalizationAlternatives.size()==1) {
            for(Translator translator: this.translatorIterable()){
                for(Language language: this.languageIterable()){
                    String translation = translator.translate(writtenForm,Language.ENGLISH,language);
                    if(translation!=null) {
                        translation = translation.trim();
                        if (!translation.isEmpty()) {
                            lexicalizationAlternatives.registerLexicalization(language, translation);
                        }
                    }
                }
            }
        }
        return lexicalizationAlternatives;
    }
}
