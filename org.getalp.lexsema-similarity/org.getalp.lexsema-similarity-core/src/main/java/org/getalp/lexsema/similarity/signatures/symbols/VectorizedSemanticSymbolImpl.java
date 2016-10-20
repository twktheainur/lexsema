package org.getalp.lexsema.similarity.signatures.symbols;

import java.util.Arrays;

public class VectorizedSemanticSymbolImpl implements VectorizedSemanticSymbol {
    private final double[] vector;
    
    private double weight = 1;
    
    VectorizedSemanticSymbolImpl(double[] vector, double weight)
    {
        this.vector = vector;
        this.weight = weight;
    }
    
    @Override
    public int compareTo(SemanticSymbol o)
    {
        return 0;
    }

    @Override
    public String getSymbol()
    {
        return Arrays.toString(vector);
    }

    @Override
    public double getWeight()
    {
        return weight;
    }
    
    @Override
    public double[] getVector()
    {
        return vector;
    }

}
