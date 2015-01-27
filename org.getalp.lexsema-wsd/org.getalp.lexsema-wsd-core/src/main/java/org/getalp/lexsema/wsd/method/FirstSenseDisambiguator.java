package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;

/**
 * Created by tchechem on 22/01/15.
 */
public class FirstSenseDisambiguator implements  Disambiguator{

    @Override
    public Configuration disambiguate(Document document) {
        Configuration c = new Configuration(document);
        for(int i=0;i<document.size();i++){
            c.setSense(i,0);
        }
        return c;
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c) {
        Configuration cret = new Configuration(document);
        for(int i=0;i<document.size();i++){
            if(c.getAssignment(i)==-1) {
                cret.setSense(i, 0);
            } else {
                cret.setSense(i,c.getAssignment(i));
            }
        }
        return cret;
    }

    @Override
    public void release() {

    }
}
