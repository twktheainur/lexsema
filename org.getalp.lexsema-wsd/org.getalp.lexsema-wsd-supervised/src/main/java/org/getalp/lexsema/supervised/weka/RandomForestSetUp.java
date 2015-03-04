/**
 *
 */
package org.getalp.lexsema.supervised.weka;

import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;

/**
 * @author tchechem
 */
public class RandomForestSetUp implements WekaClassifierSetUp {

    private int numTree = 100;
    private int maxDepth = 0;
    private int seed = 1;
    private int NumFeatures = 0;

    /* (non-Javadoc)
     * @see org.getalp.lexsema.supervised.weka.WekaClassifierSetUp#setUpClassifier()
     */

    public RandomForestSetUp(int numTree, int maxDepth, int seed, int NumFeatures) {
        super();
        this.numTree = numTree;
        this.maxDepth = maxDepth;
        this.seed = seed;
        this.NumFeatures = NumFeatures;
    }


    @Override
    public Classifier setUpClassifier() {

        RandomForest classifier = new RandomForest();
        classifier.setMaxDepth(maxDepth);
        classifier.setNumTrees(numTree);
        classifier.setSeed(seed);
        classifier.setNumFeatures(NumFeatures);
        return classifier;
    }

}
