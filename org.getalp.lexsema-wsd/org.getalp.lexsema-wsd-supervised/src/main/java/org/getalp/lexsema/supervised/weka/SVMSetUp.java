/**
 *
 */
package org.getalp.lexsema.supervised.weka;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.core.SelectedTag;

/**
 * @author tchechem
 */
public class SVMSetUp implements WekaClassifierSetUp {

    final String options[] = {"-h 0"};

    /* (non-Javadoc)
     * @see org.getalp.lexsema.supervised.weka.WekaClassifierSetUp#setUpClassifier()
     */
    @Override
    public Classifier setUpClassifier() {
        LibSVM classifier = new LibSVM();
        //classifier.setSVMType(new SelectedTag(LibSVM.SVMTYPE_C_SVC, LibSVM.TAGS_SVMTYPE));
        classifier.setSVMType(new SelectedTag(LibSVM.SVMTYPE_NU_SVC, LibSVM.TAGS_SVMTYPE));
        classifier.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));
        //classifier.gamma(2);
        //classifier.setCost(0.4);
        //classifier.setCoef0(0);
        //
        try {
            classifier.setOptions(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classifier;
    }

}
