package org.getalp.lexsema.axalign.cli.org.getalp.lexsema.acceptali.acceptions;


import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.lexsema.axalign.closure.similarity.PairwiseSimilarityMatrixGenerator;
import org.getalp.lexsema.axalign.closure.similarity.PairwiseSimilarityMatrixGeneratorSim;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.ml.matrix.filters.Filter;
import org.getalp.lexsema.ml.vector.Vectors;

import java.util.Set;
import java.util.TreeSet;

public class SimilaritySenseClusterCombFilter implements SenseClusterCombFilter {

    private final SimilarityMeasure similarityMeasure;
    private final Filter filter;

    public SimilaritySenseClusterCombFilter(SimilarityMeasure similarityMeasure, Filter normFilter) {
        this.similarityMeasure = similarityMeasure;
        this.filter = normFilter;
    }

    @Override
    public SenseCluster apply(SenseCluster senseCluster) {
        Set<Sense> senses = new TreeSet<>();
        for(Sense sense: senseCluster){
            senses.add(sense);
        }

        PairwiseSimilarityMatrixGenerator matrixGenerator = new PairwiseSimilarityMatrixGeneratorSim(similarityMeasure,senses,SimilaritySenseClusterCombFilter.class.getName());
        matrixGenerator.generateMatrix();
        DoubleMatrix2D scores = matrixGenerator.getScoreMatrix();
        scores = filter.apply(scores);

        DoubleMatrix1D permutation = Vectors.permutation(scores.viewColumn(0));
        
        return null;
    }
}
