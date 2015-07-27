package org.getalp.lexsema.ontolex;

import org.getalp.lexsema.util.Language;

/**
 * Class representing a ontolex LexicalSense entity
 */

public class LexicalSenseImpl extends AbstractLexicalResourceEntity implements LexicalSense {

    private String definition;
    private String senseNumber;
    private Language language;

    /**
     * Constructor for LexicalSense
     *
     * @param r   the ontolex lexical resource
     * @param uri the uri of the LexicalSense
     */
    public LexicalSenseImpl(LexicalResource r, String uri, LexicalResourceEntity parent, String senseNumber) {
        super(r, uri, parent);
        this.senseNumber = senseNumber;
        //fetchDefinition();
        language = r.getLanguage();
        definition = "";
    }

    @Override
    public String getDefinition() {
        return definition;
    }

    @Override
    public String toString() {
        String[] uriParts = getNode().getURI().split("/");
        String id = uriParts[uriParts.length - 1];
        return String.format("LexicalSense|%s|{'%s'}", id, definition);
    }

    @Override
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String getSenseNumber() {
        return senseNumber;
    }

    @Override
    public void setSenseNumber(String senseNumber) {
        this.senseNumber = senseNumber;
    }

    @Override
    public boolean isNull() {
        return false;
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
