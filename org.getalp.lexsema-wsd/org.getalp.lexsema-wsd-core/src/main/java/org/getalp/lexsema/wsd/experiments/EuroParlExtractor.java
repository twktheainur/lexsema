package org.getalp.lexsema.wsd.experiments;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dbnary.DBNaryLoaderImpl;
import org.getalp.lexsema.io.text.EnglishDKPTextProcessor;
import org.getalp.lexsema.io.text.FrenchDKPTextProcessor;
import org.getalp.lexsema.io.text.TextProcessor;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaTDBStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.SimulatedAnnealing;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.MatrixConfigurationScorer;
import org.getalp.ml.matrix.score.SumMatrixScorer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class EuroParlExtractor {	
	public static final String ONTOLOGY_PROPERTIES = "data" + File.separatorChar + "ontology.properties";
	Map<String, Integer> sensePair = new HashMap<String, Integer>();
	public static DBNary instantiateDBNary(Language l ) throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		//Store vts = new JenaRemoteSPARQLStore("http://kaiko.getalp.org/sparql");
		Store vts = new JenaTDBStore("/Users/tchechem/wsgetalp/data/dbnary/dbnary_full");
		StoreHandler.registerStoreInstance(vts);
		//StoreHandler.DEBUG_ON = true;
		OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
		// Creating DBNary wrapper
		Language[] langs = {l};
		return (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox,langs);
	}

	public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		LRLoader lrloaderEng = new DBNaryLoaderImpl(instantiateDBNary(Language.ENGLISH),Language.ENGLISH).loadDefinitions(true);
		LRLoader lrloaderFr = new DBNaryLoaderImpl(instantiateDBNary(Language.FRENCH), Language.FRENCH).loadDefinitions(true);

		String engdirectory = "Test/en/";
		String frdirctory = "Test/fr/";
		String fileException = null;
		FormatFile en;
		FormatFile fr;
		List<Chapter> ec;
		List<Chapter> fc;
		File engin;
		File frnch;
		ArrayList<String> englishpath;
		ArrayList<String> frenchpath;
		EuroParlExtractor e = new EuroParlExtractor();
		englishpath = e.findFiles(engdirectory);
		frenchpath = e.findFiles(frdirctory);
		englishpath.retainAll(frenchpath);		
		//try {
		for (int i = 0; i < englishpath.size(); i++) {
			for (int j = 0; j < frenchpath.size(); j++) {
				if (englishpath.get(i).contains(frenchpath.get(j))) {
					en = new FormatFile("en/");
					fr = new FormatFile("fr/");
					System.out.println(englishpath.get(i));
					String den = en.format(
							engdirectory + englishpath.get(i),
							englishpath.get(i));
					String dfr = fr.format(frdirctory + frenchpath.get(j),
							frenchpath.get(j));
					engin = new File(den);
					frnch = new File(dfr);
					Document doc = Jsoup.parse(engin, "UTF-8");
					Document docf = Jsoup.parse(frnch, "UTF-8");
					ec = e.parseDocument(doc.body());
					fc = e.parseDocument(docf.body());
					e.extractSense(ec, fc, lrloaderEng, lrloaderFr);

					break;
				}
			}
		}

		//} catch (Exception p) {
		//	System.out.println(fileException);
		//p.printStackTrace();

		//}

		// File in = new
		// File("/home/bhaskar/workspace/EuroParl/txt/en/EDIT-ep-00-01-17.txt");
		// PrintWriter writer = new
		// PrintWriter("/home/bhaskar/Desktop/Sentence.txt");

		// Document doc = Jsoup.parse(in,"UTF-8");


		// lc = e.parseDocument(doc.body());
		e.sensePairProbability();

	}

	public ArrayList<String> findFiles(String Directory) {
		ArrayList<String> textFiles = new ArrayList<String>();
		File dir = new File(Directory);
		for (File file : dir.listFiles()) {
			if (file.getName().contains(".txt"))
				textFiles.add(file.getName());
		}
		return textFiles;
	}



	public ArrayList<Chapter> parseDocument(Node d) {
		ArrayList<Chapter> result = new ArrayList<Chapter>();
		for (Node n : d.childNodes()) {
			if (n.childNodeSize() == 0)
				continue;
			result.add(parseChapter(n));
		}
		return result;
	}

	public Chapter parseChapter(Node a) {
		List<Speaker> speakers = new ArrayList<Speaker>();
		String chapterIntro = null;

		Node firstChild = a.childNode(0);
		if (firstChild instanceof TextNode) {
			TextNode domIntro = (TextNode) firstChild;
			chapterIntro = domIntro.getWholeText();
		}

		boolean skipOne = chapterIntro != null;

		for (Node s : a.childNodes()) {
			if (skipOne) {
				skipOne = false;
				continue;
			}
			if (s.childNodeSize() == 0)
				continue;
			speakers.add(parseSpeaker(s, a.attr("ID")));
		}

		Chapter result = new Chapter();
		result.setId(a.attr("ID"));
		result.setSpk(speakers);

		return result;
	}

	public Speaker parseSpeaker(Node s, String id) {
		List<Paragraph> paragraphs = new ArrayList<Paragraph>();
		int count = 0;
		for (Node p : s.childNodes()) {
			if (p.childNodeSize() == 0)
				continue;
			paragraphs.add(parseParagraph(p, id + "." + s.attr("ID") + "."
					+ Integer.toString(count)));
			count++;
		}

		Speaker result = new Speaker();
		result.setId(id + "." + s.attr("ID"));
		result.setPar(paragraphs);

		return result;
	}

	public Paragraph parseParagraph(Node p, String id) {
		String text = ((TextNode) p.childNode(0)).getWholeText();
		Paragraph result = new Paragraph();
		result.setId(id);
		result.setSentence(text);
		return result;
	}

	public void extractSense(List<Chapter> e, List<Chapter> p, LRLoader lrLoaderEng, LRLoader lrloaderFr)
			throws InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IOException {
		SimilarityMeasure similarityMeasure;
		similarityMeasure = new TverskiIndexSimilarityMeasureBuilder()
		.distance(new ScaledLevenstein()).computeRatio(true).alpha(1d)
		.beta(0.5d).gamma(0.5d).fuzzyMatching(true).build();
		ConfigurationScorer scorer = new MatrixConfigurationScorer(similarityMeasure,new SumMatrixScorer(),Runtime.getRuntime().availableProcessors());
		//ConfigurationScorer scorer = new ConfigurationScorerWithCache(similarityMeasure);
		Disambiguator disambiguator = new SimulatedAnnealing(0.8,0.8,5,100,scorer);

		TextProcessor sentenceProcessor;
		sentenceProcessor = new EnglishDKPTextProcessor();
		List<String> stre = new ArrayList<>() ;
		Map<String,List<String>> meng = new HashMap<>();
		for (Chapter a : e) {
			for (Speaker s : a.getSpk()) {
				for (Paragraph k : s.getPar()) {

					if (!k.getSentence().trim().isEmpty()) {
						Text sen = sentenceProcessor.process(k.getSentence(),
								k.getId());

						if(sen.size()>1) {
							try {

								lrLoaderEng.loadSenses(sen);

								Configuration c = disambiguator.disambiguate(sen);
								for (int i = 0; i < c.size(); i++) {
									if (!sen.getSenses(i).isEmpty() && c.getAssignment(i) >= 0) {
										Word wl = sen.getWord(0, i);
										sen.getSenses(i).size();
										System.out.println(wl.getLemma());
										stre.add(sen.getSenses(i)
												.get(c.getAssignment(i)).getId());
										System.out.println(sen.getSenses(i)
												.get(c.getAssignment(i)).getId());

									}
									if (!stre.isEmpty()) {
										meng.put(k.getId(), stre);
										stre.clear();
									}
								}


							} catch (Exception l) {
								l.printStackTrace();
							}
						}
					}
				}
			}
		}	
		Map<String,List<String>> mfrnch = new HashMap<>();
		List<String> strf = new ArrayList<String>();
		TextProcessor sentenceProcessorFr;
		sentenceProcessorFr = new FrenchDKPTextProcessor();
		for (Chapter a : p) {
			for (Speaker s : a.getSpk()) {
				for (Paragraph k : s.getPar()) {

					try{
						Text sen = sentenceProcessorFr.process(k.getSentence(),
								k.getId());
						lrloaderFr.loadSenses(sen);
						//						List<Sense> senses = new ArrayList<Sense>();
						//						for(int j= 0; j<sen.size();j++){

						//							senses.add(sen.);
						//						}
						Configuration c = disambiguator.disambiguate(sen);

						for (int i = 0; i < c.size(); i++) 
						{
							if (!sen.getSenses(i).isEmpty() && c.getAssignment(i)>=0) {
								strf.add(sen.getSenses(i)
										.get(c.getAssignment(i)).getId());
								System.out.println(sen.getSenses(i)
										.get(c.getAssignment(i)).getId());
							}
							if(!strf.isEmpty()){
								mfrnch.put(k.getId(), strf);
								strf.clear();
							}
						}

					}catch(Exception l){
						l.printStackTrace();
					}
				}

			}
		}
		compareSenses(meng, mfrnch);

	}

	public void sensePairProbability(){






	}
	public void compareSenses(Map<String , List<String>> meng,Map<String , List<String>> mfr){

		int countSense = 1;
		Iterator<Map.Entry<String,List<String>>> iter = meng.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String,List<String>> entry = iter.next();
			if(mfr.containsKey(entry.getKey())){
				for(String s : entry.getValue()){
					for(String st : mfr.get(entry.getKey())){
						String str = s+","+st; 
						if (sensePair.containsKey(str)){    					   
							sensePair.put(str, sensePair.get(str)+1);
						}else
							sensePair.put(str , countSense);
					}
				}
			}
		}


		System.out.println("FInished");

	} 
}
