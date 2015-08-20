/**
 *
 */
package org.getalp.lexsema.supervised.weka;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.core.SelectedTag;
import java.io.PrintStream;
import java.io.OutputStream;

/**
 * @author tchechem
 */
public class SVMSetUp implements WekaClassifierSetUp {

    // -h 0 Turns the shrinking heuristics off
    final String options[] = {"-h 0"};



    /* (non-Javadoc)
     * @see org.getalp.lexsema.supervised.weka.WekaClassifierSetUp#setUpClassifier()
     */
    @Override
    public Classifier setUpClassifier() {

        System.setOut(new PrintStream(new OutputStream() {
            @Override public void write(int b){};
        }));

        LibSVM classifier = new LibSVM();
        classifier.setSVMType(new SelectedTag(LibSVM.SVMTYPE_C_SVC, LibSVM.TAGS_SVMTYPE));
        //classifier.setSVMType(new SelectedTag(LibSVM.SVMTYPE_NU_SVR, LibSVM.TAGS_SVMTYPE));
        classifier.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));
        //classifier.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF, LibSVM.TAGS_KERNELTYPE));
        //classifier.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_SIGMOID, LibSVM.TAGS_KERNELTYPE));
       /* classifier.setGamma(2);
        classifier.setCost(0.4);
        classifier.setCoef0(0);*/
        //

        try {
            classifier.setOptions(options);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return classifier;
    }



}
