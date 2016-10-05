package org.getalp.lexsema.wsd.experiments.embeddings;

import edu.mit.jwi.Dictionary;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.getalp.lexsema.io.dictionary.DocumentDictionaryWriter;
import org.getalp.lexsema.io.document.loader.*;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.io.word2vec.SerializedModelWord2VecLoader;
import org.getalp.lexsema.io.word2vec.Word2VecLoader;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.signatures.enrichment.Word2VecLocalSignatureEnrichment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DictionaryCreation
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

    public static void main(String[] args) throws IOException {
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
        Word2VecLoader word2VecLoader = new SerializedModelWord2VecLoader();
        word2VecLoader.loadGoogle(new File(args[0]), true ,Boolean.valueOf(args[1]));
        WordVectors vectors = word2VecLoader.getWordVectors();
    
        writeDictionary(true, true, false, false, true, true, vectors, "../data/lesk_dict/dict_semeval2007task7_embeddings.xml");
    }
    
    private static void writeDictionary(boolean definitions, boolean extendedDefinitions, 
                                        boolean stopWords, boolean stemming, 
                                        boolean index, boolean shuffle,WordVectors wordVectors,
                                        String newDictPath) throws FileNotFoundException {
        System.out.println("Building dictionary " + newDictPath + "...");
        WordnetLoader lrloader = new WordnetLoader(wordnet);
        lrloader.filterStopWords(stopWords);
        lrloader.addSignatureEnrichment(new Word2VecLocalSignatureEnrichment(wordVectors, 10));
        lrloader.stemming(stemming);
        lrloader.loadDefinitions(definitions);
        lrloader.extendedSignature(extendedDefinitions);
        lrloader.loadRelated(extendedDefinitions);
        lrloader.index(index);
        lrloader.shuffle(shuffle);

        CorpusLoader dl = new Semeval2007CorpusLoader(new FileInputStream(docPath));
        dl.load();
        for (Text txt : dl)
        {
            lrloader.loadSenses(txt);
        }
        DocumentDictionaryWriter writer = new DocumentDictionaryWriter(dl);
        writer.writeDictionary(new File(newDictPath));
    }
}
