package org.getalp.lexsema.ontolex.babelnet.relations;


import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.RelationType;

import java.util.HashMap;
import java.util.Map;

public enum LexinfoRelationType implements RelationType {
    hypernym, hyponym, meronym, holonym, antonym, synonym, allnym("^.*nym$");

    Map<RelationType, RelationType> inverses;
    Class<? extends LexicalResourceEntity> correspondingType;
    String filter;

    LexinfoRelationType() {
        inverses = new HashMap<>();
        initInverses();
    }

    LexinfoRelationType(String filter) {
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

    @Override
    public String getURI() {
        return getPrefix() + ":" + getType().toString();
    }

    private void initInverses() {
        inverses.put(LexinfoRelationType.hypernym, LexinfoRelationType.hyponym);
        inverses.put(LexinfoRelationType.hyponym, LexinfoRelationType.hypernym);
        inverses.put(LexinfoRelationType.synonym, LexinfoRelationType.antonym);
        inverses.put(LexinfoRelationType.antonym, LexinfoRelationType.synonym);
        inverses.put(LexinfoRelationType.meronym, LexinfoRelationType.holonym);
        inverses.put(LexinfoRelationType.holonym, LexinfoRelationType.meronym);
        inverses.put(LexinfoRelationType.allnym, LexinfoRelationType.allnym);
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
