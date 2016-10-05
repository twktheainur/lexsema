package org.getalp.lexsema.examples.points;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.lexsema.ml.matrix.filters.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by boucherj on 04/02/16.
 */
public class PartialPointClusterer implements PointClusterer {
    Filter filter;

    private static final Logger logger= LoggerFactory.getLogger(PartialPointClusterer.class);

    @Override
    public void setKernelFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public List<PointCluster> cluster(DoubleMatrix2D data, int numClusters, List<Point> senses) {

        List<PointCluster> clusters = new ArrayList<>();
        data = filter.apply(data);
        return clusters;
    }

}
