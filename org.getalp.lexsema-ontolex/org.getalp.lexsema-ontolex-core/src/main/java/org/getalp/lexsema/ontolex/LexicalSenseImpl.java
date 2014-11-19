package org.getalp.lexsema.ontolex;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class representing a ontolex LexicalSense entity
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LexicalSenseImpl extends AbstractLexicalResourceEntity implements LexicalSense {

    private String definition;
    private String senseNumber;

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
    }

    @Override
    public String getDefinition() {
        return definition;
    }

    @Override
    public String toString() {
        return "LexicalSenseImpl{" +
                "definition='" + definition + '\'' +
                ", senseNumber=" + senseNumber +
                '}';
    }


}
