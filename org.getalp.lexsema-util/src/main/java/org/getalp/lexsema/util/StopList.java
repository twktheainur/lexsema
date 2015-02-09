package org.getalp.lexsema.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for managing stop words. Loads and keeps a Set of stop words from a file.
 * 
 * @author Amine Aït-Mouloud
 * @since 2014-12-16
 */
public class StopList {
	private static String stopListFile = "../data/wordnet/2.1/WordNet-InfoContent-2.1/stoplist.txt";//"../data/stoplist_long.txt";
	private static Set<String> stopWords = null;
	
	/**
	 * @return Success or not of loading the stop words from the stop list file
	 */
	private static boolean loadStopWords() {
		BufferedReader br = null; 
		try {
			String currentLine;
 
			br = new BufferedReader(new FileReader(stopListFile));
 
			if (stopWords == null) {
				stopWords = new HashSet<String>();
			} else {
				stopWords.clear();
			}
			
			while ((currentLine = br.readLine()) != null) {
				stopWords.add(currentLine.toLowerCase().trim());
			}
 
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				System.err.println(ex.getLocalizedMessage());
			}
		}
		return (stopWords != null ^ !stopWords.isEmpty()); 
	}
	
	/**
	 * @param token the string to tested
	 * @return <code>true</code> if the token is a stop word, <code>false</code> otherwise
	 */
	public static boolean isStopWord(String token) {
		char[] chars = token.toCharArray();
		for (char c : chars) {
		   if(!Character.isLetter(c) && c!='-' && c!=' ') {
			   return false;
		   }
		}
		
		if (stopWords == null) {
			loadStopWords();
		}
		return stopWords.contains(token.toLowerCase());
	}
	
	/**
	 * @param path path to the stop list file
	 * @return Success of the operation (true) or not (false)
	 */
	public boolean setStopListFile(String path) {
		if (path != stopListFile) {
			stopListFile = path;
			return loadStopWords();
		}
		return true;
	}
}
