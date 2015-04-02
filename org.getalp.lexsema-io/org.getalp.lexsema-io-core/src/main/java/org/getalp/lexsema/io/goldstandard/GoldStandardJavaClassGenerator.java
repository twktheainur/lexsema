package org.getalp.lexsema.io.goldstandard;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Map;

import static java.io.File.separator;

public class GoldStandardJavaClassGenerator {

    String entryClassName;
    String targetClassName;
    Map<String, Integer> textIndex;
    Map<String, Integer> sentenceIndex;
    private Logger logger = LoggerFactory.getLogger(GoldStandardJavaClassGenerator.class);

    public GoldStandardJavaClassGenerator(String classPrefix) {
        entryClassName = classPrefix + "GoldStandardEntry";
        targetClassName = classPrefix + "Semeval2007GoldStandardData";
    }

    public static void main(String[] args) throws FileNotFoundException {
        GoldStandardJavaClassGenerator goldStandardJavaClassGenerator = new GoldStandardJavaClassGenerator("Semeval2007");
        goldStandardJavaClassGenerator.generate(args[0]);
    }

    void generate(String goldStandardPath) throws FileNotFoundException {
        File goldStandard = new File(goldStandardPath);
        if (!goldStandard.exists()) {
            throw new FileNotFoundException(goldStandardPath + "could not be found!");
        }
        File target = new File(
                String.format("org.getalp.lexsema-io%sorg.getalp." +
                                "lexsema-io-core%ssrc%smain%sjava%sorg%sgetalp%slexsema%sio%sgoldstandard",
                        separator, separator, separator, separator, separator, separator, separator, separator, separator),
                String.format("%s.java", targetClassName));
        try (PrintWriter pw = new PrintWriter(target)) {
            generateClassHeader(pw);
            generateData(pw, goldStandard);
            generateGetters(pw);
            generateClassFooter(pw);
            pw.flush();
        }
    }

    private void generateClassHeader(PrintWriter pw) {
        pw.println("package org.getalp.lexsema.io.goldstandard;");
        pw.println("import java.util.Map;");
        pw.println("import java.util.List;");
        pw.println("import java.util.HashMap;");
        pw.println("import java.util.ArrayList;");
        pw.println("@SuppressWarnings(\"all\")");
        pw.println("public class " + targetClassName + " extends AbstractGoldStandardData {");
        pw.println();
        pw.println("\tList<GoldStandardEntry> data = new ArrayList<>();");
        pw.println("\tMap<String, Integer> textIndex = new HashMap<>();");
        pw.println("\tMap<String, Integer> textEndIndex = new HashMap<>();");
        pw.println("\tMap<String, Integer> sentenceIndex = new HashMap<>();");
        pw.println("\tMap<String, Integer> sentenceEndIndex = new HashMap<>();");
        pw.println();
        pw.println();
    }

    private void generateClassFooter(PrintWriter pw) {
        pw.println("}");
    }

    private void generateData(PrintWriter pw, File goldStandardPath) {
        String currentText = "";
        String currentSentence = "";

        pw.println("\t{");

        try (BufferedReader br = new BufferedReader(new FileReader(goldStandardPath))) {
            String line = br.readLine();
            int currentDataIndex = 0;
            String prevTextId = "";
            String prevSentenceId = "";
            while (line != null) {
                GoldStandardEntry entry = getEntryInstance(line);
                if (!entry.getTextId().equals(currentText)) {
                    currentText = entry.getTextId();
                    if (!prevTextId.isEmpty()) {
                        pw.println(String.format("\t\ttextIndex.put(\"%s\", %d);", currentText, currentDataIndex));
                        pw.println(String.format("\t\ttextEndIndex.put(\"%s\", %d);", prevTextId, currentDataIndex - 1));
                    } else {
                        pw.println(String.format("\t\ttextIndex.put(\"%s\", %d);", currentText, currentDataIndex));
                    }
                    prevTextId = currentText;
                    prevSentenceId = "";
                }
                if (!entry.getSentenceId().equals(currentSentence)) {
                    currentSentence = entry.getSentenceId();
                    if (!prevSentenceId.isEmpty()) {
                        pw.println(String.format("\t\tsentenceIndex.put(\"%s.%s\", %d);", currentText, currentSentence, currentDataIndex));
                        pw.println(String.format("\t\tsentenceEndIndex.put(\"%s.%s\", %d);", prevTextId, prevSentenceId, currentDataIndex - 1));
                    } else {
                        pw.println(String.format("\t\tsentenceIndex.put(\"%s.%s\", %d);", currentText, currentSentence, currentDataIndex));
                    }
                    prevSentenceId = currentSentence;
                }
                pw.println("\t\tdata.add(new " + entryClassName + "(\"" + line + "\"));");
                line = br.readLine();
                currentDataIndex++;
            }
            pw.println(String.format("\t\ttextEndIndex.put(\"%s\", %d);", currentText, currentDataIndex));
            pw.println(String.format("\t\tsentenceEndIndex.put(\"%s.%s\", %d);", currentText, currentSentence, currentDataIndex));
        } catch (FileNotFoundException e) {
            logger.error("Cannot find file " + goldStandardPath);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        pw.println("\t}");
    }

    private void generateGetters(PrintWriter pw) {
        pw.println(generateGetter("data", "List<GoldStandardEntry>"));
        pw.println(generateGetter("textIndex", "Map<String, Integer>"));
        pw.println(generateGetter("textEndIndex", "Map<String, Integer>"));
        pw.println(generateGetter("sentenceIndex", "Map<String, Integer>"));
        pw.println(generateGetter("sentenceEndIndex", "Map<String, Integer>"));
    }

    private String generateGetter(String attributeName, String type) {
        String getter = "@Override" + System.lineSeparator();
        getter += String.format("protected %s get%s%s() {%s", type, String.valueOf(attributeName.charAt(0)).
                toUpperCase(), attributeName.substring(1), System.lineSeparator());
        getter += String.format("\t\treturn %s;%s", attributeName, System.lineSeparator());
        getter += String.format("\t}%s", System.lineSeparator());
        return getter;
    }

    @SuppressWarnings("all")
    private GoldStandardEntry getEntryInstance(String entryLine) {
        try {
            Class<? extends GoldStandardEntry> builderClass = (Class<? extends GoldStandardEntry>)
                    Class.forName("org.getalp.lexsema.io.goldstandard." + entryClassName);
            Constructor<? extends GoldStandardEntry> entryClassConstructor = builderClass.getConstructor(String.class);
            return entryClassConstructor.newInstance(new Object[]{entryLine});
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("FATAL ERROR, ENTRY TYPE DOES NOT EXIST " + entryClassName);
        }
        System.exit(-1);
        return null;
    }

}
