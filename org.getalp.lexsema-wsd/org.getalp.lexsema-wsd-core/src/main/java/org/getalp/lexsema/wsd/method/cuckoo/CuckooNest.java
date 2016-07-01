package org.getalp.lexsema.wsd.method.cuckoo;

public interface CuckooNest 
{
	void move(double distance);
	
	CuckooNest clone();
	
	double score();
}
