package org.getalp.lexsema.axalign.graph.writer;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.jgrapht.ext.VertexNameProvider;

public class LexicalEntryIdProvider implements VertexNameProvider<LexicalEntry>{

    @SuppressWarnings("FeatureEnvy,LawOfDemeter")
    @Override
    public String getVertexName(LexicalEntry vertex){
        return String.format("\"%s_%d_%s\\n%s\"", vertex.getLemma(), vertex.getNumber(), vertex.getPartOfSpeech().split("#")[1], vertex.getLanguage().getISO2Code());
    }
}
