package org.getalp.lexsema.wsd.experiments.cuckoo.wsd;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolutionFactory;
import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolution;

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
