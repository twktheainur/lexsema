package org.getalp.lexsema.supervised.weka;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class WekaClassifier {

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

    public void loadTrainingData(FeatureIndex featureIndex, String file) throws IOException {
        File trainingData = new File(file);
        BufferedReader br = new BufferedReader(new FileReader(trainingData));

        classes = new TreeSet<String>();
        String inst = "";
        List<String[]> instanceTokens = new ArrayList<String[]>();
        String[] attrs = br.readLine().trim().split("\t");
        while ((inst = br.readLine()) != null) {
            String[] tokens = inst.trim().split("\t");
            classes.add(tokens[0]);
            instanceTokens.add(tokens);

        }

        FastVector classAttribute = new FastVector(classes.size());
        for (String className : classes) {
            classAttribute.addElement(className);
        }

        attributes = new FastVector(attrs.length);
        attributes.addElement(new Attribute("SENSE", classAttribute));
        for (int i = 1; i < attrs.length; i++) {
            attributes.addElement(new Attribute(attrs[i]));
        }

        instances = new Instances("Training Dataset", attributes, 1);
        instances.setClassIndex(0);

        for (String[] tokens : instanceTokens) {
            Instance instance = new Instance(attributes.size());
            instance.setDataset(instances);
            instance.setValue(0, tokens[0]);
            for (int i = 1; i < attributes.size(); i++) {
                instance.setValue(i, featureIndex.get(tokens[i]));
            }
            instances.add(instance);
        }
    }

    public void saveModel() {
        try {
            weka.core.SerializationHelper.write(modelPath, classifier);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }

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

    public List<ClassificationEntry> classify(FeatureIndex index, List<String> features) {

        Instance instance = new Instance(features.size() + 1);
        instance.setDataset(instances);
        for (int feat = 1; feat < features.size(); feat++) {
            instance.setValue(feat, index.get(features.get(feat)));
        }
        double[] output = null;
        if (loadSuccessful) {
            instances = new Instances("Training Dataset", attributes, 1);
            this.instances.setClassIndex(0);
        }
        instance.setDataset(instances);
        try {
            output = classifier.distributionForInstance(instance);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }

        List<ClassificationEntry> result = new ArrayList<ClassificationEntry>();

        try {
            int i = 0;
            for (String c : classes) {
                result.add(new ClassificationEntry(c, Double.valueOf(output[i])));
                i++;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        Collections.sort(result);
        return result;
    }

    public boolean isClassifierTrained() {
        return classifierTrained;
    }

    public class ClassificationEntry implements Comparable<ClassificationEntry> {
        private String key;
        private double frequency;

        public ClassificationEntry(String key, double frequency) {
            this.key = key;
            this.frequency = frequency;
        }

        public String getKey() {
            return key;
        }

        public double getFrequency() {
            return frequency;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassificationEntry)) return false;

            ClassificationEntry that = (ClassificationEntry) o;

            if (key != null ? !key.equals(that.key) : that.key != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return key != null ? key.hashCode() : 0;
        }

        @Override
        public int compareTo(ClassificationEntry o) {
            return Double.valueOf(o.getFrequency()).compareTo(frequency);
        }
    }
}
