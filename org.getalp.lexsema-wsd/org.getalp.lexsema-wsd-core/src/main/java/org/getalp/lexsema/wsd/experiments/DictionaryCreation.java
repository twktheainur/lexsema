package org.getalp.lexsema.wsd.experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import edu.mit.jwi.Dictionary;
import org.getalp.lexsema.io.dictionary.DocumentDictionaryWriter;
import org.getalp.lexsema.io.document.loader.GMBCorpusLoader;
import org.getalp.lexsema.io.document.loader.OldDSOCorpusLoader;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.SemCorCorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.document.loader.WordnetGlossTagCorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.io.text.DicollecteFrenchLemmatizer;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.signatures.enrichment.*;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurusImpl;
import org.getalp.lexsema.util.StopList;
import org.getalp.lexsema.util.VectorOperation;
import org.getalp.lexsema.util.word2vec.Word2VecClient;

public class DictionaryCreation
{
    public static String wordnet21Path = "../data/wordnet/2.1/dict/";

    public static String wordnet30Path = "../data/wordnet/3.0/dict/";

    public static String semcor21Path = "../data/semcor/2.1/all.xml";

    public static String semcor30Path = "../data/semcor/3.0/all.xml";

    public static String semcorfrPath = "../data/semcor/fr/all.xml";

    public static String dsoPath = "../data/dso/";

    public static String semeval2007task7Path = "../data/senseval2007_task7/test/eng-coarse-all-words.xml";

    public static String wordnetGlossTagPath = "../data/wordnet/3.0/glosstag/";

    public static String gmbPath = "../data/gmb-2.2.0/";

    public static String word2vecPath = "../data/word2vec/";

    public static String senseClustersPath = "../data/senseval2007_task7/key/sense_clusters-21.senses";

    public static Dictionary wordnet21 = new Dictionary(new File(wordnet21Path));

    public static Dictionary wordnet30 = new Dictionary(new File(wordnet30Path));

    public static CorpusLoader semCor21 = new SemCorCorpusLoader(semcor21Path);

    public static CorpusLoader semCor30 = new SemCorCorpusLoader(semcor30Path);

    public static CorpusLoader semcorfr = new SemCorCorpusLoader(semcorfrPath);

    public static CorpusLoader dso = new OldDSOCorpusLoader(dsoPath, wordnet21Path);

    public static CorpusLoader wordnetGlossTag = new WordnetGlossTagCorpusLoader(wordnetGlossTagPath);

    public static CorpusLoader gmb = new GMBCorpusLoader(gmbPath, wordnet21);

    public static List<List<String>> senseClusters = new ArrayList<>();
    
    public static boolean semcor21IsLoaded = false;

    public static boolean dsoIsLoaded = false;

    public static boolean wordnetGlossTagIsLoaded = false;

    public static boolean gmbgIsLoaded = false;

    public static boolean senseClustersIsLoaded = false;
    
    
    public boolean withDefinitions = true;
    
    public boolean withExtendedDefinitions = false;
    
    public boolean withStopwords = false;
    
    public boolean withStemming = false;
    
    public boolean withIndexing = false;
    
    public boolean withShuffling = false;
    
    public boolean withSemcorThesaurus = false;
    
    public boolean withDSOThesaurus = false;
    
    public boolean withWNGTThesaurus = false;
    
    public boolean withGMBThesaurus = false;
    
    public int numberOfWordsFromThesauri = 0;
    
    public boolean loadOnlySemeval2007Task7Senses = false;
    
    public boolean withSenseClusters = false;
    
    public boolean withSynsetOffsetInsteadOfSenseKey = false;
    
    public final List<SignatureEnrichment> otherSignatureEnrichments = new ArrayList<>();
    
    public boolean verbose = false;
    
    public DictionaryCreation()
    {
        
    }
    
