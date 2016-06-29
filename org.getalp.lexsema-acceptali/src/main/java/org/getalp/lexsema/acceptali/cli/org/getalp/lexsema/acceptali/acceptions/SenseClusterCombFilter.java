package org.getalp.lexsema.acceptali.cli.org.getalp.lexsema.acceptali.acceptions;

/**
 * Filters clustering results with an post-constraint
 */
public interface SenseClusterCombFilter {
    SenseCluster apply(SenseCluster senseCluster);
}
