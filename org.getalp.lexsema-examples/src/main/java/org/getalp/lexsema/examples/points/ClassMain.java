package org.getalp.lexsema.examples.points;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import com.trickl.cluster.KMeans;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.crosslingual.TranslatorCrossLingualSimilarity;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Created by boucherj on 04/02/16.
 */
public class ClassMain {

    public static void main(String... args) throws FileNotFoundException {




 //       String fichier ="/home/boucherj/Stage/lexsema/org.getalp.lexsema-examples/src/main/java/org/getalp/lexsema/exampleBoucherj/data.txt";
 //      DoubleMatrix2D inputData =recuperationData(fichier);


        //matrice 1
        DoubleMatrix2D inputData = new DenseDoubleMatrix2D(5,2);
        inputData.assign(1);
        inputData.set(1,1,4);
        inputData.set(3,0,2);
        inputData.set(0,0,3);
        inputData.set(4,1,2);
        int nbCluster=4;

        System.out.println("Matrice");
        for(int i= 0;i<inputData.rows();i++){
            for(int j= 0;j<inputData.columns();j++){
                System.out.print(inputData.get(i,j)+" ");
            }
            System.out.println();
        }

        ArrayList<Point> points = new ArrayList<>();
        for(int i= 0;i<inputData.rows();i++){
            points.add(new Point((int)inputData.get(i,0),(int)inputData.get(i,1)));
        }

        PointClusterer clusterer = new TricklPointClusterer(new KMeans());

        inputData.normalize();
        System.out.println("Matrice a normaliser");
        for(int i= 0;i<inputData.rows();i++){
            for(int j= 0;j<inputData.columns();j++){
                System.out.print(inputData.get(i,j)+" ");
            }
            System.out.println();
        }



        KMeans kMeans=new KMeans();
        kMeans.cluster(inputData, nbCluster);
        DoubleMatrix2D assignment = kMeans.getPartition();

        System.out.println("Partition");
        for(int i= 0;i<assignment.rows();i++){
            for(int j= 0;j<assignment.columns();j++){
                System.out.print(assignment.get(i,j)+" ");
            }
            System.out.println();
        }
        int poidsTotal=0;
        for(int i= 0;i<points.size();i++){
            poidsTotal+=points.get(i).x+points.get(i).y;
        }
        System.out.println("PoidsTotal : "+poidsTotal);

        //assignment = kMeans.getMeans();
        System.out.println("********Clusters********");
        for(int i= 0;i<assignment.columns();i++){
            System.out.println("Cluster " + i + ":");
            for(int j= 0;j<assignment.rows();j++){
                double assig = assignment.get(j,i);
                if(assig > 0) {
                    System.out.print("\t Point " + j);
                }
            }
            System.out.println();
        }
    }


}