    public void write(String newDictPath) throws Exception
    {
        System.out.print("Building dictionary " + newDictPath + "...");
        System.out.flush();

        WordnetLoader lrloader = new WordnetLoader(wordnet21);

        lrloader.loadDefinitions(withDefinitions);
        lrloader.extendedSignature(withExtendedDefinitions);
        lrloader.loadRelated(withExtendedDefinitions);
        lrloader.shuffle(withShuffling);
        lrloader.setloadSynsetOffsetInsteadOfSenseKey(withSynsetOffsetInsteadOfSenseKey);
        lrloader.setVerbose(verbose);

        //lrloader.addSignatureEnrichment(new WordnetGlossTagEnrichment(wordnetGlossTagPath));

        ArrayList<Text> corpora = new ArrayList<Text>();
        
        if (withSemcorThesaurus)
        {
            if (!semcor21IsLoaded) 
            {
                semCor21.load();
                semcor21IsLoaded = true;
            }
            for (Text corpusText : semCor21)
            {
                corpora.add(corpusText);
            }
        }

        if (withDSOThesaurus)
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

        if (withWNGTThesaurus)
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

        if (withGMBThesaurus)
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

        //printStats(corpora);

        AnnotatedTextThesaurusImpl thesaurus = new AnnotatedTextThesaurusImpl(corpora, numberOfWordsFromThesauri);
        lrloader.addThesaurus(thesaurus);

        if (withStopwords)
        {
            lrloader.addSignatureEnrichment(new StopwordsRemovingSignatureEnrichment());
        }
        
        for (SignatureEnrichment otherSignatureEnrichment : otherSignatureEnrichments)
        {
        	lrloader.addSignatureEnrichment(otherSignatureEnrichment);
        }
        
        if (withStopwords)
        {
            lrloader.addSignatureEnrichment(new StopwordsRemovingSignatureEnrichment());
        }
        
        if (withStemming)
        {
            lrloader.addSignatureEnrichment(new StemmingSignatureEnrichment());
        }
        
        if (withIndexing)
        {
            lrloader.addSignatureEnrichment(new IndexingSignatureEnrichment());
        }
        
        if (withSenseClusters)
        {
            if (!senseClustersIsLoaded)
            {
                loadSenseClusters();
                senseClustersIsLoaded = true;
            }
            lrloader.setSenseClusters(senseClusters);
        }
        
        if (loadOnlySemeval2007Task7Senses)
        {
            CorpusLoader corpus = new Semeval2007CorpusLoader(new FileInputStream(semeval2007task7Path));
            corpus.load();
            for (Text txt : corpus)
            {
                lrloader.loadSenses(txt);
            }
            DocumentDictionaryWriter writer = new DocumentDictionaryWriter(corpus);
            writer.allowDuplicate(false);
            writer.writeDictionary(new File(newDictPath));
        }
        else
        {
            doWriteFullDictionary(lrloader.getAllSenses(), newDictPath);
        }  
        
        System.out.println(" done");
    }

    private static void doWriteFullDictionary(Map<Word, List<Sense>> senses, String newDictPath) throws Exception
    {
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
    
    private static void loadSenseClusters() throws Exception
    {
        Scanner sc = new Scanner(new File(senseClustersPath));
        while (sc.hasNextLine())
        {
            String line = sc.nextLine();
            String[] tokens = line.split(" ");
            List<String> senses = new ArrayList<>(Arrays.asList(tokens));
            senseClusters.add(senses);
        }
        sc.close();
    }
    private static void printStats(Iterable<Text> texts)
    {
        Set<String> annotatedWordSet = new HashSet<>();
        List<String> annotatedWordList = new ArrayList<>();
        Set<String> annotatedSenseSet = new HashSet<>();
        List<String> annotatedSenseList = new ArrayList<>();
        //Map<String, Integer> counter = new HashMap<>();
        for (Text txt : texts)
        {
            for (Sentence stc : txt.sentences())
            {
                for (Word w : stc)
                {
                    if (w.getLemma() != null && !w.getLemma().equals("") && 
                    	w.getSenseAnnotation() != null && !w.getSenseAnnotation().equals(""))
                    {
                        String wordStr = w.getLemma() + "%" + w.getSenseAnnotation();
                        annotatedWordSet.add(w.getLemma());
                        annotatedWordList.add(w.getLemma());
                        annotatedSenseSet.add(wordStr);
                        annotatedSenseList.add(wordStr);
                        //counter.put(wordStr, counter.get(wordStr) + 1);
                    }
                }
            }
        }
        System.out.println("Number of unique annotated word : " + annotatedWordSet.size());
        System.out.println("Number of annotated word : " + annotatedWordList.size());
        System.out.println("Number of unique annotated sense : " + annotatedSenseSet.size());
        System.out.println("Number of annotated sense : " + annotatedSenseList.size());
        /*
        Integer[] counters = counter.values().toArray(new Integer[counter.values().size()]);
        double mean = 0;
        for (Integer i : counters) mean += i;
        mean /= (double) counters.length;
        System.out.println("Mean number of annotation per sense : " + mean);
        */
    }
    public static void main(String[] args) throws Exception
    {
        DictionaryCreation dict = new DictionaryCreation();
		dict.loadOnlySemeval2007Task7Senses = true;
        dict.withStopwords = true;
        dict.withShuffling = true;
		dict.withSenseClusters = false;
        dict.withDefinitions = true;
        dict.withExtendedDefinitions = true;
        dict.verbose = false;
/*
        dict.withIndexing = false;
        dict.withStemming = false;

        dict.otherSignatureEnrichments.add(new VectorizationSignatureEnrichment());
        dict.write("../data/lesk_dict/semeval2007task7/w2v/vectorized1");
        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new VectorizationSignatureEnrichment2());
        dict.write("../data/lesk_dict/semeval2007task7/w2v/vectorized2");
        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new VectorizationSignatureEnrichment3(0));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/vectorized3_0");

        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new VectorizationSignatureEnrichment3(-0.5));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/vectorized3_-0.5");
        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new VectorizationSignatureEnrichment3(-1));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/vectorized3_-1");

        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new VectorizationSignatureEnrichment3(0.5));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/vectorized3_0.5");
*/
        
        dict.withIndexing = true;
        dict.withStemming = true;
        dict.verbose = true;
/*  
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment2(1));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended1_1");

        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment2(3));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended1_3");

        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment2(5));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended1_5");
*/

        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment2(10));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended1_10");

        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment2(20));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended1_20");

        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment2(50));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended1_50");        
