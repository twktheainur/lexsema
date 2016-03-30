package org.getalp.lexsema.acceptali.graph.writer;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.jgrapht.ext.VertexNameProvider;

public class LexicalEntryIdProvider implements VertexNameProvider<LexicalEntry>{
    public String getVertexName(LexicalEntry vertex){
        String id = "\""+vertex.getLemma()+"_"+vertex.getNumber()+"_"+vertex.getPartOfSpeech().split("#")[1]+"\\n"+vertex.getLanguage().getISO2Code()+"\"" ;
        return id ;
    }
}
