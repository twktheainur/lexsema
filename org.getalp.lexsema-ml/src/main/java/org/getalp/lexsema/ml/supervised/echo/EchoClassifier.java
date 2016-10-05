package org.getalp.lexsema.ml.supervised.echo;

import org.getalp.lexsema.ml.supervised.ClassificationOutput;
import org.getalp.lexsema.ml.supervised.Classifier;
import org.getalp.lexsema.ml.supervised.FeatureIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class EchoClassifier implements Classifier {

    private static Logger logger = LoggerFactory.getLogger(EchoClassifier.class);

    private Map<String, List<String>> instances;

    private Set<String> classes;

    private URL url;

    public EchoClassifier() {
        try {
            url = new URL("http://ama.liglab.fr/~brouard/echo/echo2.php");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        instances = new HashMap<>();
    }

    @Override
    public void loadTrainingData(FeatureIndex featureIndex, List<List<String>> trainingInstances, List<String> attrs) {
        classes = new TreeSet<>();
        for (List<String> instance : trainingInstances) {
            classes.add(instance.get(0));
        }
        for (String clazz : classes) {
            instances.put(clazz, new ArrayList<String>());
            for (List<String> tokens : trainingInstances) {
                String instance = String.format("toto %s ", clazz);
                for (int i = 1; i < tokens.size(); i++) {
                    instance = String.format("%s %d", instance, featureIndex.get(tokens.get(i)));
                }

                // System.out.println(instance);

                instances.get(clazz).add(instance);
            }
        }
    }

    @Override
    public void saveModel() {

    }

    @Override
    public void trainClassifier() {
        /*Training and classification cannot be separated as Echo is called through a POST API*/
    }

    private String sendToEcho(String data) {
        String sendData = "";
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        try {
            sendData = "data=" + data + "\n";
            //création de la connections
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            //envoi de la requête
            writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write(sendData);
            writer.flush();
            //lecture de la réponse
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String ligne;
            String sortie = "";
            while ((ligne = reader.readLine()) != null) {
                sortie += ligne;
            }
            return sortie;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ignored) {
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {
            }
        }
        return "";
    }

    @Override
    public List<ClassificationOutput> classify(FeatureIndex index, List<String> features) {

        List<ClassificationOutput> results = new ArrayList<>();

        String data = "";

        String item = "toto";

        Object[] T = classes.toArray();

        String id = (String) T[0];

        if (classes.size() == 1) {

            results.add(new ClassificationOutput(id, 1, 1));

            return results;
        }


        for (String clazz : classes) {

            for (String instance : instances.get(clazz)) {
                data += String.format("%s ;", instance);
            }
        }

        String instance = item + " 0 ";
        for (int i = 1; i < features.size(); i++) {
            instance = String.format("%s %d", instance, index.get(features.get(i)));
        }
        data += String.format("%s%s ;", data, instance);

        System.out.println("data = " + data);

        String output = sendToEcho(data);
        System.out.println("output = " + output);

        output = output.replace(',', '.');//patch virgule point

        System.out.println("output = " + output);

        System.exit(0);

        String[] outputTokens = null;
        ClassificationOutput co = null;


        outputTokens = output.split("\"")[2].split(";")[0].split(":");

        for (int i = 0; i < outputTokens.length; i++) {

            System.out.println(i + " = " + outputTokens[i]);
        }

        //   co = new ClassificationOutput(clazz, outputTokens[0],1);


        results.add(co);

        //System.out.println(results);

        Collections.sort(results);

        //System.out.println(results);

        return results;
    }

    @Override
    public boolean isClassifierTrained() {
        return true;
    }
}
