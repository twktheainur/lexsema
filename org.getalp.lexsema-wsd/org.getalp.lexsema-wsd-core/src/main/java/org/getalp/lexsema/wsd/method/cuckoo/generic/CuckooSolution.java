package org.getalp.lexsema.wsd.method.cuckoo.generic;

public interface CuckooSolution
{
    public void makeRandomChanges(double numberOfChanges);
    
    public CuckooSolution clone();
}
