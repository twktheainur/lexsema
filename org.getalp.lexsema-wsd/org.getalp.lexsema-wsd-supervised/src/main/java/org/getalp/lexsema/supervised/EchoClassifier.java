package org.getalp.lexsema.supervised;

import org.getalp.lexsema.supervised.weka.FeatureIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
            url = new URL("http://ama.liglab.fr/~brouard/echo/echo.php");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        instances = new HashMap<>();
    }

    @Override
    public void loadTrainingData(FeatureIndex featureIndex, String file) throws IOException {
        File trainingData = new File(file);
        Collection<String[]> tokenInstances;
        try (BufferedReader br = new BufferedReader(new FileReader(trainingData))) {

            classes = new TreeSet<String>();
            String inst = "";
            String[] attrs = br.readLine().trim().split("\t");
            tokenInstances = new ArrayList<>();
            while ((inst = br.readLine()) != null) {
                String[] tokens = inst.trim().split("\t");
                classes.add(tokens[0]);
                tokenInstances.add(tokens);
            }
        }

        for (String clazz : classes) {
            instances.put(clazz, new ArrayList<String>());
            for (String[] tokens : tokenInstances) {
                String instance = "";
                if (clazz.equals(tokens[0])) {
                    instance = String.format("%s %d ", clazz, 1);
                } else {
                    instance = String.format("%s %d ", clazz, 2);
                }
                for (int i = 1; i < tokens.length; i++) {
                    instance = String.format("%s %d", instance, featureIndex.get(tokens[i]));
                }
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
        for (String clazz : classes) {
            String data = "";
            for (String instance : instances.get(clazz)) {
                data += String.format("%s ;", instance);
            }
            String instance = clazz + " 0 ";
            for (int i = 1; i < features.size(); i++) {
                instance = String.format("%s %d", instance, index.get(features.get(i)));
            }
            data = String.format("%s%s ;", data, instance);
            String output = sendToEcho(data);
            String[] outputTokens = output.split("\"")[2].split(";")[0].split(":");
            results.add(new ClassificationOutput(clazz, Double.valueOf(outputTokens[1]), Double.valueOf(outputTokens[2])));
        }
        return results;
    }

    @Override
    public boolean isClassifierTrained() {
        return true;
    }
}
