package org.getalp.lexsema.wsd.experiments.cuckoo.wsd;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolution;

public class CuckooConfiguration extends ContinuousConfiguration implements CuckooSolution
{
    public CuckooConfiguration(Document d)
    {
        super(d);
    }

    public CuckooConfiguration(Document d, int[] senses)
    {
        super(d, senses);
    }

    public void makeRandomChanges(double numberOfChanges)
    {
        makeRandomChanges((int) numberOfChanges);
    }
    
    public CuckooConfiguration clone()
    {
        return new CuckooConfiguration(super.getDocument(), super.getAssignments());
    }
    
    public String toString()
    {
        return "";
    }
}
