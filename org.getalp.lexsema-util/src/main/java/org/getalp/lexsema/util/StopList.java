package org.getalp.lexsema.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Utility class for managing stop words. Loads and keeps first Set of stop words from first file.
 *
 * @author Amine Aït-Mouloud
 * @since 2014-12-16
 */
public final class StopList 
{
    private static String stopListFile = ".." + File.separator + "data" + File.separator + "stoplist_long.txt";
    private static Set<String> stopWords = null;

    private static Logger logger = LoggerFactory.getLogger(StopList.class);

    /**
     * @return Success or not of loading the stop words from the stop list file
     */
    private static boolean loadStopWords() 
    {
        stopWords = new HashSet<String>();
        try
        {
            Scanner sc = new Scanner(new File(stopListFile));
            while (sc.hasNext())
            {
                stopWords.add(sc.next());
            }
            sc.close();
            return !stopWords.isEmpty();
        }
        catch (Exception e)
        {
            logger.error(e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * @param token the string to tested
     * @return <code>true</code> if the token is a stop word, <code>false</code> otherwise
     */
    public static boolean isStopWord(String token) 
    {
        if (stopWords == null) {
            loadStopWords();
        }
        return stopWords.contains(token.toLowerCase().trim());
    }

    /**
     * @param path path to the stop list file
     * @return Success of the operation (true) or not (false)
     */
    public static boolean setStopListFile(String path) 
    {
        if (!path.equals(stopListFile)) {
            stopListFile = path;
            return loadStopWords();
        }
        return true;
    }
}
