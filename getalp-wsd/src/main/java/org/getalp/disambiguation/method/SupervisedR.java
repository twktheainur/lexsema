package org.getalp.disambiguation.method;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.Sense;
import org.getalp.disambiguation.configuration.Configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by tchechem on 10/14/14.
 */
public class SupervisedR implements Disambiguator {

    private int lemmamin;
    private int lemmamax;
    private int posmin;
    private int posmax;
    private String dataPath;

    public SupervisedR(int lemmamin, int lemmamax, int posmin, int posmax, String dataPath) {
        this.lemmamin = lemmamin;
        this.lemmamax = lemmamax;
        this.posmin = posmin;
        this.posmax = posmax;
        this.dataPath = dataPath;
    }

    public String convertPos(String pos) {
        String converted = "";
        if (pos.equals("n")) {
            converted = "NN";
        } else if (pos.equals("v")) {
            converted = "VB";
        } else if (pos.equals("a")) {
            converted = "JJ";
        } else if (pos.equals("r")) {
            converted = "RB";
        }
        return converted;
    }

    @Override
    public Configuration disambiguate(Document document) {
        Configuration c = new Configuration(document);
        for (int i = 0; i < document.getWords().size(); i++) {
            System.err.print(String.format("\tDisambiguating: %.2f%%\r", ((double) i / (double) document.getWords().size()) * 100d));

            String targetLemma = document.getWords().get(i).getLemma();
            String targetPos = convertPos(document.getWords().get(i).getPos());

            String header = "";
            String featureVector = "";

            int numfeatures = 1;
            for (int j = i - lemmamin; j <= i + lemmamax; j++) {
                if (i != j) {
                    String lemmaFeature = "";
                    if (j < 0 || j >= document.getWords().size()) {
                        lemmaFeature = "\"X\"";
                    } else {
                        lemmaFeature = "\"" + document.getWords().get(j).getLemma() + "\"";
                    }
                    header += "A" + numfeatures + "\t";
                    featureVector += lemmaFeature + "\t";
                    numfeatures++;
                }
            }

            for (int j = i - posmin; j <= i + posmax; j++) {
                if (j != i) {
                    String posFeature = "";
                    if (j < 0 || j >= document.getWords().size()) {
                        posFeature = "\"0\"";
                    } else {
                        posFeature = "\"" + convertPos(document.getWords().get(j).getPos()) + "\"";
                    }
                    header += "A" + numfeatures + "\t";
                    featureVector += posFeature + "\t";
                    numfeatures++;
                }
            }
            header += "A" + numfeatures + "\t";
            header += "A" + (numfeatures + 1);
            featureVector += "\"" + targetLemma + "\"\t\"" + targetPos + "\"";

            File ffV = new File(dataPath + File.separatorChar + "script" + File.separatorChar + "inputVector.csv");
            try {
                PrintWriter featureWriter = new PrintWriter(ffV);
                featureWriter.println(header);
                featureWriter.println(featureVector);
                featureWriter.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String output = runR(targetLemma);
            if (output.length() == 0) {
                if(document.getSense().get(i).size()==1){
                    c.setSense(i, 0);
                } else {
                    c.setSense(i, -1);
                }
            } else {
                String[] table = output.split("print\\(table\\(P\\)\\);");
                if (table.length > 1) {
                    table = table[1]
                            .split(">")[0]
                            .split("P")[1]
                            .split("\n");
                    String[] keys = table[1].trim().split("\\s+");
                    String[] values = table[2].trim().split("\\s+");
                    List<RClassificationEntry> results = new ArrayList<RClassificationEntry>();
                    for (int ki = 0; ki < keys.length; ki++) {
                        results.add(new RClassificationEntry(keys[ki], Integer.valueOf(values[ki])));
                    }
                    Collections.sort(results);
                    String bestResult = results.get(0).getKey();
                    c.setSense(i, -1);
                    for (int s = 0; s < document.getSense().get(i).size(); s++) {
                        Sense cs = document.getSense().get(i).get(s);
                        if (cs.getId().contains(bestResult)) {
                            c.setSense(i, s);
                            break;
                        }
                    }
                } else {
                    c.setSense(i, -1);
                }
            }
        }
        return c;
    }

    public int getMatchingSense(String tag, int wordIndex){
        for (int s = 0; s < document.getSense().get(wordIndex).size(); s++) {
            Sense cs = document.getSense().get(wordIndex).get(s);
            if (cs.getId().contains(tag)) {
                return s;
            }
        }
        return -1;
    }

    private String runR(String lemma) {
        ProcessBuilder extrpb = new ProcessBuilder("R", "--vanilla");
        setEnvironment(extrpb);
        Process exproc = null;
        String rCommand = getRCommand(lemma);
        if (rCommand.length() > 0) {
            try {
                exproc = extrpb.start();
                InputStream exis = exproc.getInputStream();
                InputStream eerrs = exproc.getErrorStream();

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(exproc.getOutputStream()));
                for (String line : rCommand.split("\n")) {
                    bw.write(line);
                    bw.newLine();
                }
                bw.flush();
                exproc.waitFor();
                String outputstream = convertStreamToStr(exis);
                System.out.println(outputstream);
                String errstream = convertStreamToStr(eerrs);
                System.out.println(errstream);
                return outputstream;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void setEnvironment(ProcessBuilder pb) {
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            pb.environment().put(envName, env.get(envName));
        }
    }

    public String convertStreamToStr(InputStream is) throws IOException {

        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is,
                        "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    public String getRCommand(String lemma) {
        File f = new File(dataPath + File.separatorChar + lemma + ".csv");
        if (f.exists()) {
            return "#if(file.exists(\"" + dataPath + File.separatorChar + "script" + File.separatorChar + lemma + ".RData\")){\n" +
                    "#load(\"" + dataPath + File.separatorChar + "script" + File.separatorChar + lemma + ".RData\")\n" +
                    "#} else {\n" +
                    "examples <- read.table(\"" + dataPath + File.separatorChar + lemma + ".csv\", header=T);\n" +
                    "library(e1071);\n" +
                    "#examples <- apply(examples, 1, function(x) as.factor(x))\n" +
                    lemma + " <- svm(SENSE ~ ., data=examples, type = \"C-classification\", kernel = \"linear\", degree = 2, gamma = 2, cost = 0.4, coef0 = 0);\n" +
                    "save(" + lemma + ",file = \"" + dataPath + File.separatorChar + "script" + File.separatorChar + lemma + ".RData\");\n" +
                    "#}\n" +
                    "input = read.table(\"" + dataPath + File.separatorChar + "script" + File.separatorChar + "inputVector.csv\",header =T);\n" +
                    "#input[] <- apply(input, 1, function(x) as.factor(x))\n" +
                    "P <- predict(" + lemma + ", input, type=\"class    \");\n" +
                    "print(table(P));\n" +
                    "quit()";
        } else {
            return "";
        }
    }

    public class RClassificationEntry implements Comparable<RClassificationEntry> {
        private String key;
        private int frequency;

        public RClassificationEntry(String key, int frequency) {
            this.key = key;
            this.frequency = frequency;
        }

        public String getKey() {
            return key;
        }

        public int getFrequency() {
            return frequency;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RClassificationEntry)) return false;

            RClassificationEntry that = (RClassificationEntry) o;

            if (key != null ? !key.equals(that.key) : that.key != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return key != null ? key.hashCode() : 0;
        }

        @Override
        public int compareTo(RClassificationEntry o) {
            return Integer.valueOf(o.getFrequency()).compareTo(frequency);
        }
    }
}
