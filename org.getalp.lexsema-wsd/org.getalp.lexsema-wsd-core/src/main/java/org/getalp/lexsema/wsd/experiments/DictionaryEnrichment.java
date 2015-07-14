package org.getalp.lexsema.wsd.experiments;

import java.io.File;

import org.getalp.lexsema.io.definitionenricher.TextDefinitionEnricher;
import org.getalp.lexsema.io.dictionary.DocumentDictionaryWriter;
import org.getalp.lexsema.io.document.SemCorTextLoader;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Text;

public class DictionaryEnrichment
{
    public static String dictPath = "../data/wordnet/2.1/dict/";
    
    public static String semCorPath = "../data/semcor2.1/all.xml";
    
    public static String docPath = "../data/senseval2007_task7/test/eng-coarse-all-words.xml";
    
    public static TextLoader semCor = new SemCorTextLoader(semCorPath);
    
    public static TextDefinitionEnricher semCorExpender =  new TextDefinitionEnricher(semCor);
    
    public static void main(String[] args)
    {
        //writeDictionary(false, false, false, "../data/dict_semeval2007task7");
        //writeDictionary(true, false, false, "../data/dict_semeval2007task7_stopwords");
        //writeDictionary(false, true, false, "../data/dict_semeval2007task7_stemming");
        //writeDictionary(true, true, false, "../data/dict_semeval2007task7_stopwords_stemming");

        //writeDictionary(false, false, true, "../data/dict_semeval2007task7_semcor");
        //writeDictionary(true, false, true, "../data/dict_semeval2007task7_stopwords_semcor");
        //writeDictionary(false, true, true, "../data/dict_semeval2007task7_stemming_semcor");
        writeDictionary(true, true, true, "../data/dict_semeval2007task7_stopwords_stemming_semcor");
    }
    
    private static void writeDictionary(boolean stopWords, boolean stemming, boolean semCorrify, String newDictPath)
    {
        System.out.println("Building dictionary " + newDictPath + "...");
        WordnetLoader lrloader = new WordnetLoader(dictPath);
        lrloader.loadDefinitions(true);
        lrloader.extendedSignature(true);
        lrloader.setLoadRelated(true);
        lrloader.setUsesIndex(true);
        lrloader.shuffle(true);
        lrloader.setUsesStopWords(stopWords);
        lrloader.setStemming(stemming);
        lrloader.setUsesSemCor(semCorrify);
        lrloader.setSemCorDefinitionExpender(semCorExpender);
        lrloader.setSemCorNumberOfWordsToTake(20);
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
