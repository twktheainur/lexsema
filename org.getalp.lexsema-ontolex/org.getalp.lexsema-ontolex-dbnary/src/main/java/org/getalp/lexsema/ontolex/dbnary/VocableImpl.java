package org.getalp.lexsema.ontolex.dbnary;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.getalp.lexsema.ontolex.AbstractLexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;

/**
 * A Dbnary Vocable entry handler
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class VocableImpl extends AbstractLexicalResourceEntity implements Vocable {

    private String vocable;

    public VocableImpl(LexicalResource r, String uri, LexicalResourceEntity parent, String vocable) {
        super(r, uri, parent);
        getOntologyModel();
        this.vocable = vocable;
    }
}
