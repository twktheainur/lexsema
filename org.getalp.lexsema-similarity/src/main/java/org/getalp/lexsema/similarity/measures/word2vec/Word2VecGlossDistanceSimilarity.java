package org.getalp.lexsema.similarity.measures.word2vec;


import com.wcohen.ss.ScaledLevenstein;
import com.wcohen.ss.api.StringDistance;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.util.dataitems.Pair;
import org.getalp.lexsema.util.dataitems.PairImpl;
import org.getalp.ml.matrix.distance.Distance;
import org.getalp.ml.matrix.filters.Filter;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Word2VecGlossDistanceSimilarity implements SimilarityMeasure {


    private static final Logger logger = LoggerFactory.getLogger(Word2VecGlossDistanceSimilarity.class);
    private final Word2Vec word2Vec;
    private final Distance distance;
    private final Filter filter;

    private final StringDistance levenshtein = new ScaledLevenstein();
    private static final double THRESHOLD = .8;

    public Word2VecGlossDistanceSimilarity(Word2Vec word2Vec, Distance distance, Filter factorizationFilter) {
        this.word2Vec = word2Vec;
        this.distance = distance;
        filter = factorizationFilter;
    }

    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB, Map<String, SemanticSignature> relatedSignaturesA, Map<String, SemanticSignature> relatedSignaturesB) {
        return computePairDistance(
                generateComparableSignatureMatrices(sigA, sigB));
    }

    private double computePairDistance(Pair<INDArray, INDArray> sigs) {
        INDArray first = sigs.first();
        INDArray second = sigs.second();
        if(filter!=null){
            first = filter.apply(first);
            second = filter.apply(second);
        }

        double dist = 1 - distance.compute(first, second);
        //if(logger.isDebugEnabled()){
            logger.info(String.format("Distance computed=%s", dist));
        //}
        return dist;
    }


    private Pair<INDArray, INDArray> generateComparableSignatureMatrices(SemanticSignature semanticSignatureA, SemanticSignature semanticSignatureB) {

        /**
         * Retrieving and sorting semantic signatures
         */
        List<String> alignedA = sortedSignature(semanticSignatureA);
        List<String> alignedB = sortedSignature(semanticSignatureB);

        int sizeA = alignedA.size();
        int sizeB = alignedB.size();

        int i = 0, j = 0;
        int currentTargetIndex = 0;

        /**
         * Calculating number of dimensions of the model (columns)
         */
        int dimensions = numberOfColumns();

        INDArray sigA = Nd4j.create(sizeA + sizeB, dimensions);
        INDArray sigB = Nd4j.create(sizeA + sizeB, dimensions);

        while (i < sizeA && j < sizeB && currentTargetIndex < sizeA + sizeB) {
            String symbolA = alignedA.get(i);
            String symbolB = alignedB.get(j);
            double editDistance = levenshtein.score(symbolA, symbolB);
            if (editDistance > THRESHOLD) {
                sigA.putRow(currentTargetIndex,
                        getWord2ecVector(symbolA, dimensions));
                sigB.putRow(currentTargetIndex,
                        getWord2ecVector(symbolB, dimensions));
                i++;
                j++;
            } else if (symbolA.compareTo(symbolB) > 0) {
                sigA.putRow(currentTargetIndex, Nd4j.zeros(1, dimensions));
                sigB.putRow(currentTargetIndex,
                        getWord2ecVector(symbolB, dimensions));
                j++;

            } else if (symbolA.compareTo(symbolB) < 0) {
                sigB.putRow(currentTargetIndex, Nd4j.zeros(1, dimensions));
                sigA.putRow(currentTargetIndex,
                        getWord2ecVector(symbolA, dimensions));
                i++;
            }
            currentTargetIndex++;
        }
        return new PairImpl<>(sigA, sigB);
    }

    private List<String> sortedSignature(SemanticSignature semanticSignature) {
        List<String> symbols = semanticSignature.getSymbols();
        Collections.sort(symbols);
        return Collections.unmodifiableList(symbols);
    }

    private int numberOfColumns() {
        WeightLookupTable weightLookupTable = word2Vec.lookupTable();
        Iterator<INDArray> vectors = weightLookupTable.vectors();
        INDArray next = vectors.next();
        return next.columns();
    }

    private INDArray getWord2ecVector(String symbol, int dim) {
        INDArray array;
        if (word2Vec.hasWord(symbol)) {
            array = word2Vec.getWordVectorMatrixNormalized(symbol);
        } else {
            array = Nd4j.zeros(dim);
        }
        return array;
    }


    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB) {
        return compute(sigA, sigB, null, null);
    }


}
