/**
 * 
 */
package org.getalp.disambiguation.method.weka;

import weka.classifiers.Classifier;

/**
 * @author tchechem
 *
 */
public class LoadClassifierModelSetUp implements WekaClassifierSetUp {
	
	String modelFile;
	
	public LoadClassifierModelSetUp(String modelFile){
		this.modelFile = modelFile;
	}

	/* (non-Javadoc)
	 * @see org.getalp.disambiguation.method.weka.WekaClassifierSetUp#setUpClassifier()
	 */
	@Override
	public Classifier setUpClassifier() throws ClassifierSetUpException {
		try {
			Classifier c = (Classifier) weka.core.SerializationHelper.read(modelFile);
			return c;
		} catch (Exception e) {
			throw new ClassifierSetUpException();
		}
	}

}
