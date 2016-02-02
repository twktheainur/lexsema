package org.getalp.lexsema.ontolex;

import com.hp.hpl.jena.graph.Node;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.util.Language;

public final class NullLexicalSense implements LexicalSense{

    @Override
    public String getDefinition() {
        return "";
    }

    @Override
    public void setDefinition(String definition) {

    }

    @Override
    public String getSenseNumber() {
        return "";
    }

    @Override
    public void setSenseNumber(String senseNumber) {

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