/*
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment3(10));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended2_10");
        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment3(50));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended2_50");
        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment3(100));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended2_100");

        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment32(10, -0.5));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended3_10_-0.5");
        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment32(50, -0.5));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended3_50_-0.5");
        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment32(100, -0.5));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended3_100_-0.5");


        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment32(10, 0));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended3_10_0");
        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment32(50, 0));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended3_50_0");
        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment32(100, 0));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended3_100_0");


        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment32(10, 0.5));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended3_10_0.5");
        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment32(50, 0.5));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended3_50_0.5");
        
        dict.otherSignatureEnrichments.clear();
        dict.otherSignatureEnrichments.add(new Word2VecSignatureEnrichment32(100, 0.5));
        dict.write("../data/lesk_dict/semeval2007task7/w2v/extended3_100_0.5");
*/
        
        /*
        for (int i = 1 ; i <= 15 ; i++) 
        {
        	for (int j = 50 ; j <= 300 ; j += 50) 
        	{
        		dict.withSemcorThesaurus = (i & 1) == 1;
        		dict.withDSOThesaurus = (i & 2) == 2;
        		dict.withWNGTThesaurus = (i & 4) == 4;
        		dict.withGMBThesaurus = (i & 8) == 8;
        		dict.numberOfWordsFromThesauri = j;
        		dict.write("../data/lesk_dict/semeval2007task7/coarse_nodef/" + i + "/" + j);
        	}
        }
		*/
        
        /*
        dict.withSemcorThesaurus = true;
        dict.withWNGTThesaurus = true;
        dict.numberOfWordsFromThesauri = 250;
        
        dict.withSynsetOffsetInsteadOfSenseKey = false;
        dict.withSenseClusters = false;
        dict.write("../data/lil_new");
        
        dict.withSynsetOffsetInsteadOfSenseKey = false;
        dict.withSenseClusters = true;
        dict.write("../data/lil_new_clustered");
        
        dict.withSynsetOffsetInsteadOfSenseKey = true;
        dict.withSenseClusters = true;
        dict.write("../data/lil_new_alt");
        */
        /*
        SemCorCorpusLoader frenchSemcor = new SemCorCorpusLoader("../data/semcor_fr_filtered.xml");
        frenchSemcor.load();
        DicollecteFrenchLemmatizer lemmatizer = new DicollecteFrenchLemmatizer("../data/dicollecte/lexique-dicollecte-fr-v5.6.txt");

        Map<Word, String> realLemmas = new HashMap<>();
        
        for (Document text : frenchSemcor)
        {
            for (Word word : text)
            {
                String realLemma = lemmatizer.getLemma(word.getSurfaceForm());
                if (realLemma != null)
                {
                    realLemmas.put(word, realLemma);
                }
            }
        }
        
        List<String> allZeWords = new ArrayList<>();
        
       
        AnnotatedTextThesaurusImpl thesaurus = new AnnotatedTextThesaurusImpl(frenchSemcor, 100);
        
        File newDict = new File("../data/lesk_dict/all/french");
        FileOutputStream fos = new FileOutputStream(newDict);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("<dict>");
        bw.newLine();
        for (Document text : frenchSemcor)
        {
            for (Word word : text)
            {
                DicollecteFrenchLemmatizer lemmatizer = new DicollecteFrenchLemmatizer("../data/dicollecte/lexique-dicollecte-fr-v5.6.txt");
                String realLemma = lemmatizer.getLemma(word.getSurfaceForm());
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
        */
    }

}
