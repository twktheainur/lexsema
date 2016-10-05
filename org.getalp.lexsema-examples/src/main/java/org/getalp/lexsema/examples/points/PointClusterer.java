package org.getalp.lexsema.examples.points;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.lexsema.ml.matrix.filters.Filter;

import java.awt.*;
import java.util.List;

/**
 * Created by boucherj on 04/02/16.
 */
public interface PointClusterer {
    public void setKernelFilter(Filter filter);
    public List<PointCluster> cluster(DoubleMatrix2D data, int numClusters, List<Point> senses);

}
