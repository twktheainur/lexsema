/**
 *
 */
package org.getalp.lexsema.ontology.lemon;

import org.getalp.lexsema.lexicalresource.LexicalResource;
import org.getalp.lexsema.ontology.graph.defaultimpl.DefaultNode;

/**
 * @author tchechem
 */
public class LexicalEntry extends DefaultNode {


    private LexicalResource lexicalResource;
    private String lemma;
    private String partOfSpeech;
    private Integer number;

    {
        number = -1;
    }

    public LexicalEntry(LexicalResource r, String uri) {
        super(r.getURI() + uri, r);
        lexicalResource = r;
    }


}
