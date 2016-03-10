package org.getalp.lexsema.examples.points;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import com.trickl.cluster.ClusterAlgorithm;
import org.getalp.ml.matrix.filters.Filter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by boucherj on 04/02/16.
 */
public class TricklPointClusterer implements PointClusterer {
    private final ClusterAlgorithm clusterAlgorithm;
    private Filter kernelFilter;
    private final double threshold;

    public TricklPointClusterer(ClusterAlgorithm clusterAlgorithm) {
        this(clusterAlgorithm, 0);

    }

    public TricklPointClusterer(ClusterAlgorithm clusterAlgorithm, double threshold) {
        this.clusterAlgorithm = clusterAlgorithm;
        this.threshold = threshold;
    }


    public List<PointCluster> cluster(DoubleMatrix2D data, int numClusters, List<Point> senses){
        DoubleMatrix2D filteredData = data;
        if(kernelFilter!=null) {
            filteredData = kernelFilter.apply(data);
        }
        clusterAlgorithm.cluster(filteredData, numClusters);
        DoubleMatrix2D assignment = clusterAlgorithm.getPartition();
        return  extractClusters(assignment,senses, data);
    }

    private List<PointCluster> extractClusters(DoubleMatrix2D assignments, List<Point> senses, DoubleMatrix2D data){
        List<PointCluster> clusters = new ArrayList<>();

        for(int col=0;col<assignments.columns();col++){
            clusters.add(new PointClusterImpl(String.valueOf(col+1)));
        }
        for(int row=0;row<assignments.rows();row++){
            for(int col=0;col<assignments.columns();col++){
                if (assignments.getQuick(row, col) > threshold) {
                    clusters.get(col).addMember(senses.get(row),data.viewRow(row),assignments.getQuick(row,col));
                }
            }
        }
        return clusters;
    }

    @SuppressWarnings("all")
    public void clearFilter(){
        kernelFilter = null;
    }


    public void setKernelFilter(Filter filter){
        kernelFilter = filter;
    }

}
