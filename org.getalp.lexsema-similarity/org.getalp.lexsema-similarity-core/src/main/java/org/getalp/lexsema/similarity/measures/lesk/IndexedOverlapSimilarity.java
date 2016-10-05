package org.getalp.lexsema.similarity.measures.lesk;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;

import java.util.List;
import java.util.Map;

/**
 * An overlap measure based on sorted and indexed definition bags of words. This is the lesk implementation from the formica
 * project.
 */
public class IndexedOverlapSimilarity implements SimilarityMeasure {


    private boolean normalize;

    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB) {
        return compute(sigA,sigB,null,null);
    }


    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB, Map<String, SemanticSignature> relatedSignaturesA, Map<String, SemanticSignature> relatedSignaturesB) {

        List<String> la = sigA.getStringSymbols();
        List<String> lb = sigB.getStringSymbols();

//        if(sigA instanceof IndexedSemanticSignature && sigB instanceof  IndexedSemanticSignature){
//            la = ((IndexedSemanticSignature) sigA).getIndexedSymbols();
//            lb = ((IndexedSemanticSignature) sigA).getIndexedSymbols();
//        } else {
//            la = new ArrayList<>();
//            lb = new ArrayList<>();
//            try{
//                for(String symbolA: sigA.getStringSymbols()){
//                    la.add(Integer.valueOf(symbolA));
//                }
//                for(String symbolB: sigB.getStringSymbols()){
//                    la.add(Integer.valueOf(symbolB));
//                }
//            } catch (NumberFormatException ignored){
//            }
//        }

        int aSize = la.size();
        int bSize = lb.size();
        int count = 0;
        int i = 0;
        int j = 0;
        while (i < aSize && j < bSize) {
            if (la.get(i).compareTo(lb.get(j)) == 0 && !lb.get(j).equals(-1)) {
                count++;
                i++;
                j++;
            } else if (la.get(i).compareTo(lb.get(j)) == -1) {
                i++;
            } else {
                j++;
            }
        }
        if (normalize) {
            return (double) count / Math.min(aSize, bSize);
        }
        return count;
    }

    private SemanticSymbol getSignatureSymbolAt(int index, IndexedSemanticSignature signature) {
        return signature.getSymbol(index);
    }

    @SuppressWarnings({"MethodReturnOfConcreteClass", "BooleanParameter", "PublicMethodNotExposedInInterface"})
    public IndexedOverlapSimilarity normalize(boolean value) {
        normalize = value;
        return this;
    }
}
