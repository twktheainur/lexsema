package org.getalp.lexsema.translation;


import org.getalp.lexsema.util.Language;

import java.io.Serializable;

public interface Translator extends Serializable{

    String translate(String source, Language sourceLanguage, Language targetLanguage);

    void close();

}
