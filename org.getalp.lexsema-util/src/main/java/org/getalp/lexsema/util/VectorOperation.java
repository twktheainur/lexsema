package org.getalp.lexsema.util;

public class VectorOperation
{
    public static double[] sub(double[] a, double[] b)
    {
        double[] ret = new double[a.length];
        for (int i = 0 ; i < a.length ; i++)
        {
            ret[i] = a[i] - b[i];
        }
        return ret;
    }

    public static double[] add(double[] a, double[] b)
    {
        double[] ret = new double[a.length];
        for (int i = 0 ; i < a.length ; i++)
        {
            ret[i] = a[i] + b[i];
        }
        return ret;
    }
    
    public static double[] sum(double[]... vectors)
    {
        double[] ret = new double[vectors[0].length];
        for (int i = 0 ; i < ret.length ; i++) 
        {
            ret[i] = 0;
            for (int j = 0 ; j < vectors.length ; j++) 
            {
                ret[i] += vectors[j][i];
            }
        }
        return ret;
    }
    
    public static double norm(double[] v) 
    {
        double ret = 0;
        for (int i = 0 ; i < v.length ; i++) 
        {
            ret += v[i] * v[i];
        }
        return Math.sqrt(ret);
    }
    
    public static double[] normalize(double[] v) 
    {
        double[] ret = new double[v.length];
        double norm = norm(v);
        for (int i = 0 ; i < v.length ; i++) 
        {
            ret[i] = v[i] / norm;
        }
        return ret;
    }

    public static double dot_product(double[] a, double[] b) 
    {
        double ret = 0;
        for (int i = 0 ; i < a.length ; i++) 
        {
            ret += a[i] * b[i];
        }
        return ret;
    }
    
}
