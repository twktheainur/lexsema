package org.getalp.lexsema.ontolex;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.getalp.lexsema.util.Language;

/**
 * Class representing a ontolex LexicalSense entity
 */
@Data
@EqualsAndHashCode(callSuper = false)
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
}
