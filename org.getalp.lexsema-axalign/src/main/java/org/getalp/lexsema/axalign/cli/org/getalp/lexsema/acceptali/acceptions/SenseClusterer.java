package org.getalp.lexsema.axalign.cli.org.getalp.lexsema.acceptali.acceptions;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.ml.matrix.filters.Filter;

import java.util.List;

public interface SenseClusterer {
    public void setKernelFilter(Filter filter);
    public List<SenseCluster> cluster(DoubleMatrix2D data, int numClusters, List<Sense> senses);

}
