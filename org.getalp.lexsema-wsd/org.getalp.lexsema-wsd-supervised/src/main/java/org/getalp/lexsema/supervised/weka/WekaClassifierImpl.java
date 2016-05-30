package org.getalp.lexsema.supervised.weka;

import org.getalp.lexsema.supervised.ClassificationOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class WekaClassifierImpl implements WekaClassifier {

    private static final Logger logger = LoggerFactory.getLogger(WekaClassifier.class);

    Classifier classifier;
    WekaClassifierSetUp classifierCreator;

    private FastVector attributes;
    private Instances instances;
    private final String modelPath;

    private boolean loadSuccessful;
    private boolean classifierTrained;
    private Set<String> classes = Collections.emptySet();

    public WekaClassifierImpl(WekaClassifierSetUp classifierCreator, String modelPath, boolean load) {
        this.classifierCreator = classifierCreator;
        this.modelPath = modelPath;

        loadSuccessful = false;
        classifierTrained = false;
        attributes = new FastVector(0);
        instances = new Instances(null, attributes, 0);
        classifier = new NullClassifier();
        if (load) {
            try {
                loadClassifierModel(modelPath);
                classifierTrained = true;
            } catch (ClassifierSetUpException e) {
                logger.error(MessageFormat.format("Model file non-existent or cannot be loaded:{0}", e.getLocalizedMessage()));
                loadSuccessful = false;
                createNewClassifier();
            }
        } else {
            createNewClassifier();
        }
    }

    private void createNewClassifier() {
        try {
            classifier = classifierCreator.setUpClassifier();
        } catch (ClassifierSetUpException e1) {
            logger.error(MessageFormat.format("Cannot create new classifier instance:{0}", e1.getLocalizedMessage()));
        }
    }

    private void loadClassifierModel(String modelPath) throws ClassifierSetUpException {
        WekaClassifierSetUp loadClassifierModelSetUp = new LoadClassifierModelSetUp(modelPath);
        classifier = loadClassifierModelSetUp.setUpClassifier();
    }

    /*@Override
    public void loadTrainingData(FeatureIndex featureIndex, List<List<String>> trainingInstances, List<String> attrs) throws IOException {
        classes = trainingInstances.stream().map(instance -> instance.get(0)).collect(Collectors.toSet());
        FastVector classAttribute = new FastVector(classes.size());

        classes.forEach(classAttribute::addElement);
        attributes = new FastVector(attrs.size());
        attributes.addElement(new Attribute("SENSE", classAttribute));
        for (int i = 1; i < attrs.size(); i++) {
            attributes.addElement(new Attribute(attrs.get(i)));
        }

        instances = new Instances("Training Dataset", attributes, 1);
        instances.setClassIndex(0);

        System.err.println("Taille " + trainingInstances.size());

        for (List<String> tokens : trainingInstances) {

            System.err.println("tokens " + tokens);
            //System.exit(0);
            Instance instance = new Instance(attributes.size());
            instance.setDataset(instances);
            instance.setValue(0, tokens.get(0));
            for (int i = 1; i < attributes.size(); i++) {
                instance.setValue(i, featureIndex.get(tokens.get(i)));
            }
            instances.add(instance);
        }
    }*/

    @Override
    public void loadTrainingData(FeatureIndex featureIndex, List<List<String>> trainingInstances, List<String> attrs) throws IOException {

        //classes = sens possibles
        classes = trainingInstances.stream().map(instance -> instance.get(0)).collect(Collectors.toSet());

      //  System.err.println("classes " + classes);

       // System.err.println("attrs " + attrs);

        FastVector classAttribute = new FastVector(classes.size());

        classes.forEach(classAttribute::addElement);

        attributes = new FastVector(attrs.size());
        attributes.addElement(new Attribute("SENSE", classAttribute));
        for (int i = 1; i < attrs.size(); i++) {

            attributes.addElement(new Attribute(attrs.get(i)));
        }

        instances = new Instances("Training Dataset", attributes, 1);
        instances.setClassIndex(0);

        //System.err.println("Taille " + trainingInstances.size());

        //System.exit(0);


        for (List<String> tokens : trainingInstances) {

           // System.err.println("tokens " + tokens);
            List<String> newToken = convert(tokens, featureIndex);
            //System.err.println("newToken " + newToken);

            Instance instance = new Instance(newToken.size());
            instance.setDataset(instances);
         //   System.err.println("instance " + instance);
         //   System.err.println("newToken.get(0) " + newToken.get(0));
            instance.setValue(0, newToken.get(0));
            for (int i = 1; i < attributes.size(); i++) {
                //instance.setValue(i, featureIndex.get(newToken.get(i)));
                instance.setValue(i, Integer.parseInt(newToken.get(i)));
            }
            //System.err.println("instance " + instance);
           // System.exit(0);
            instances.add(instance);
        }

    }

    private List<String> convert(List<String> tokens, FeatureIndex featureIndex) {

        List<String> ls = new ArrayList<String>(10000);//tokens.subList(0, tokens.size());//comment créer une liste ??

        ls.add(tokens.get(0));//identifiant sens

        char state = 'S';
        int vocabularySize=-1;
        int ivoc = 0;//iterateur sur vocabulaire

        for(int i = 1 ; i < tokens.size(); i++){


            String current = tokens.get(i);

            switch(state){

                case 'S':{//cas par défaut, on recopie le vecteur jusqu'à arriver à un debVector

                    if(current.equals("debVector"))
                        state = 'A';
                    else{

                        ls.add(featureIndex.get(current)+"");
                    }
                    break;
                }

                case 'A':{//lecture taille vecteur

                    vocabularySize=Integer.parseInt(current);
                    state = 'B';
                    break;
                }

                case 'B':{

                    if(current.equals("endVector")) {

                        //compléter le vecteur de vocabulaire par des 0
                        for (;ivoc < vocabularySize; ivoc++){

                            ls.add("0");
                        }
                        state = 'S';
                    }
                    else{//mettre des zeros dans le vecteur du vocabulaire jusqu'à l'indice et y mettre un 1

                        int nextValue = Integer.parseInt(current);
                        for (;ivoc < nextValue; ivoc++){

                            ls.add("0");
                        }
                        ls.add("1");
                        ivoc++;
                    }

                    break;
                }

                default:{

                    System.err.println("ERROR");
                    System.exit(0);
                    break;
                }


            }

        }
        return ls;
    }

    @Override
    public void saveModel() {
        try {
            weka.core.SerializationHelper.write(modelPath, classifier);
        } catch (Exception e) {
            logger.error(MessageFormat.format("Failed to serialize classification model: {0}", e.getLocalizedMessage()));
        }
    }

    @Override
    public void trainClassifier() {
        // train the neural network

        if (!loadSuccessful) {
            if (classifierTrained) {
                createNewClassifier();
            }
            try {
                if(instances.numClasses()>=2) {
                    classifier.buildClassifier(instances);
                    classifierTrained = true;
                }
            } catch (Exception e1) {
                logger.error(MessageFormat.format("Failed to create new classifier for (re)traing: {0}", e1.getLocalizedMessage()));
            }
           // saveModel();
        }
    }

    @Override
    public List<ClassificationOutput> classify(FeatureIndex index, List<String> features) {

        Instance instance = new Instance(features.size() + 1);
        double[] output;
        if (loadSuccessful) {
            instances = new Instances("Training Dataset", attributes, 1);
            instances.setClassIndex(0);
        }
        instance.setDataset(instances);

        for (int feat = 0; feat < features.size(); feat++) {
            instance.setValue(feat + 1, index.get(features.get(feat)));
        }
        try {
            output = classifier.distributionForInstance(instance);
        } catch (Exception e) {
            logger.error(String.format("Classification for %s failed: %s", instance.toString(), e.getLocalizedMessage()));
            output = new double[0];
        }

        List<ClassificationOutput> result = new ArrayList<>();

        try {
            int i = 0;
            for (String className : classes) {
                result.add(new ClassificationOutput(className, output[i]));
                i++;
            }
        } catch (RuntimeException e) {
            logger.error(MessageFormat.format("Cannot generate classification result: {0}", e.getLocalizedMessage()));
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public boolean isClassifierTrained() {
        return classifierTrained;
    }

    private static class NullClassifier extends Classifier {
        @Override
        public void buildClassifier(Instances data) throws Exception {

        }

        @SuppressWarnings({"MethodReturnOfConcreteClass", "CastToConcreteClass"})
        @Override
        public NullClassifier clone() throws CloneNotSupportedException {
            return (NullClassifier) super.clone();
        }
    }
}
