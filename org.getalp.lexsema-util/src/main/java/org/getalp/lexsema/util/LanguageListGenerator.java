package org.getalp.lexsema.util;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LanguageListGenerator {
    public static void main(String[] args) throws IOException {
        Map<String,Map<String,String>> languages = new HashMap<>();
        File input = new File(args[0]);
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            String line = br.readLine();
            while (line!=null){
                //System.out.println(line);
                String[] tokens = line.split(",");
                if(tokens.length==3 && tokens[1].trim().length()==2) {
                    System.err.println(generateId(tokens[2]) + "(\"" + filterThreeLetterCode(tokens[0]) + "\",\"" + tokens[1] + "\",\"" + tokens[2] + "\"),");
                }
                line = br.readLine();
            }
        }
    }

    private static String generateId(String name){
        String id;
        if(name.contains(";")){
            String[] variants = name.split(";");
            id = variants[0].trim().replaceAll(" ","_").toUpperCase();
        } else if(name.contains("(")){
            id = name.substring(0,name.indexOf("(")).toUpperCase();
        } else {
            id = name.toUpperCase();
        }
        //System.err.println(id);
        return id;
    }

    private static  String filterThreeLetterCode(String code){
        if(code.contains("(")){
            String[] tokens = code.split("\\)");
            if(tokens.length>2) {
                code = code.substring(code.indexOf(")"), code.length()).trim();
            } else {
                code = code.substring(0,code.indexOf("("));
            }
        }
        return code.trim();
    }
}
