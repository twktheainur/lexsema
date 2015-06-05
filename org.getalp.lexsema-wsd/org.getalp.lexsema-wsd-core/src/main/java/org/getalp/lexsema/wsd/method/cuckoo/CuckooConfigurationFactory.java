package org.getalp.lexsema.wsd.method.cuckoo;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolutionFactory;

public class CuckooConfigurationFactory implements CuckooSolutionFactory
{
    Document currentDocument;
    
    public CuckooConfigurationFactory(Document currentDocument)
    {
        this.currentDocument = currentDocument;
    }
    
    public CuckooSolution createRandomSolution()
    {
        return new CuckooConfiguration(currentDocument);
    }

}
