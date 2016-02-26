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

    private static double[] term_to_term_product_squared(double[] a, double[] b)
    {
        double[] ret = new double[a.length];
        for (int i = 0 ; i < ret.length ; i++)
        {
            int sign = a[i] * b[i] < 0 ? -1 : 1;
            ret[i] = sign * Math.sqrt(Math.abs(a[i] * b[i]));
        }
        return ret;
    }

    private static double[] term_to_term_product(double[] a, double[] b)
    {
        double[] ret = new double[a.length];
        for (int i = 0 ; i < ret.length ; i++)
        {
            ret[i] = a[i] * b[i];
        }
        return ret;
    }
    
    private static double[] weak_contextualization(double[] a, double[] b)
    {
        double[] ret = new double[a.length];
        for (int i = 0 ; i < ret.length ; i++)
        {
            int sign = a[i] * b[i] < 0 ? -1 : 1;
            ret[i] = a[i] + b[i] + sign * Math.sqrt(Math.abs(a[i] * b[i]));
        }
        return ret;
    }
    
    public static double absolute_synonymy(double[] a, double[] b)
    {
        double[] c = term_to_term_product(a, b);
        double[] ac = term_to_term_product(a, c);
        double[] aac = VectorOperation.normalize(VectorOperation.add(a, ac));
        double[] bc = term_to_term_product(b, c);
        double[] bbc = VectorOperation.normalize(VectorOperation.add(b, bc));
        return VectorOperation.dot_product(aac, bbc);
    }

    public static double[] to_vector(String string)
    {
        if (string == null) return null;
        String[] strValues = string.trim().replace("[", "").replace("]", "").split(", ");
        double[] ret = new double[strValues.length];
        for (int i = 0 ; i < ret.length ; i++) ret[i] = Double.parseDouble(strValues[i]);
        return ret;
    }
}
