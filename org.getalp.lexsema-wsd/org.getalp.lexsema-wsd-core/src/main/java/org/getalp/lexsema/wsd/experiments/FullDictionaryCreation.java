package org.getalp.lexsema.wsd.experiments;

import edu.mit.jwi.Dictionary;
import org.getalp.lexsema.io.dictionary.DictionaryWriter;
import org.getalp.lexsema.io.dictionary.FullLRDictionaryWriter;
import org.getalp.lexsema.io.document.loader.*;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurusImpl;
import org.getalp.lexsema.similarity.Text;

import java.io.File;
import java.io.FileNotFoundException;

public class FullDictionaryCreation
{
    public static String wordnetPath = "../data/wordnet/2.1/dict/";
    
    public static String semCorPath = "../data/semcor2.1/all.xml";

    public static String dsoPath = "../data/dso/";
    
    public static String docPath = "../data/senseval2007_task7/test/eng-coarse-all-words.xml";

    public static String wordnetGlossTagPath = "../data/wordnet/3.0/glosstag/";
    
    public static String gmbPath = "../data/gmb-2.2.0/";
    
    public static Dictionary wordnet = new Dictionary(new File(wordnetPath));
    
    public static CorpusLoader semCor = new SemCorCorpusLoader(semCorPath);
    
    public static CorpusLoader dso = new OldDSOCorpusLoader(dsoPath, wordnetPath);

    public static CorpusLoader wordnetGlossTag = new WordnetGlossTagCorpusLoader(wordnetGlossTagPath);

    public static CorpusLoader gmb = new GMBCorpusLoader(gmbPath, wordnet);

    public static boolean semCorIsLoaded = false;

    public static boolean dsoIsLoaded = false;

    public static boolean wordnetGlossTagIsLoaded = false;

    public static boolean gmbgIsLoaded = false;
    
    public static int numberOfWordsFromThesauri = 100;

    public static void main(String[] args) throws FileNotFoundException {
        //writeDictionary(false, false, false, "../data/dict_semeval2007task7");
        //writeDictionary(true, false, false, "../data/dict_semeval2007task7_stopwords");
        //writeDictionary(false, true, false, "../data/dict_semeval2007task7_stemming");
        //writeDictionary(true, true, false, "../data/dict_semeval2007task7_stopwords_stemming");

        //writeDictgionary(false, false, true, "../data/dict_semeval2007task7_semcor");
        //writeDictionary(true, false, true, "../data/dict_semeval2007task7_stopwords_semcor");
        //writeDictionary(false, true, true, "../data/dict_semeval2007task7_stemming_semcor");
        //writeDictionary(true, true, true, "../data/dict_semeval2007task7_stopwords_stemming_semcor");

        //writeDictionary(true, true, true, true, true, true, true, false, "../data/lesk_dict/dict_semeval2007task7_stopwords_stemming_semcor");
        //writeDictionary(true, true, true, true, true, true, false, true, "../data/lesk_dict/dict_semeval2007task7_stopwords_stemming_dso");
        //writeDictionary(true, true, true, true, true, true, true, true, "../data/lesk_dict/dict_semeval2007task7_stopwords_stemming_semcor_dso");
    
        writeDictionary(true, true, true, false, false, true, false, false, false, false, "../data/lesk_dict/wordnet_full_disct.xml");
    }
    
    private static void writeDictionary(boolean definitions, boolean extendedDefinitions, 
                                        boolean stopWords, boolean stemming, 
                                        boolean index, boolean shuffle, 
                                        boolean useSemCorThesaurus, 
                                        boolean useDSOThesaurus, 
                                        boolean useWordnetGlossTag,
                                        boolean useGMBThesaurus,
                                        String newDictPath) throws FileNotFoundException {
        System.out.println("Building dictionary " + newDictPath + "...");
        LRLoader lrloader = new WordnetLoader(wordnet);
        lrloader.loadDefinitions(definitions);
        lrloader.extendedSignature(extendedDefinitions);
        lrloader.loadRelated(extendedDefinitions);
        lrloader.index(index);
        lrloader.shuffle(shuffle);
        lrloader.filterStopWords(stopWords);
        lrloader.stemming(stemming);
        if (useSemCorThesaurus)
        {
            if (!semCorIsLoaded) {
                semCor.load();
                semCorIsLoaded = true;
            }
            lrloader.addThesaurus(new AnnotatedTextThesaurusImpl(semCor, numberOfWordsFromThesauri));
        }
        if (useDSOThesaurus)
        {
            if (!dsoIsLoaded) {
                dso.load();
                dsoIsLoaded = true;
            }
            lrloader.addThesaurus(new AnnotatedTextThesaurusImpl(dso, numberOfWordsFromThesauri));
        }
        if (useWordnetGlossTag)
        {
            if (!wordnetGlossTagIsLoaded) {
                wordnetGlossTag.load();
                wordnetGlossTagIsLoaded = true;
            }
            lrloader.addThesaurus(new AnnotatedTextThesaurusImpl(wordnetGlossTag, numberOfWordsFromThesauri));
        }
        if (useGMBThesaurus)
        {
            if (!gmbgIsLoaded) {
                gmb.load();
                gmbgIsLoaded = true;
            }
            lrloader.addThesaurus(new AnnotatedTextThesaurusImpl(gmb, numberOfWordsFromThesauri));
        }

        DictionaryWriter writer = new FullLRDictionaryWriter(lrloader);
        writer.writeDictionary(new File(newDictPath));
    }
}
