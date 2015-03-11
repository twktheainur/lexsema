package org.getalp.lexsema.acceptali.crosslingual.translation;

import org.getalp.lexsema.language.Language;

public interface Translator {

    public String translate(String source, Language sourceLanguage, Language targetLanguage);

    public void close();

}
