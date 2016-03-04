package org.getalp.lexsema.similarity.signatures.symbols;

import java.util.Arrays;

public class VectorizedSemanticSymbol implements SemanticSymbol 
{
    private double[] vector;
    
    private double weight = 1;
    
    public VectorizedSemanticSymbol(double[] vector, double weight)
    {
        this.vector = vector;
        this.weight = weight;
    }
    
    public int compareTo(SemanticSymbol o)
    {
        return 0;
    }

    public String getSymbol()
    {
        return Arrays.toString(vector);
    }

    @Override
    public double getWeight()
    {
        return weight;
    }
    
    public double[] getVector()
    {
        return vector;
    }

}
