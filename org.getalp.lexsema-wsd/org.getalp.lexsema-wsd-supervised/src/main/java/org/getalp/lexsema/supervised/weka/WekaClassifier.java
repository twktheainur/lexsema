package org.getalp.lexsema.supervised.weka;

import org.getalp.lexsema.supervised.ClassificationOutput;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.util.*;

public class WekaClassifier implements org.getalp.lexsema.supervised.Classifier {

    Classifier classifier;
    WekaClassifierSetUp classifierCreator;

    private FastVector attributes;
    private Instances instances;
    private String modelPath;

    private boolean load;
    private boolean loadSuccessful;
    private boolean classifierTrained;
    private Set<String> classes;

    public WekaClassifier(WekaClassifierSetUp classifierCreator, String modelPath, boolean load) {
        this.classifierCreator = classifierCreator;
        this.modelPath = modelPath;
        this.load = load;

        if (load) {
            try {
                LoadClassifierModelSetUp lcmsu = new LoadClassifierModelSetUp(modelPath);
                classifier = lcmsu.setUpClassifier();
                classifierTrained = true;
            } catch (Exception e) {
                loadSuccessful = false;
                System.err.println("Model file non-existant or cannot be loaded.");
                try {
                    classifier = classifierCreator.setUpClassifier();
                } catch (ClassifierSetUpException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    System.err.println("Cannot load classifier");
                }
            }
        } else {
            try {
                classifier = classifierCreator.setUpClassifier();
            } catch (ClassifierSetUpException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }
        }
    }

    @Override
    public void loadTrainingData(FeatureIndex featureIndex, List<List<String>> trainingInstances, List<String> attrs) throws IOException {
        classes = new TreeSet<String>();
        for (List<String> instance : trainingInstances) {
            classes.add(instance.get(0));
        }
        FastVector classAttribute = new FastVector(classes.size());

        for (String className : classes) {
            classAttribute.addElement(className);
        }
        attributes = new FastVector(attrs.size());
        attributes.addElement(new Attribute("SENSE", classAttribute));
        for (int i = 1; i < attrs.size(); i++) {
            attributes.addElement(new Attribute(attrs.get(i)));
        }

        instances = new Instances("Training Dataset", attributes, 1);
        instances.setClassIndex(0);

        for (List<String> tokens : trainingInstances) {
            Instance instance = new Instance(attributes.size());
            instance.setDataset(instances);
            instance.setValue(0, tokens.get(0));
            for (int i = 1; i < attributes.size(); i++) {
                instance.setValue(i, featureIndex.get(tokens.get(i)));
            }
            instances.add(instance);
        }
    }

    @Override
    public void saveModel() {
        try {
            weka.core.SerializationHelper.write(modelPath, classifier);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }

    @Override
    public void trainClassifier() {
        // train the neural network

        if (!loadSuccessful) {
            if (classifierTrained) {
                try {
                    classifier = classifierCreator.setUpClassifier();
                } catch (ClassifierSetUpException e) {
                    e.printStackTrace();
                }
            }
            try {
                classifier.buildClassifier(instances);
                classifierTrained = true;
            } catch (Exception e1) {
                // e1.printStackTrace();
            }
            saveModel();
        }
    }

    @Override
    public List<ClassificationOutput> classify(FeatureIndex index, List<String> features) {

        Instance instance = new Instance(features.size() + 1);
        double[] output = null;
        if (loadSuccessful) {
            instances = new Instances("Training Dataset", attributes, 1);
            instances.setClassIndex(0);
        }
        instance.setDataset(instances);
        //instance.setClassValue("\"\"");
        //instance.setValue(0, "\"\"");
        for (int feat = 0; feat < features.size(); feat++) {
            instance.setValue(feat + 1, index.get(features.get(feat)));
        }
        try {
            output = classifier.distributionForInstance(instance);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }

        List<ClassificationOutput> result = new ArrayList<ClassificationOutput>();

        try {
            int i = 0;
            for (String c : classes) {
                result.add(new ClassificationOutput(c, Double.valueOf(output[i])));
                i++;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public boolean isClassifierTrained() {
        return classifierTrained;
    }
}
