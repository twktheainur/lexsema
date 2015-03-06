package org.getalp.lexsema.acceptali.cli.org.getalp.lexsema.acceptali.acceptions;


import cern.colt.matrix.tdouble.DoubleMatrix2D;
import com.trickl.cluster.ClusterAlgorithm;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.ml.matrix.filters.Filter;

import java.util.ArrayList;
import java.util.List;

public class SenseClustererImpl implements SenseClusterer {
    private ClusterAlgorithm clusterAlgorithm;
    private Filter kernelFilter;

    public SenseClustererImpl(ClusterAlgorithm clusterAlgorithm) {
        this.clusterAlgorithm = clusterAlgorithm;
    }

    public List<SenseCluster> cluster(DoubleMatrix2D data, int numClusters, List<Sense> senses){
        if(kernelFilter!=null) {
            clusterAlgorithm.cluster(kernelFilter.apply(data), numClusters);
        } else {
            clusterAlgorithm.cluster(data, numClusters);
        }
        DoubleMatrix2D assignment = clusterAlgorithm.getPartition();
        return  extractClusters(assignment,senses, data);
    }

    private List<SenseCluster> extractClusters(DoubleMatrix2D assignments, List<Sense> senses, DoubleMatrix2D data){
        List<SenseCluster> clusters = new ArrayList<>();

        for(int col=0;col<assignments.columns();col++){
                clusters.add(new SenseClusterImpl(String.valueOf(col)));
        }
        for(int row=0;row<assignments.rows();row++){
            for(int col=0;col<assignments.columns();col++){
                if(assignments.getQuick(row,col)>0){
                    clusters.get(col).addMember(senses.get(col),data.viewRow(row),assignments.getQuick(row,col));
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
        this.kernelFilter = filter;
    }
}
