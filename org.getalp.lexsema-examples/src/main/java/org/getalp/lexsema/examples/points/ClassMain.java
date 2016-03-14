package org.getalp.lexsema.examples.points;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import com.trickl.cluster.KMeans;
import org.getalp.lexsema.acceptali.cli.org.getalp.lexsema.acceptali.acceptions.SenseCluster;
import org.getalp.lexsema.acceptali.cli.org.getalp.lexsema.acceptali.acceptions.SenseClusterer;
import org.getalp.lexsema.acceptali.cli.org.getalp.lexsema.acceptali.acceptions.TricklSenseClusterer;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.crosslingual.TranslatorCrossLingualSimilarity;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.translation.GoogleWebTranslator;
import org.getalp.lexsema.translation.Translator;
import org.getalp.lexsema.util.Language;
import org.getalp.ml.matrix.filters.Filter;
import org.getalp.ml.matrix.filters.NMFKLMatrixFactorizationFilter;
import org.getalp.ml.matrix.filters.normalization.ZSignificanceNormalizationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

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
/*        //matrice 2
        DoubleMatrix2D inputData = new DenseDoubleMatrix2D(8,2);
        inputData.assign(1);
        inputData.set(0,1,2);
        inputData.set(2,0,2);
        inputData.set(2,1,2);
        inputData.set(3,0,2);
        inputData.set(4,0,4);
        inputData.set(4,1,5);
        inputData.set(5,0,4);
        inputData.set(5,1,4);
        inputData.set(6,0,5);
        inputData.set(6,1,5);
        inputData.set(7,0,5);
        inputData.set(7,1,4);
        int nbCluster=2;
*/
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

  //      KMeans kMeans=new KMeans();
        Filter normalizationFilter = new ZSignificanceNormalizationFilter();
    //    normalizationFilter.apply(inputData);
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

        assignment = kMeans.getMeans();
        System.out.println("Means");
        for(int i= 0;i<assignment.rows();i++){
            for(int j= 0;j<assignment.columns();j++){
                System.out.print(assignment.get(i,j)*poidsTotal+" ");
            }
            System.out.println();
        }


/*        List<PointCluster> clusters = clusterer.cluster(inputData, nbCluster, new ArrayList<>(points));

        for (PointCluster sc : clusters) {
            System.out.println(sc.toString());
        }*/

/*
        int nbCluster=3;
        kMeans.cluster(inputData,nbCluster);

        System.out.println("Partition");
        DoubleMatrix2D assignment=kMeans.getPartition();

        for(int i= 0;i<assignment.rows();i++){
            for(int j= 0;j<assignment.columns();j++){
                System.out.print(assignment.get(i,j)+" ");
            }
            System.out.println();
        }
*/



    }

    public static DoubleMatrix2D recuperationData(String fichier) {
        DoubleMatrix2D matrix = new DenseDoubleMatrix2D(5, 2);

        String chaine = "";
        //lecture du fichier texte
        try {
            InputStream ips = new FileInputStream(fichier);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String ligne;
            ligne = br.readLine();

            while ((ligne = br.readLine()) != null) {

                System.out.println("" + ligne);
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return matrix;
    }

}
