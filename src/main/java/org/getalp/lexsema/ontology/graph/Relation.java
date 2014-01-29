/**
 *
 */
package org.getalp.lexsema.ontology.graph;

import org.getalp.lexsema.lexicalresource.LexicalResourceEntity;

import java.util.List;

/**
 * @author tchechem
 */
public class Relation {
    private LexicalResourceEntity start;
    private LexicalResourceEntity end;

    public static List<Relation> findRelationsForSource(Node n) {
        return findRelationsForSource(n, null);
    }

    public static List<Relation> findRelationsForTarget(Node n) {

        return findRelationsForTarget(n, null);
    }

    public static List<Relation> findRelationsForSource(Node n, RelationType type) {
        return null;
    }


    public static List<Relation> findRelationsForTarget(Node n, RelationType type) {

        return null;
    }

}
