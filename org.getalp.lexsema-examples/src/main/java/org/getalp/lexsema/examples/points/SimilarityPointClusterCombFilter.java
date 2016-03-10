package org.getalp.lexsema.examples.points;


import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.ml.matrix.filters.Filter;
import org.getalp.ml.vector.Vectors;


import java.awt.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class SimilarityPointClusterCombFilter implements PointClusterCombFilter {

    SimilarityMeasure similarityMeasure;
    Filter filter;

    public SimilarityPointClusterCombFilter(SimilarityMeasure similarityMeasure, Filter normFilter) {
        this.similarityMeasure = similarityMeasure;
        this.filter = normFilter;
    }

    @Override
    public PointCluster apply(PointCluster pointCluster) {
        ArrayList<Point> points = new ArrayList<>();
        //Set<Point> points = new TreeSet<>();
        for(Point point: pointCluster){
            points.add(point);
        }

        PairwiseSimilarityMatrixGenerator matrixGenerator = new PairwiseSimilarityMatrixGeneratorSim(similarityMeasure,points,SimilarityPointClusterCombFilter.class.getName());
        matrixGenerator.generateMatrix();
        DoubleMatrix2D scores = matrixGenerator.getScoreMatrix();
        scores = filter.apply(scores);

        DoubleMatrix1D permutation = Vectors.permutation(scores.viewColumn(0));

        return null;
    }
}