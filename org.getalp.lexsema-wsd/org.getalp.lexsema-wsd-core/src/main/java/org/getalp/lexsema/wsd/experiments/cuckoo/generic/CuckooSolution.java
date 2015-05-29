package org.getalp.lexsema.wsd.experiments.cuckoo.generic;

public interface CuckooSolution
{
    public void makeRandomChanges(double numberOfChanges);
    
    public CuckooSolution clone();
}
