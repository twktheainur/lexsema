package org.getalp.lexsema.wsd.experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.mit.jwi.Dictionary;
import org.getalp.lexsema.io.dictionary.DocumentDictionaryWriter;
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
import org.getalp.lexsema.similarity.signatures.enrichment.*;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurusImpl;
import org.getalp.lexsema.util.VectorOperation;
import org.getalp.lexsema.util.word2vec.Word2VecClient;

public class DictionaryCreation
{
    public static String wordnetPath = "../data/wordnet/2.1/dict/";

    public static String semCorPath = "../data/semcor2.1/all.xml";

    public static String dsoPath = "../data/dso/";

    public static String semeval2007task7Path = "../data/senseval2007_task7/test/eng-coarse-all-words.xml";

    public static String wordnetGlossTagPath = "../data/wordnet/3.0/glosstag/";

    public static String gmbPath = "../data/gmb-2.2.0/";

    public static String word2vecPath = "../data/word2vec/";

    public static String senseClustersPath = "../data/senseval2007_task7/key/sense_clusters-21.senses";
    
    public static Dictionary wordnet = new Dictionary(new File(wordnetPath));

    public static CorpusLoader semCor = new SemCorCorpusLoader(semCorPath);

    public static CorpusLoader dso = new OldDSOCorpusLoader(dsoPath, wordnetPath);

    public static CorpusLoader wordnetGlossTag = new WordnetGlossTagCorpusLoader(wordnetGlossTagPath);

    public static CorpusLoader gmb = new GMBCorpusLoader(gmbPath, wordnet);

    public static List<List<String>> senseClusters = new ArrayList<>();
    
    public static boolean semCorIsLoaded = false;

    public static boolean dsoIsLoaded = false;

    public static boolean wordnetGlossTagIsLoaded = false;

    public static boolean gmbgIsLoaded = false;

    public static boolean word2vecIsLoaded = false;
    
    public static boolean senseClustersIsLoaded = false;

    public static void main(String[] args) throws Exception
    {
        /*
         * double[] a = Word2VecClient.getWordVector("france");
        double[] b = Word2VecClient.getWordVector("italy");
        double[] c = Word2VecClient.getWordVector("germany");
        double[] d = Word2VecClient.getWordVector("spain");
        double[] e = Word2VecClient.getWordVector("england");
        double[] f = Word2VecClient.getWordVector("continent");
        double[] res = VectorOperation.normalize(VectorOperation.sum(a, b, c, d, e));
        System.out.println(Arrays.toString(Word2VecClient.getMostSimilarWords(res, 10).toArray()));
        System.out.println(Arrays.toString(Word2VecClient.getMostSimilarWords(f, 10).toArray()));
        System.out.println(Arrays.toString(Word2VecClient.getMostSimilarWords(f, 10, res).toArray()));
        */
        
        writeDictionary(false, false, true, true, false, true, false, false, false, true, 50, false, 0, true, false, "../data/lesk_dict/semeval2007task7/gmb_alone_tmp");
        
        //writeDictionary(true, true, true, true, true, false, false, false, false, false, 0, false, 0, true, false, "../data/lesk_dict/semeval2007task7/w2v0");
        //writeDictionary(true, true, true, true, true, true, false, false, false, false, 0, true, 1, true, false, "../data/lesk_dict/semeval2007task7/w2v1");
        //writeDictionary(true, true, true, true, true, true, false, false, false, false, 0, true, 2, true, false, "../data/lesk_dict/semeval2007task7/w2v2");
        //writeDictionary(true, true, true, true, true, true, false, false, false, false, 0, true, 3, true, false, "../data/lesk_dict/semeval2007task7/w2v3");
/*
        for (int i = 1 ; i <= 15 ; i++) {
            for (int j = 50 ; j <= 300 ; j += 50) {
                boolean sc = (i & 1) == 1;
                boolean dso = (i & 2) == 2;
                boolean wngt = (i & 4) == 4;
                boolean gmb = (i & 8) == 8;
                writeDictionary(false, false, true, true, true, true, sc, dso, wngt, gmb, j, false, 0, true, false, "../data/lesk_dict/semeval2007task7/" + i + "/" + j + "_alone");
            }
        }
*/
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
            boolean loadOnlySemeval2007Task7Senses, 
            boolean useSenseClusters,
            String newDictPath) throws Exception
    {    
        System.out.println("Building dictionary " + newDictPath + "...");

        WordnetLoader lrloader = new WordnetLoader(wordnet);
        ArrayList<Text> corpora = new ArrayList<Text>();

        lrloader.loadDefinitions(definitions);
        lrloader.extendedSignature(extendedDefinitions);
        lrloader.loadRelated(extendedDefinitions);
        lrloader.shuffle(shuffle);

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

        if (stopWords)
        {
            lrloader.addSignatureEnrichment(new StopwordsRemovingSignatureEnrichment());
        }
        
        if (useWord2Vec)
        {
            lrloader.addSignatureEnrichment(new Word2VecSignatureEnrichment2(numberOfWordsFromWord2Vec));
        }

        if (stemming)
        {
            lrloader.addSignatureEnrichment(new StemmingSignatureEnrichment());
        }
        
        if (index)
        {
            lrloader.addSignatureEnrichment(new IndexingSignatureEnrichment());
        }
        
        lrloader.addSignatureEnrichment(new VectorizationSignatureEnrichment());
        
        if (useSenseClusters)
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
}
