package org.getalp.lexsema.language;

import java.io.Serializable;

@SuppressWarnings("PublicMethodNotExposedInInterface")
public enum Language implements Cloneable, Serializable {

    ENGLISH("eng", "en", "English"),
    FRENCH("fra", "fr", "French"),
    GERMAN("deu", "de", "German"),
    ITALIAN("ita", "it", "Italian"),
    JAPANESE("jap", "jp", "Japanese"),
    SPANISH("spa", "es", "Spanish"),
    PORTUGUESE("por", "pt", "Portuguese"),
    FINNISH("fin", "fi", "Finnish"),
    BULGARIAN("bul", "bg", "Bulgarian"),
    CATALAN("cat", "ca", "Catalan"),
    RUSSIAN("rus", "ru", "Russian"),
    GREEK("ell", "el", "Modern Greek"),
    TURKISH("tur", "tr", "Turkish"),
    UNSUPPORTED("", "", "");

    private String iso3Code;
    private String iso2Code;
    private String languageName;


    Language(String iso3Code, String iso2Code, String languageName) {
        this.iso3Code = iso3Code;
        this.iso2Code = iso2Code;
        this.languageName = languageName;
    }

    public static Language fromCode(String code) {
        try {
            for (Language l : Language.values()) {
                if (code.equals(l.getISO2Code()) || code.equals(l.getISO3Code()) || code.equals(l.getLanguageName())) {
                    return l;
                }
            }
        } catch (NullPointerException ignored) {
        }
        return Language.UNSUPPORTED;
    }

    public String getISO3Code() {
        return iso3Code;
    }

    public String getISO2Code() {
        return iso2Code;
    }

    public String getLanguageName() {
        return languageName;
    }
}
