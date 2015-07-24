package org.getalp.lexsema.wsd.experiments;

import java.io.File;

import edu.mit.jwi.Dictionary;

import org.getalp.lexsema.io.dictionary.DocumentDictionaryWriter;
import org.getalp.lexsema.io.document.DSOTextLoader;
import org.getalp.lexsema.io.document.SemCorTextLoader;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurus;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurusImpl;

public class DictionaryEnrichment
{
    public static String wordnetPath = "../data/wordnet/2.1/dict/";
    
    public static String semCorPath = "../data/semcor2.1/all.xml";

    public static String dsoPath = "../data/dso/";
    
    public static String docPath = "../data/senseval2007_task7/test/eng-coarse-all-words.xml";

    public static Dictionary wordnet = new Dictionary(new File(wordnetPath));
    
    public static TextLoader semCor = new SemCorTextLoader(semCorPath);

    public static TextLoader dso = new DSOTextLoader(dsoPath, wordnetPath);
    
    public static void main(String[] args)
    {
        //semCor.load();
        dso.load();
        AnnotatedTextThesaurus dsoThesaurus =  new AnnotatedTextThesaurusImpl(dso, 10);
        //writeDictionary(false, false, false, "../data/dict_semeval2007task7");
        //writeDictionary(true, false, false, "../data/dict_semeval2007task7_stopwords");
        //writeDictionary(false, true, false, "../data/dict_semeval2007task7_stemming");
        //writeDictionary(true, true, false, "../data/dict_semeval2007task7_stopwords_stemming");

        //writeDictionary(false, false, true, "../data/dict_semeval2007task7_semcor");
        //writeDictionary(true, false, true, "../data/dict_semeval2007task7_stopwords_semcor");
        //writeDictionary(false, true, true, "../data/dict_semeval2007task7_stemming_semcor");
        //writeDictionary(true, true, true, "../data/dict_semeval2007task7_stopwords_stemming_semcor");
        /*
        for (int i = 100 ; i <= 100 ; i += 5)
        {
            writeDictionary(true, true, true, true, true, i, "../data/dict_semeval2007task7_stopwords_stemming_semcor" + i);
        }
        */
        writeDictionary(false, false, true, true, false, true, dsoThesaurus, 20, "../data/test");
    }
    
    private static void writeDictionary(boolean definitions, boolean extendedDefinitions, 
                                        boolean stopWords, boolean stemming, 
                                        boolean index, boolean shuffle, 
                                        AnnotatedTextThesaurus thesaurus, int nbThesaurusWords, 
                                        String newDictPath)
    {
        System.out.println("Building dictionary " + newDictPath + "...");
        LRLoader lrloader = new WordnetLoader(wordnet, thesaurus)
        .loadDefinitions(definitions)
        .extendedSignature(extendedDefinitions)
        .loadRelated(extendedDefinitions)
        .index(index)
        .shuffle(shuffle)
        .filterStopWords(stopWords)
        .stemming(stemming);
        TextLoader dl = new Semeval2007TextLoader(docPath);
        dl.load();
        for (Text txt : dl)
        {
            lrloader.loadSenses(txt);
        }
        DocumentDictionaryWriter writer = new DocumentDictionaryWriter(dl);
        writer.writeDictionary(new File(newDictPath));
    }
}