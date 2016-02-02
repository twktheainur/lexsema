package org.getalp.lexsema.ontolex;

import com.hp.hpl.jena.graph.Node;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.util.Language;

public final class NullLexicalEntry implements LexicalEntry{

    @Override
    public String getLemma() {
        return "";
    }

    @Override
    public void setLemma(String lemma) {

    }

    @Override
    public String getPartOfSpeech() {
        return "";
    }

    @Override
    public void setPartOfSpeech(String partOfSpeech) {

    }

    @Override
    public int getNumber() {
        return 0;
    }

    @Override
    public void setNumber(int number) {

    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public LexicalResource getLexicalResource() {
        return null;
    }

    @Override
    public OntologyModel getOntologyModel() {
        return null;
    }

    @Override
    public Node getNode() {
        return null;
    }

    @Override
    public LexicalResourceEntity getParent() {
        return null;
    }

    @Override
    public Language getLanguage() {
        return Language.UNSUPPORTED;
    }

    @Override
    public void setLanguage(Language language) {

    }

    @Override
    public int compareTo(LexicalResourceEntity o) {
        return 0;
    }
}
