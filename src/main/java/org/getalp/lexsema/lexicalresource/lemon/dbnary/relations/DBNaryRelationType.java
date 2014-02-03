package org.getalp.lexsema.lexicalresource.lemon.dbnary.relations;

import org.getalp.lexsema.lexicalresource.LexicalResourceEntity;
import org.getalp.lexsema.ontology.graph.RelationType;

import java.util.HashMap;
import java.util.Map;

public enum DBNaryRelationType implements RelationType {
    isTranslationOf,hypernym, hyponym, meronym, holonym, antonym, synonym, allnym("^.*nym$") ;

    Map<DBNaryRelationType,DBNaryRelationType> inverses;
    Class<? extends LexicalResourceEntity> correspondingType;
    String filter;

    public String getFilter() {
        return filter;
    }

    DBNaryRelationType() {
        inverses = new HashMap<>();
        initInverses();
    }

    DBNaryRelationType(String filter) {
        inverses = new HashMap<>();
        initInverses();
        this.filter = filter;
    }

    private final void initInverses(){
        inverses.put(DBNaryRelationType.hypernym,DBNaryRelationType.hyponym);
        inverses.put(DBNaryRelationType.hyponym,DBNaryRelationType.hypernym);
        inverses.put(DBNaryRelationType.synonym,DBNaryRelationType.antonym);
        inverses.put(DBNaryRelationType.antonym,DBNaryRelationType.synonym);
        inverses.put(DBNaryRelationType.meronym,DBNaryRelationType.holonym);
        inverses.put(DBNaryRelationType.holonym,DBNaryRelationType.meronym);
        inverses.put(DBNaryRelationType.isTranslationOf,DBNaryRelationType.isTranslationOf);
        inverses.put(DBNaryRelationType.allnym, DBNaryRelationType.allnym);
    }

    @Override
    public RelationType getType() {
        return this;
    }

    @Override
    public RelationType getInverseType() {
        return inverses.get(this);
    }
}
