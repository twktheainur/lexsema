package org.getalp.lexsema.acceptali.cli.org.getalp.lexsema.acceptali.acceptions;


import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.ml.matrix.filters.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PartialSenseClusterer implements SenseClusterer {
    Filter filter;

    private static final Logger logger= LoggerFactory.getLogger(PartialSenseClusterer.class);

    @Override
    public void setKernelFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public List<SenseCluster> cluster(DoubleMatrix2D data, int numClusters, List<Sense> senses) {

        List<SenseCluster> clusters = new ArrayList<>();
        data = filter.apply(data);
        return clusters;
    }

}
