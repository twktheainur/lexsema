package org.getalp.lexsema.wsd.experiments;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.apache.commons.io.IOUtils;

public class WSDForSMT2
{
    private static final String serviceURL = "http://localhost:8081/org.getalp.lexsema-ws/wsdforsmtservice";

    private static String send(String first, String second) {
        try {
            String query = "first=" + URLEncoder.encode(first, "UTF-8") + "&second=" + URLEncoder.encode(second, "UTF-8");
            URL url = new URL(serviceURL);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            writer.write(query); writer.flush();
            InputStream response = con.getInputStream();
            return IOUtils.toString(response, "UTF-8");
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
	/**
	 * Input: Array of Wordnet senses (form [xxx, yyy, zzz]) following by array of words (same form) 
	 * Output: integer, score
	 */
    public static void main(String[] args) throws Exception
    {
		PrintStream stdout = System.out;
		System.setOut(new PrintStream(new File("sysout")));
		System.setErr(new PrintStream(new File("syserr")));
		
		StringBuilder argsInOneBuilder = new StringBuilder();
		for (String arg : args) {
			argsInOneBuilder.append(arg + " ");
		}
		String argsInOne = argsInOneBuilder.toString();
		
		String firstArg = argsInOne.substring(argsInOne.indexOf("[") + 1, argsInOne.indexOf("]"));
		String secondArg = argsInOne.substring(argsInOne.lastIndexOf("[") + 1, argsInOne.lastIndexOf("]"));
		
		String score = send(firstArg, secondArg);

        System.setOut(stdout);
        System.out.println(score);
    }
}
