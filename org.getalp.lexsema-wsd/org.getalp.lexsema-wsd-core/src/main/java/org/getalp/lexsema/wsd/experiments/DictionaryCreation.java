package org.getalp.lexsema.wsd.experiments;

import java.io.File;

import edu.mit.jwi.Dictionary;

import org.getalp.lexsema.io.dictionary.DocumentDictionaryWriter;
import org.getalp.lexsema.io.document.WordnetGlossTagTextLoader;
import org.getalp.lexsema.io.document.loader.DSOCorpusLoader;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.SemCorCorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurusImpl;

public class DictionaryCreation
{
    public static String wordnetPath = "../data/wordnet/2.1/dict/";
    
    public static String semCorPath = "../data/semcor2.1/all.xml";

    public static String dsoPath = "../data/dso/";
    
    public static String docPath = "../data/senseval2007_task7/test/eng-coarse-all-words.xml";

    public static String wordnetGlossTagPath = "../data/wordnet/3.0/glosstag/";
    
    public static Dictionary wordnet = new Dictionary(new File(wordnetPath));
    
    public static CorpusLoader semCor = new SemCorCorpusLoader(semCorPath);
    
    public static boolean semCorIsLoaded = false;

    public static CorpusLoader dso = new DSOCorpusLoader(dsoPath, wordnetPath);

    public static boolean dsoIsLoaded = false;

    public static CorpusLoader wordnetGlossTag = new WordnetGlossTagTextLoader(wordnetGlossTagPath);

    public static boolean wordnetGlossTagIsLoaded = false;

    public static void main(String[] args)
    {
        //writeDictionary(false, false, false, "../data/dict_semeval2007task7");
        //writeDictionary(true, false, false, "../data/dict_semeval2007task7_stopwords");
        //writeDictionary(false, true, false, "../data/dict_semeval2007task7_stemming");
        //writeDictionary(true, true, false, "../data/dict_semeval2007task7_stopwords_stemming");

        //writeDictionary(false, false, true, "../data/dict_semeval2007task7_semcor");
        //writeDictionary(true, false, true, "../data/dict_semeval2007task7_stopwords_semcor");
        //writeDictionary(false, true, true, "../data/dict_semeval2007task7_stemming_semcor");
        //writeDictionary(true, true, true, "../data/dict_semeval2007task7_stopwords_stemming_semcor");

        //writeDictionary(true, true, true, true, true, true, true, false, "../data/lesk_dict/dict_semeval2007task7_stopwords_stemming_semcor");
        //writeDictionary(true, true, true, true, true, true, false, true, "../data/lesk_dict/dict_semeval2007task7_stopwords_stemming_dso");
        //writeDictionary(true, true, true, true, true, true, true, true, "../data/lesk_dict/dict_semeval2007task7_stopwords_stemming_semcor_dso");
    
        writeDictionary(true, true, true, true, true, true, true, true, true, "../data/lesk_dict/dict_semeval2007task7_stopwords_stemming_semcor_dso_wordnetglosstag");
    }
    
    private static void writeDictionary(boolean definitions, boolean extendedDefinitions, 
                                        boolean stopWords, boolean stemming, 
                                        boolean index, boolean shuffle, 
                                        boolean useSemCorThesaurus, 
                                        boolean useDSOThesaurus, 
                                        boolean useWordnetGlossTag,
                                        String newDictPath)
    {
        System.out.println("Building dictionary " + newDictPath + "...");
        WordnetLoader lrloader = new WordnetLoader(wordnet);
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
            lrloader.addThesaurus(new AnnotatedTextThesaurusImpl(semCor, 100));
        }
        if (useDSOThesaurus)
        {
            if (!dsoIsLoaded) {
                dso.load();
                dsoIsLoaded = true;
            }
            lrloader.addThesaurus(new AnnotatedTextThesaurusImpl(dso, 100));
        }
        if (useWordnetGlossTag)
        {
            if (!wordnetGlossTagIsLoaded) {
                wordnetGlossTag.load();
                wordnetGlossTagIsLoaded = true;
            }
            lrloader.addThesaurus(new AnnotatedTextThesaurusImpl(wordnetGlossTag, 100));
        }
        CorpusLoader dl = new Semeval2007CorpusLoader(docPath);
        dl.load();
        for (Text txt : dl)
        {
            lrloader.loadSenses(txt);
        }
        DocumentDictionaryWriter writer = new DocumentDictionaryWriter(dl);
        writer.writeDictionary(new File(newDictPath));
    }
}
