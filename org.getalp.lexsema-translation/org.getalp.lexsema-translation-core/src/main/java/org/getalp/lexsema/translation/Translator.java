package org.getalp.lexsema.translation;


import org.getalp.lexsema.util.Language;

public interface Translator {

    public String translate(String source, Language sourceLanguage, Language targetLanguage);

    public void close();

}
