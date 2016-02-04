package org.getalp.lexsema.wsd.experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.mit.jwi.Dictionary;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.getalp.lexsema.io.dictionary.DocumentDictionaryWriter;
import org.getalp.lexsema.io.document.loader.DSOCorpusLoader;
import org.getalp.lexsema.io.document.loader.GMBCorpusLoader;
import org.getalp.lexsema.io.document.loader.OldDSOCorpusLoader;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.SemCorCorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.document.loader.WordnetGlossTagCorpusLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.signatures.enrichment.SignatureEnrichment;
import org.getalp.lexsema.similarity.signatures.enrichment.Word2VecLocalSignatureEnrichment;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurusImpl;
import org.getalp.lexsema.io.word2vec.SerializedModelWord2VecLoader;
import org.getalp.lexsema.io.word2vec.Word2VecLoader;

public class DictionaryCreation
{
    public static String wordnetPath = "../data/wordnet/2.1/dict/";
    
    public static String semCorPath = "../data/semcor2.1/all.xml";

    public static String dsoPath = "../data/dso/";
    
    public static String docPath = "../data/senseval2007_task7/test/eng-coarse-all-words.xml";

    public static String wordnetGlossTagPath = "../data/wordnet/3.0/glosstag/";
    
    public static String gmbPath = "../data/gmb-2.2.0/";
    
    public static String word2vecPath = "../data/word2vec/";
    
    public static Dictionary wordnet = new Dictionary(new File(wordnetPath));
    
    public static CorpusLoader semCor = new SemCorCorpusLoader(semCorPath);
    
    public static CorpusLoader dso = new OldDSOCorpusLoader(dsoPath, wordnetPath);

    public static CorpusLoader wordnetGlossTag = new WordnetGlossTagCorpusLoader(wordnetGlossTagPath);

    public static CorpusLoader gmb = new GMBCorpusLoader(gmbPath, wordnet);
    
    public static CorpusLoader corpus = initializeSemeval2007Corpus();
    
    public static Word2VecLoader word2VecLoader = new SerializedModelWord2VecLoader();
    
    public static SignatureEnrichment signatureEnrichment = null;

    public static boolean semCorIsLoaded = false;

    public static boolean dsoIsLoaded = false;

    public static boolean wordnetGlossTagIsLoaded = false;

    public static boolean gmbgIsLoaded = false;
    
    public static boolean word2vecIsLoaded = false;
    
    public static boolean corpusIsLoaded = false;
    
    public static void main(String[] args) throws Exception
    {
        writeDictionary(true, true, true, true, true, true, false, false, false, false, 100, true, 20, true, "../data/lesk_dict/semeval2007task7/w2v20");
        writeDictionary(true, true, true, true, true, true, false, false, false, false, 100, true, 15, true, "../data/lesk_dict/semeval2007task7/w2v15");
        writeDictionary(true, true, true, true, true, true, false, false, false, false, 100, true, 5, true, "../data/lesk_dict/semeval2007task7/w2v5");
        writeDictionary(true, true, true, true, true, true, false, false, false, false, 100, true, 3, true, "../data/lesk_dict/semeval2007task7/w2v3");
    }
    
    public static void writeDictionary(boolean definitions, boolean extendedDefinitions, 
                                        boolean stopWords, boolean stemming, 
                                        boolean index, boolean shuffle, 
                                        boolean useSemCorThesaurus, 
                                        boolean useDSOThesaurus, 
                                        boolean useWordnetGlossTag,
                                        boolean useGMBThesaurus,
                                        int numberOfWordsFromThesauri,
                                        boolean useWord2Vec,
                                        int numberOfWordsFromWord2Vec,
                                        boolean limitToCorpus, 
                                        String newDictPath) throws Exception
    {
        System.out.println("Building dictionary " + newDictPath + "...");
        
        WordnetLoader lrloader = new WordnetLoader(wordnet);
        ArrayList<Text> corpora = new ArrayList<Text>();
        
        lrloader.loadDefinitions(definitions);
        lrloader.extendedSignature(extendedDefinitions);
        lrloader.loadRelated(extendedDefinitions);
        lrloader.index(index);
        lrloader.shuffle(shuffle);
        lrloader.filterStopWords(stopWords);
        lrloader.stemming(stemming);
        
        if (useSemCorThesaurus)
        {
            if (!semCorIsLoaded) 
            {
                semCor.load();
                semCorIsLoaded = true;
            }
            for (Text corpusText : semCor)
            {
                corpora.add(corpusText);
            }
        }
        
        if (useDSOThesaurus)
        {
            if (!dsoIsLoaded) 
            {
                dso.load();
                dsoIsLoaded = true;
            }
            for (Text corpusText : dso)
            {
                corpora.add(corpusText);
            }
        }
        
        if (useWordnetGlossTag)
        {
            if (!wordnetGlossTagIsLoaded) 
            {
                wordnetGlossTag.load();
                wordnetGlossTagIsLoaded = true;
            }
            for (Text corpusText : wordnetGlossTag)
            {
                corpora.add(corpusText);
            }
        }
        
        if (useGMBThesaurus)
        {
            if (!gmbgIsLoaded) 
            {
                gmb.load();
                gmbgIsLoaded = true;
            }
            for (Text corpusText : gmb)
            {
                corpora.add(corpusText);
            }
        }
        
        lrloader.addThesaurus(new AnnotatedTextThesaurusImpl(corpora, numberOfWordsFromThesauri));
        
        if (useWord2Vec)
        {
        	if (!word2vecIsLoaded)
        	{
                word2VecLoader.loadGoogle(new File(word2vecPath), true, true);
                signatureEnrichment = new Word2VecLocalSignatureEnrichment(word2VecLoader.getWordVectors(), numberOfWordsFromWord2Vec);
                word2vecIsLoaded = true;
        	}
        	lrloader.signatureEnrichment(signatureEnrichment);
        }
        
        if (limitToCorpus)
        {
            if (!corpusIsLoaded)
            {
            	corpus.load();
            	corpusIsLoaded = true;
            }
            for (Text txt : corpus)
            {
                lrloader.loadSenses(txt);
            }
            DocumentDictionaryWriter writer = new DocumentDictionaryWriter(corpus);
            writer.writeDictionary(new File(newDictPath));
        }
        else
        {
            Map<Word, List<Sense>> senses = lrloader.getAllSenses();
            System.out.println("" + senses.size() + " words");
            File newDict = new File(newDictPath);
            FileOutputStream fos = new FileOutputStream(newDict);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write("<dict>");
            bw.newLine();
            for (Word word : senses.keySet())
            {
            	bw.write(String.format("<word tag=\"%s%%%s\">", word.getLemma(), word.getPartOfSpeech()));
            	bw.newLine();
                for (Sense sense : senses.get(word))
                {
                	bw.write("<sense>");
                	bw.newLine();
                    bw.write(String.format("<ids>%s</ids>", sense.getId()));
                    bw.newLine();
                    bw.write("<def>");
                    bw.write(sense.getSemanticSignature().toString());
                    bw.write("</def>");
                    bw.newLine();
                    bw.write("</sense>");
                    bw.newLine();
                }
                bw.write("</word>");
                bw.newLine();
            }
            bw.write("</dict>");
            bw.newLine();
            bw.flush();
            bw.close();
            fos.flush();
            fos.close();
        }
        
    }
    
    public static CorpusLoader initializeSemeval2007Corpus()
    {
    	try
    	{
			return new Semeval2007CorpusLoader(new FileInputStream(docPath));
		}
    	catch (FileNotFoundException e)
    	{
			throw new Error(e);
		}
    }
}
