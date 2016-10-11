package org.getalp.lexsema.io.clwsd.org.getalp.lexsema.io.europarl;

import java.io.*;


class FormatFile {
private String dFilepath ="ProcessedDoc/" ;
String Lang;
//	final static String filepath = "/home/bhaskar/workspace/EuroParl/txt/en/ep-00-01-17.txt";
	public FormatFile(String Lang) {		
		this.Lang = Lang;
	}
	public String format(String actFilePath, String filename) throws FileNotFoundException, IOException{
		dFilepath =dFilepath+Lang+filename;
		BufferedWriter bw = new BufferedWriter(new FileWriter(dFilepath));
		BufferedReader br = new BufferedReader(new FileReader(actFilePath));
	
		String str = filetoString(actFilePath);		
		str = str.replaceAll("<P>", "</P>\n<P>");
		str = str.replaceAll("(<SPEAKER .*?>)", "</P>\n</SPEAKER>\n$1<P>\n");
		str = str.replaceAll("(<CHAPTER .*?>)", "</P></SPEAKER></CHAPTER>\n$1\n");
		bw.write(str);

		br.close();
         bw.close();
         return dFilepath;
	}
	public static String filetoString(String fl) throws IOException{
		 BufferedReader reader = new BufferedReader( new FileReader (fl));
		    String         line = null;
		    StringBuilder  stringBuilder = new StringBuilder();
		    String         ls = System.getProperty("line.separator");

		    while( ( line = reader.readLine() ) != null ) {
		        stringBuilder.append( line );
		        stringBuilder.append( ls );
		    }

		    return stringBuilder.toString();
		
		
	}
}


