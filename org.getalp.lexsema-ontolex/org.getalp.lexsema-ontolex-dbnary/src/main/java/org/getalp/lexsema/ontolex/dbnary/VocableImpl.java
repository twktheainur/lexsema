package org.getalp.lexsema.ontolex.dbnary;


import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.ontolex.AbstractLexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;

/**
 * A Dbnary Vocable entry handler
 */
public class VocableImpl extends AbstractLexicalResourceEntity implements Vocable {

    private String vocable;
    private Language language;

    public VocableImpl(LexicalResource r, String uri, LexicalResourceEntity parent, String vocable) {
        super(r, uri, parent);
        getOntologyModel();
        this.vocable = vocable;
        language = r.getLanguage();
    }

    @Override
    public String getVocable() {
        return vocable;
    }

    @Override
    public void setVocable(String vocable) {
        this.vocable = vocable;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(Language language) {
        this.language = language;
    }
}
