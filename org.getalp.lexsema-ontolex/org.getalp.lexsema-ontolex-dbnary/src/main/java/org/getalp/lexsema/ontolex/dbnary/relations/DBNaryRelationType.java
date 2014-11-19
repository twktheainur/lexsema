package org.getalp.lexsema.ontolex.dbnary.relations;


import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.RelationType;

import java.util.HashMap;
import java.util.Map;

public enum DBNaryRelationType implements RelationType {
    isTranslationOf, hypernym, hyponym, meronym, holonym, antonym, synonym, allnym("^.*nym$");

    Map<RelationType, RelationType> inverses;
    Class<? extends LexicalResourceEntity> correspondingType;
    String filter;

    DBNaryRelationType() {
        inverses = new HashMap<>();
        initInverses();
    }

    DBNaryRelationType(String filter) {
        inverses = new HashMap<>();
        initInverses();
        this.filter = filter;
    }

    @Override
    public String getFilter() {
        return filter;
    }

    @Override
    public String getPrefix() {
        return "dbnary";
    }

    private void initInverses() {
        inverses.put(DBNaryRelationType.hypernym, DBNaryRelationType.hyponym);
        inverses.put(DBNaryRelationType.hyponym, DBNaryRelationType.hypernym);
        inverses.put(DBNaryRelationType.synonym, DBNaryRelationType.antonym);
        inverses.put(DBNaryRelationType.antonym, DBNaryRelationType.synonym);
        inverses.put(DBNaryRelationType.meronym, DBNaryRelationType.holonym);
        inverses.put(DBNaryRelationType.holonym, DBNaryRelationType.meronym);
        inverses.put(DBNaryRelationType.isTranslationOf, DBNaryRelationType.isTranslationOf);
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
