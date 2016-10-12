package org.getalp.lexsema.ml.optimization.functions.setfunctions.input;

import lombok.Data;

@Data
public class Interval {

    private int start;
    private int end;

    public Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    public int getStart() {
    	return start;
    }
    
    public int getEnd() {
    	return end;
    }
    
    public void setStart(int s) {
    	start = s;
    }
    
    public void setEnd(int e) {
    	end = e;
    }
}
