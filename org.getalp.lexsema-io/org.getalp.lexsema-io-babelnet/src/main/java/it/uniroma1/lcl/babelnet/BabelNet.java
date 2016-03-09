package it.uniroma1.lcl.babelnet;

import it.uniroma1.lcl.babelnet.iterators.BabelLexiconIterator;
import it.uniroma1.lcl.babelnet.iterators.BabelOffsetIterator;
import it.uniroma1.lcl.babelnet.iterators.BabelSynsetIterator;
import it.uniroma1.lcl.jlt.util.IntegerCounter;
import it.uniroma1.lcl.jlt.util.Language;
import it.uniroma1.lcl.jlt.util.ScoredItem;
import it.uniroma1.lcl.jlt.util.Strings;
import it.uniroma1.lcl.jlt.util.Triple;
import it.uniroma1.lcl.jlt.wordnet.WordNet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import edu.mit.jwi.item.POS;

/**
 * A class to programmatically access BabelNet.
 * 
 * @author navigli, ponzetto, vannella
 *
 */
public class BabelNet
{
	private static final Log log = LogFactory.getLog(BabelNet.class);
	
	/**
	 * The only instance of BabelNet 
	 */
	static private BabelNet instance;

	/**
	 * Synset id separator (e.g., bn:00000001, UNR:00000001)
	 */
	public static final String SEPARATOR = ":";
	
	private static final int MAXIMUM_NUMBER_OF_SYNSETS = 500;
	
	private static final boolean USE_REDIRECTION_SENSES =
		BabelNetConfiguration.getInstance().getBabelNetUseRedirectionSenses();
	
	private static final boolean USE_IMAGE_FILTER =
			BabelNetConfiguration.getInstance().isBadImageFilterActive();

	private static final String MISSING_LICENSES = "[MISSING_LICENSES]";
	
	/**
	 * Objects for searching the Lucene index
	 */
	private final IndexSearcher babelnet;
	private final IndexSearcher lexicon;
	private final IndexSearcher dictionary;
	private final IndexSearcher glosses;
	private final IndexSearcher graph;

	/**
	 * Dictionaries (other licenses)
	 */
	private HashMap<BabelLicense, IndexSearcher> licenseToDictionaries = new HashMap<BabelLicense, IndexSearcher>();

	/**
	 * Glosses (other licenses)
	 */
	private HashMap<BabelLicense, IndexSearcher> licenseToGlosses = new HashMap<BabelLicense, IndexSearcher>();

	/**
	 * The private constructor used to initialize the BabelNet indexes
	 * 
	 * @throws IOException
	 */
	private BabelNet() throws IOException
	{
		log.info(BabelAPIInfo.getHeader());
		
		BabelNetConfiguration config = BabelNetConfiguration.getInstance();
		
		String mappingFile = config.getBabelNetMappingIndexDir();
		String lexiconFile = config.getBabelNetLexiconIndexDir();
		String dictionaryFile = config.getBabelNetDictIndexDir();
		String glossFile = config.getBabelNetGlossIndexDir();
		String graphFile = config.getBabelNetGraphIndexDir();
		
		// old version of graph (previous version of the index)

		if (!new File(mappingFile).exists())
			graphFile = graphFile.replace("_"+BabelLicense.CC_BY_NC_SA_30,"");
		
		Directory mappingDir = new SimpleFSDirectory(new File(mappingFile));
		Directory lexiconDir = new SimpleFSDirectory(new File(lexiconFile));
		Directory dictionaryDir = new SimpleFSDirectory(new File(dictionaryFile));
		Directory glossDir = new SimpleFSDirectory(new File(glossFile));
		Directory graphDir = new SimpleFSDirectory(new File(graphFile));
		
		log.info("---------------");
		
		// open the unrestricted indices and keep them open
		log.info("Opening dict index: "+dictionaryFile);
		this.dictionary = new IndexSearcher(dictionaryDir, true);
		licenseToDictionaries.put(BabelLicense.UNRESTRICTED, this.dictionary);
		

		log.info("Opening gloss index: "+glossFile);
		this.glosses = new IndexSearcher(glossDir, true);
		licenseToGlosses.put(BabelLicense.UNRESTRICTED, this.glosses);

		ArrayList<IndexReader> theLexiconIndexList = new ArrayList<IndexReader>(); 

		// add UNRESTRICTED lexicon index
		log.info("Opening lexicon index: "+dictionaryFile);
 		theLexiconIndexList.add(new IndexSearcher(lexiconDir, true).getIndexReader());
		
		for (BabelLicense bl : BabelLicense.values())
		{
			if (bl.equals(BabelLicense.UNRESTRICTED))continue;
			if (new File(lexiconFile+"_"+bl).exists())
			{
				log.info("Opening lexicon index: "+lexiconFile+"_"+bl);
				Directory dirLicense = new SimpleFSDirectory(new File(lexiconFile+"_"+bl));
				IndexSearcher indexLicense = new IndexSearcher(dirLicense, true);
				theLexiconIndexList.add(indexLicense.getIndexReader());
 			}
			
			if (new File(dictionaryFile+"_"+bl).exists())
			{
				log.info("Opening dict index: "+dictionaryFile+"_"+bl);
				Directory dictionaryDirLicense = new SimpleFSDirectory(new File(dictionaryFile+"_"+bl));
				IndexSearcher dictionaryLicense = new IndexSearcher(dictionaryDirLicense, true);
				licenseToDictionaries.put(bl, dictionaryLicense);
			}
			
			if (new File(glossFile+"_"+bl).exists())
			{
				log.info("Opening gloss index: "+glossFile+"_"+bl);
				Directory dirLicense = new SimpleFSDirectory(new File(glossFile+"_"+bl));
				IndexSearcher indexLicense = new IndexSearcher(dirLicense, true);
				licenseToGlosses.put(bl, indexLicense);
			}
		}
		
		log.info("Using BabelNet v"+getVersion());
		
		// if the mapping index does not exist
		if (!new File(mappingFile).exists())
		{
			// it is an old index
			this.babelnet = null;
			// if old index is corrupted
			if (isIndexCorrupted())
			{
				log.fatal("Fatal error: Mapping index missing");
				System.exit(0);
			}
		}
		else {
			log.info("Opening mapping index");
			this.babelnet = new IndexSearcher(mappingDir, true);
		}

		// merge lexicon index
		IndexReader[] lexiconReader =  theLexiconIndexList.toArray(new IndexReader[]{});
		MultiReader multiReader = new MultiReader(lexiconReader);

		this.lexicon = new IndexSearcher(multiReader);
		
		log.info("Opening graph index: "+graphFile);
		this.graph = new IndexSearcher(graphDir, true);
	}

	/**
	 * Used to access {@link BabelNet}
	 * 
	 * @return an instance of {@link BabelNet}
	 */
	public static synchronized BabelNet getInstance()
	{
		try
		{
			if (instance == null) instance = new BabelNet();
			return instance;
		}
		catch (IOException e)
		{
			throw new RuntimeException("Could not init BabelNet: " + e.getMessage());
		}
	}
	
	/**
	 * Returns the version of BabelNet
	 * 
	 * @return version constant
	 */
	public BabelVersion getVersion()
	{
		try
		{
			TopDocs docs = dictionary.search(new MatchAllDocsQuery(BabelNetIndexField.VERSION.toString()), 1);

			if (docs.totalHits == 0) return BabelVersion.PRE_2_0;
			else
			{
			    Document doc = dictionary.doc(docs.scoreDocs[0].doc);
			    String versionString = doc.get(BabelNetIndexField.VERSION.toString());
			    if(versionString == null)return BabelVersion.PRE_2_0;
				return BabelVersion.valueOf(versionString);
			}
		}
		catch(IOException e)
		{
			return BabelVersion.UNKNOWN;
		}
	}
	
	/**
	 * Returns true if the mapping index of BabelNet is missing or corrupted
	 *  
	 * @return boolean
	 */
	public boolean isIndexCorrupted()
	{
		try
		{
			if (!dictionary.doc(10).get(BabelNetIndexField.ID.toString()).startsWith("bn:")) return true;
		}
		catch (IOException e)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Gets a mapping Document from a License ID.
	 * 
	 * @param id
	 *            the License synset ID for a specific concept
	 * 
	 * @return mapping document
	 * @throws IOException
	 */
	private Document getMappingDocumentFromLicenseId(String id) throws IOException
	{
		return getMappingDocument(id, BabelNetIndexField.LICENSE_ID);
	}

	/**
	 * Gets a mapping Document from a BabelNet ID.
	 * 
	 * @param id
	 *            the BabelNet synset ID for a specific concept
	 * 
	 * @return mapping document
	 * @throws IOException
	 */
	private Document getMappingDocumentFromBabelNetId(String id)
			throws IOException
	{
		return getMappingDocument(id, BabelNetIndexField.ID);
	}

	/**
	 * Gets a mapping Document from a License or BabelNet ID. 
	 * 
	 * @param id the input id
	 * @param field the kind of id
	 ***/
	private Document getMappingDocument(String id, BabelNetIndexField field)
			throws IOException
	{
		TermQuery q = new TermQuery(new Term(field.toString(), id));

		// old version
		if (babelnet == null)
		{
			TopDocs docs = dictionary.search(q, 1);
			if (docs.totalHits == 0) return null;

			Document doc = new Document();
			
			doc.add(new Field(BabelNetIndexField.ID.toString(), id, Store.YES,
					Index.NOT_ANALYZED));
			doc.add(new Field(BabelNetIndexField.LICENSE_ID.toString(), id,
					Store.YES, Index.NOT_ANALYZED));
			return doc;
		}

		// interroga l'indice e restituisce il synset se esiste
		TopDocs docs = babelnet.search(q, 1);
		
		// nessun synset trovato
		if (docs.totalHits == 0) return null;

		// restituisce il synset
		Document doc = babelnet.doc(docs.scoreDocs[0].doc);
		return doc;
	}
	
	/**
	 * Gets the list of Documents in all indices from a given query
	 * 
	 * @param q query to obtain the Lucene documents
	 * 
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 * @author vannella, navigli
	 */
	private Multimap<String, Document> getDictionaryDocuments(Query q) throws CorruptIndexException, IOException
	{
		Multimap<String, Document> id2Docs = new HashMultimap<String, Document>();
	
		List<String> documentIds = new ArrayList<String>();
		List<String> retrievedLicenseIds = new ArrayList<String>();
		HashMap<String, String> licenseToBabelNetID = new HashMap<String, String>();

		for (BabelLicense bl : BabelLicense.values())
		{
			if (!licenseToDictionaries.containsKey(bl)) continue;
			
			IndexSearcher dictLicense = licenseToDictionaries.get(bl);
			TopDocs docsLicense = dictLicense.search(q, MAXIMUM_NUMBER_OF_SYNSETS);

			for (ScoreDoc scoreDoc : docsLicense.scoreDocs)
			{
				Document doc = dictLicense.doc(scoreDoc.doc);
				String idLicense = doc.get(BabelNetIndexField.ID.toString());

				// find the other docs by other indexs
				if (!retrievedLicenseIds.contains(idLicense))
				{
					Document mappingDoc = getMappingDocumentFromLicenseId(idLicense);
					String idBabelNet = mappingDoc.get(BabelNetIndexField.ID
							.toString());
					String[] otherLicenseIds = mappingDoc.getValues(BabelNetIndexField.LICENSE_ID.toString());
					for (String otherID : otherLicenseIds)
					{
						licenseToBabelNetID.put(otherID, idBabelNet);
						documentIds.add(otherID);
						retrievedLicenseIds.add(otherID);
					}
				}
				
				String babelNetID = licenseToBabelNetID.get(idLicense);
				documentIds.remove(idLicense);
				// remove id_license
				doc.removeField(BabelNetIndexField.ID.toString());
				// add id_license with id_babelnet
				doc.add(new Field(BabelNetIndexField.ID.toString(), babelNetID,
						Store.YES, Index.NOT_ANALYZED));

				// associates a document with the babelnet ID
				id2Docs.put(babelNetID, doc);
			}
		}
		
		for (String id : documentIds)
		{
			BabelLicense bl = BabelLicense.getLongName(id.split(":")[0]);
			IndexSearcher dictLicense = null;
			if (licenseToDictionaries.containsKey(bl))
				dictLicense = licenseToDictionaries.get(bl);
			else
				continue;
			Document doc = getDocFromIndexById(id, dictLicense);

			if (doc == null)
				throw new RuntimeException("Fatal error: index does not contain: " + id);

			// remove id_license
			doc.removeField(BabelNetIndexField.ID.toString());
			// add id_license with id_babelnet
			String babelNetID = licenseToBabelNetID.get(id);
			doc.add(new Field(BabelNetIndexField.ID.toString(),
					babelNetID, Store.YES, Index.NOT_ANALYZED));
			id2Docs.put(babelNetID, doc);
		}

		return id2Docs;
	}
	
	/**
	 * Gets the list of Documents by mapping ID
	 * 
	 * @param babelNetID BabelNet ID
	 * @param ids License IDs of the various synset pieces
	 * 
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 * @author vannella
	 */
	private List<Document> getDictionaryDocs(String babelNetID, String[] ids) throws CorruptIndexException, IOException
	{
		List<Document> docs = new ArrayList<Document>();
		// all id babelnet in other license
		for (String id : ids)
		{
			BabelLicense bl = BabelLicense.getLongName(id.split(":")[0]);
			if (!licenseToDictionaries.containsKey(bl)) continue;
			IndexSearcher dictLicense = licenseToDictionaries.get(bl);
			Document doc = getDocFromIndexById(id, dictLicense);
 			// remove id_license
			doc.removeField(BabelNetIndexField.ID.toString());
			// add id_license with id_babelnet
			doc.add(new Field(BabelNetIndexField.ID.toString(),
						babelNetID, Store.YES,
						Index.NOT_ANALYZED));

			docs.add(doc);
		}
		return docs;
	}
	/**
	 *  merge glosses
	 * 
	 * @throws IOException 
	 * @throws CorruptIndexException
	 * @author vannella
	 */
	private Document mergeGlossDocument(String babelNetID, Document doc, List<Document> docs) throws CorruptIndexException, IOException
	{
		doc.removeField(BabelNetIndexField.ID.toString());
		doc.add(new Field(BabelNetIndexField.ID.toString(), babelNetID,	Store.YES, Index.NOT_ANALYZED));
		for (Document docLicense : docs)
		{
			String[] storedGlosses = docLicense.getValues(BabelNetIndexField.GLOSS.toString());
			for (int k = 0; k < storedGlosses.length; k++)
			{
				doc.add(new Field(BabelNetIndexField.GLOSS.toString(), storedGlosses[k], Store.YES, Index.NO));
			}
		}
		
		return doc;
	}
	
	/**
	 * 
	 * Merge all Lucene dictionary Documents
	 * 
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 * @author vannella
	 */
	private Document mergeDictionaryDocuments(Collection<Document> docsColl) throws CorruptIndexException, IOException
	{
		ArrayList<Document> docs = new ArrayList<Document>(docsColl);
		Document doc = docs.remove(0);
		
		String id = doc.get(BabelNetIndexField.ID.toString());
		HashMap<String, Integer> newPositionTraslated = new HashMap<String, Integer>();

		// mainLicense starts by 0 
		BabelLicense mainLicense = BabelSenseSource.getLicense(BabelSenseSource
				.valueOf(doc.get(BabelNetIndexField.LEMMA_SOURCE
						.toString())), Language.valueOf(doc
				.get(BabelNetIndexField.LEMMA_LANGUAGE.toString())));
		newPositionTraslated.put(mainLicense.getShortName(), 0);
		
		for (Document docLicense : docs)
		{
			String idLicense = docLicense.get(BabelNetIndexField.ID.toString());

			// we combine only the docs with same id
			if(!idLicense.equals(id)) continue;
			
			BabelLicense bl = BabelSenseSource.getLicense(BabelSenseSource.valueOf(docLicense.get(BabelNetIndexField.LEMMA_SOURCE.toString())), Language.valueOf(docLicense.get(BabelNetIndexField.LEMMA_LANGUAGE.toString())));
		    String mainSense = doc.get(BabelNetIndexField.MAIN_SENSE.toString());
		    // merge main sense
		    if (mainSense.isEmpty() || bl.compareTo(mainLicense) < 0)  
		    {
				mainLicense = bl;
				doc.removeFields(BabelNetIndexField.MAIN_SENSE.toString());
				doc.add(new Field(BabelNetIndexField.MAIN_SENSE.toString(),docLicense.get(BabelNetIndexField.MAIN_SENSE.toString()), Store.YES, Index.NO));
				doc.removeFields(BabelNetIndexField.WORDNET_OFFSET.toString());
				
				List<String> wnOffsets = Arrays.asList(docLicense.getValues(BabelNetIndexField.WORDNET_OFFSET.toString()));
				if(wnOffsets.size() == 1 && wnOffsets.equals("-"))
					// empty offset
					doc.add(new Field(BabelNetIndexField.WORDNET_OFFSET.toString(),"", Store.YES, Index.NOT_ANALYZED));
				else
					for (String wordnetOffset : wnOffsets)
						doc.add(new Field(BabelNetIndexField.WORDNET_OFFSET.toString(), wordnetOffset, Store.YES, Index.NOT_ANALYZED));
				
		    }
		    
		    // numero sense in doc
		    int numSenses = doc.getValues(BabelNetIndexField.LEMMA.toString()).length;
		    newPositionTraslated.put(bl.getShortName(),numSenses);
		    
		    // merge sense
		    String[] languageLemmas = docLicense.getValues(BabelNetIndexField.LANGUAGE_LEMMA.toString());
		    String[] lemmas = docLicense.getValues(BabelNetIndexField.LEMMA.toString());
		    String[] lemmaSources = docLicense.getValues(BabelNetIndexField.LEMMA_SOURCE.toString());
		    String[] lemmaLanguages = docLicense.getValues(BabelNetIndexField.LEMMA_LANGUAGE.toString());
		    String[] lemmaWeights = docLicense.getValues(BabelNetIndexField.LEMMA_WEIGHT.toString());
		    String[] lemmaSensekeys = docLicense.getValues(BabelNetIndexField.LEMMA_SENSEKEY.toString());
		    
		    for (int k = 0; k < lemmas.length; k++)
		    {
		    	doc.add(new Field(BabelNetIndexField.LANGUAGE_LEMMA.toString(),
		    			languageLemmas[k], Store.YES, Index.NOT_ANALYZED));
				// LEMMA => indexed
				doc.add(new Field(BabelNetIndexField.LEMMA.toString(),
						lemmas[k], Store.YES, Index.NOT_ANALYZED));
				// LEMMA LOWER => indexed
				String lemmaToLowerCase = lemmas[k].toLowerCase();
				doc.add(new Field(BabelNetIndexField.LEMMA_TOLOWERCASE.toString(),
						lemmaToLowerCase, Store.YES, Index.NOT_ANALYZED));
				// LEMMA LANG => indexed
				doc.add(new Field(BabelNetIndexField.LEMMA_LANGUAGE.toString(),
						lemmaLanguages[k], Store.YES, Index.NOT_ANALYZED));
				// LEMMA WEIGHT => not indexed
				doc.add(new Field(BabelNetIndexField.LEMMA_WEIGHT.toString(),
						lemmaWeights[k], Store.YES, Index.NOT_ANALYZED));
				// SENSE SOURCE => not indexed
				doc.add(new Field(BabelNetIndexField.LEMMA_SOURCE.toString(),
						lemmaSources[k], Store.YES, Index.NO));
				// WN SENSEKEY => indexed
				doc.add(new Field(BabelNetIndexField.LEMMA_SENSEKEY.toString(),
						lemmaSensekeys[k], Store.YES, Index.NOT_ANALYZED));
		    }
		    
		    // traduzioni docLicense
		    List<String> translationMappings = Arrays.asList(docLicense.getValues(BabelNetIndexField.TRANSLATION_MAPPING.toString()));
		    for(String tr : translationMappings)
		    {
		    	String[] elements = tr.split("_");
		    	String newTraslation = elements[0]+"_"+elements[1]+"_";
		    	List<Integer> pox = new ArrayList<Integer>();
		    	for(String position : elements[2].split(",")){
		    		pox.add(Integer.parseInt(position)+numSenses);
		    	}
		    	newTraslation += pox.toString().replace("[","").replace("]","").replace(" ","");
		    	doc.add(new Field(BabelNetIndexField.TRANSLATION_MAPPING.toString(),
		    			newTraslation, Store.YES, Index.NO));
		    }
		    
		    // merge image
		    String[] imageNamesLicense = docLicense.getValues(BabelNetIndexField.IMAGE.toString());
			for (String image : imageNamesLicense)
			{
				// IMAGE => not indexed
				doc.add(new Field(BabelNetIndexField.IMAGE.toString(),
						image, Store.YES, Index.NO));
			}
			//merge categorie
		    String[] categoreNamesLicense = docLicense.getValues(BabelNetIndexField.CATEGORY.toString());

		    for (String category : categoreNamesLicense)
			{
				// CATEGORY => not indexed
				doc.add(new Field(BabelNetIndexField.CATEGORY.toString(),
						category, Store.YES, Index.NO));
			}
		}

		//     pulitura iniziale delle traduzioni, rimuovere i shortLicense e
		// 	   modificare la posizione della radice
	    //	   old: CBS30_9_10,14,15,17,20,24,28,30,31,33,35,..
	    //	   new: CBS30_9_217,221,222,224,227,231,235,237,23..
	    List<String> translationMappings = Arrays.asList(doc.getValues(BabelNetIndexField.TRANSLATION_MAPPING.toString()));
	    doc.removeFields(BabelNetIndexField.TRANSLATION_MAPPING.toString());
		for (String tr : translationMappings)
		{
			String[] elements = tr.split("_");
		   	String newTraslation = "";
		   	if(elements.length > 2)
		   	{	
		   		int pox = newPositionTraslated.get(elements[0])+Integer.parseInt(elements[1]);
		   		newTraslation = pox+"_"+elements[2];
		   	}
		   	else
		   	{
		   		newTraslation = tr;
		   	}
		   	
		   	doc.add(new Field(BabelNetIndexField.TRANSLATION_MAPPING.toString(),
		   			newTraslation, Store.YES, Index.NO));
		}
		
		return doc;
	}
	
	/**
	 * Given a word, returns the senses for the word. Assumes the word is a
	 * noun.
	 * 
	 * @param language
	 *            the language of the input word.
	 * @param word
	 *            the word whose senses are to be retrieved.
	 * @return the senses of the word.
	 * 
	 */
	public List<BabelSense> getSenses(Language language, String word) throws IOException
	{
		return getSenses(language, word, POS.NOUN);
	}

	/**
	 * Given a word, returns the senses for the word.
	 * 
	 * @param language
	 *            the language of the input word.
	 * @param word
	 *            the word whose senses are to be retrieved.
	 * @param pos
	 *            the PoS of the word.
	 * @return the senses of the word.
	 * 
	 */
	public List<BabelSense> getSenses(Language language, String word, POS pos) throws IOException
	{
		return getSenses(language, word, pos, USE_REDIRECTION_SENSES);
	}
	
	/**
	 * Given a word, returns the senses for the word.
	 * 
	 * @param language
	 *            the language of the input word.
	 * @param word
	 *            the word whose senses are to be retrieved.
	 * @param pos
	 *            the PoS of the word.
	 * @param allowedSources
	 *            the {@link BabelSenseSource}s that can be used to look up
	 *            the possible senses of the input word.
	 * @return the senses of the word.
	 * 
	 */
	public List<BabelSense> getSenses(Language language, String word, POS pos,
									  BabelSenseSource... allowedSources) throws IOException
	{
		return getSenses(language, word, pos, USE_REDIRECTION_SENSES, allowedSources);
	}
	/**
	 * Given a word, returns the senses for the word.
	 * 
	 * @param language
	 *            the language of the input word.
	 * @param word
	 *            the word whose senses are to be retrieved.
	 * @param pos
	 *            the PoS of the word.
	 * @param includeRedirections
	 *            whether to include synsets senses of the word
	 *            which are redirections in Wikipedia.
	 * @param allowedSources
	 *            the {@link BabelSenseSource}s that can be used to look up
	 *            the possible senses of the input word.
	 * @return the senses of the word.
	 * 
	 */
	public List<BabelSense> getSenses(Language language, String word,
									  POS pos, boolean includeRedirections,
									  BabelSenseSource... allowedSources) throws IOException
	{
		word = word.replace(" ", "_");
		String langWord = 
			new StringBuffer(language.toString()).
				append(SEPARATOR).append(word.toLowerCase()).toString();
		
		BooleanQuery q = new BooleanQuery();
		q.add(new BooleanClause(new TermQuery(
				new Term(BabelNetIndexField.LANGUAGE_LEMMA.toString(), 
						langWord)),
						Occur.MUST));
		if (pos != null)
			q.add(new BooleanClause(new TermQuery(
				new Term(BabelNetIndexField.POS.toString(), 
						Character.toString(pos.getTag()))), 
						Occur.MUST));
		
		Multimap<String, Document> id2Docs = getDictionaryDocuments(q);

		List<BabelSense> senses = new ArrayList<BabelSense>();
		for (String id : id2Docs.keySet())
		{
		    Document doc = mergeDictionaryDocuments(id2Docs.get(id));
		    BabelSynset babelSynset = getSynsetFromAllDocuments(doc);
		    List<BabelSense> allSenses = babelSynset.getSenses(language, word);
		    List<BabelSenseSource> allowedSourceList = Arrays.asList(allowedSources);

		    for (BabelSense sense : allSenses)
		    {
		    	// check for specific sources we are interested in
		    	if (!allowedSourceList.isEmpty() && !allowedSourceList.contains(sense.getSource()))
		    		continue;
		    		
		    	// check whether we need to consider senses of redirections
		    	if (includeRedirections || sense.getSource() != BabelSenseSource.WIKIRED)
		    		senses.add(sense);
		    }
		}
	
		return senses;
	}

	/**
	 * Given a word, returns the senses for the word. Assumes the word is a
	 * noun.
	 * 
	 * @param language
	 *            the language of the input word.
	 * @param word
	 *            the word whose senses are to be retrieved.
	 * @return the senses of the word.
	 * 
	 */
	public List<BabelSynset> getSynsets(Language language, String word) throws IOException
	{
		return getSynsets(language, word, POS.NOUN);
	}

	/**
	 * Given a word, returns the senses for the word.
	 * 
	 * @param language
	 *            the language of the input word.
	 * @param word
	 *            the word whose senses are to be retrieved.
	 * @param pos
	 *            the PoS of the word.
	 * @return the senses of the word.
	 * 
	 */
	public List<BabelSynset> getSynsets(Language language, String word, POS pos) throws IOException
	{
		return getSynsets(language, word, pos, USE_REDIRECTION_SENSES);
	}
	
	/**
	 * Given a word, returns the senses for the word.
	 * 
	 * @param language
	 *            the language of the input word.
	 * @param word
	 *            the word whose senses are to be retrieved.
	 * @param pos
	 *            the PoS of the word.
	 * @param allowedSources
	 *            the {@link BabelSenseSource}s that can be used to look up
	 *            the possible senses of the input word.
	 * @return the senses of the word.
	 * 
	 */
	public List<BabelSynset> getSynsets(Language language, String word, POS pos,
										BabelSenseSource... allowedSources) throws IOException
	{
		return getSynsets(language, word, pos, USE_REDIRECTION_SENSES, allowedSources);
	}
	
	/**
	 * Given a word, returns the senses for the word.
	 * 
	 * @param language
	 *            the language of the input word.
	 * @param word
	 *            the word whose senses are to be retrieved.
	 * @param pos
	 *            the PoS of the word.
	 * @param includeRedirections
	 *            whether to include synsets with senses of the word which are
	 *            only redirections in Wikipedia.
	 * @param allowedSources
	 *            the {@link BabelSenseSource}s that can be used to look up the
	 *            possible senses of the input word.
	 * @return the senses of the word.
	 */
	public List<BabelSynset> getSynsets(Language language, String word,
										POS pos, boolean includeRedirections,
										BabelSenseSource... allowedSources) throws IOException
	{
		word = word.replace(" ", "_");
		String langWord = 
			new StringBuffer(language.toString()).
				append(SEPARATOR).append(word.toLowerCase()).toString();
		
		BooleanQuery q = new BooleanQuery();
		q.add(new BooleanClause(new TermQuery(
				new Term(BabelNetIndexField.LANGUAGE_LEMMA.toString(), 
						langWord)), 
						Occur.MUST));
		if (pos != null)
			q.add(new BooleanClause(new TermQuery(
				new Term(BabelNetIndexField.POS.toString(), 
						Character.toString(pos.getTag()))), 
						Occur.MUST));
		
		Multimap<String, Document> id2Docs = getDictionaryDocuments(q);
		
	    List<BabelSynset> synsets = new ArrayList<BabelSynset>();
		for (String id : id2Docs.keySet())
		{
		    Document doc = mergeDictionaryDocuments(id2Docs.get(id));
		    BabelSynset babelSynset = getSynsetFromAllDocuments(doc);
		    synsets.add(babelSynset);
		}
		
		// check whether to include specific types of senses
		if (allowedSources.length > 0)
		{
			Set<BabelSynset> remove = new HashSet<BabelSynset>();
			List<BabelSenseSource> allowedSourceList = Arrays.asList(allowedSources);
			
			for (BabelSynset synset : synsets)
			{
				boolean toRemove = true;
				List<BabelSense> senses = synset.getSenses(language, word);
				senses.addAll(synset.getSenses(language, word.replace("_"," ")));
				for (BabelSense sense : senses)
				{
					BabelSenseSource source = sense.getSource();
					if (allowedSourceList.contains(source))
					{
						toRemove = false;
						break;
					}
				}
				if (toRemove) remove.add(synset);
			}
			synsets.removeAll(remove);
		}
		
		// check whether to include redirection senses
		if (!includeRedirections)
		{
			// do not consider if it comes only from a redirection
			Set<BabelSynset> remove = new HashSet<BabelSynset>();
			for (BabelSynset synset : synsets)
			{
				Set<BabelSenseSource> sources = new HashSet<BabelSenseSource>();
				List<BabelSense> senses = synset.getSenses(language, word);
				senses.addAll(synset.getSenses(language, word.replace("_"," ")));
				for (BabelSense sense : senses)
				{
					BabelSenseSource source = sense.getSource();
					switch (source)
					{
						case WIKIRED:
							// counts as a redirection only if it has a
							// sense label
							String lemma = sense.getLemma();
							String simpleLemma = sense.getSimpleLemma();
							if (!simpleLemma.equalsIgnoreCase(lemma))
								sources.add(sense.getSource());
							break;

						default:
							sources.add(sense.getSource());
							break;
					}
				}
				if (sources.size() == 1 && sources.iterator().next() == BabelSenseSource.WIKIRED)
					remove.add(synset);
			}
			synsets.removeAll(remove);
		}
		
		return synsets;
	}

	/**
	 * Gets a index-document lucene identifier (Babel
	 * synset ID).
	 * 
	 * @param id
	 *            the Babel synset ID for a specific concept
	 * @param dictIndex
	 *            IndexSearcher 
	 *            
	 * @return document with the input id 
	 * @throws IOException
	 */
	private Document getDocFromIndexById(String id,IndexSearcher dictIndex) throws IOException
	{
		TermQuery q = new TermQuery(new Term(BabelNetIndexField.ID.toString(), id));

		// interroga l'indice e restituisce il synset se esiste
		TopDocs docs = dictIndex.search(q, 1);
		
		// nessun synset trovato
		if (docs.totalHits == 0) return null;
		
		// restituisce il synset
		Document doc = dictIndex.doc(docs.scoreDocs[0].doc);
		return doc;
	}
	
	/**
	 * Gets a full-fledged {@link BabelSynset} from a concept identifier (Babel
	 * synset ID).
	 * 
	 * @param id
	 *            the Babel synset ID for a specific concept
	 * @return an instance of a {@link BabelSynset} from a concept ID
	 * @throws IOException
	 */
	public BabelSynset getSynsetFromId(String id) throws IOException
	{
		Document mappingDoc = getMappingDocumentFromBabelNetId(id);
		if (mappingDoc == null) return null;
		String[] licenseIds = mappingDoc.getValues(BabelNetIndexField.LICENSE_ID.toString());
	
		List<Document> allDocs = getDictionaryDocs(id, licenseIds);

		// missing license
		if (allDocs.size() == 0 && licenseIds.length > 0)
			return getSynsetFromEmptyDocument(id);
		
		Document doc = mergeDictionaryDocuments(allDocs);

		// return synset
		return getSynsetFromAllDocuments(doc);
	}
	
	/**
	 *            
	 * @return an instance of a {@link BabelSynset} empty
	 */
	private BabelSynset getSynsetFromEmptyDocument(String id)
	{
	    BabelSynsetSource synsetSource = null;

	    String mainSense = MISSING_LICENSES;
	    
	    POS pos = WordNet.getPOSfromChar(Strings.lastCharOf(id));
	    List<String> wnOffsets = new ArrayList<String>(); 
	    List<String> translationMappings =  new ArrayList<String>();
	    List<BabelImage> images = new ArrayList<BabelImage>();
	    List<BabelCategory> categories = new ArrayList<BabelCategory>();
	    
	    List<BabelSense> synsetSenses = new ArrayList<BabelSense>();
	    BabelSynset babelSynset = 
	    	new BabelSynset(
	    			id, 
	    			pos,
	    			synsetSource,
	    			wnOffsets,
	    			synsetSenses,
	    			translationMappings,
	    			images,
	    			categories,
	    			mainSense,
	    			BabelSynsetType.UNKNOWN);
	    return babelSynset;
	}
	
	/**
	 * Gets a full-fledged {@link BabelSynset} from a {@link Document}
	 * 
	 * @param doc
	 *            a Lucene {@link Document} record for a certain Babel synset
	 * @return an instance of a {@link BabelSynset} from a {@link Document}
	 */
	public static BabelSynset getSynsetFromDocument(Document doc)
	{
	    try
	    {
	    	return BabelNet.getInstance().getSynsetFromId(doc.get(BabelNetIndexField.ID.toString()));
		}
	    catch (IOException e)
	    {
			e.printStackTrace();
		}
	    
	    return null;
	}

	/**
	 * @deprecated use getSynsetFromDocument
	 */
	public static BabelSynset getBabelSynsetFromDocument(Document doc)
	{
		return getSynsetFromDocument(doc);
	}
		
	/**
	 * Gets a full-fledged {@link BabelSynset} from a {@link Document}
	 * 
	 * @param doc
	 *            a Lucene {@link Document} record for a certain Babel synset
	 *            
	 * @return an instance of a {@link BabelSynset} from a {@link Document}
	 */
	private BabelSynset getSynsetFromAllDocuments(Document doc)
	{
 	    String id = doc.get(BabelNetIndexField.ID.toString());
	    BabelSynsetSource synsetSource =
	    	BabelSynsetSource.valueOf(doc.get(BabelNetIndexField.SOURCE.toString()));
	    BabelSynsetType synsetType =
	    	BabelSynsetType.valueOf(doc.get(BabelNetIndexField.TYPE.toString()));
	    String mainSense = doc.get(BabelNetIndexField.MAIN_SENSE.toString());
	    POS pos =
	    	WordNet.getPOSfromChar(
	    		doc.get(BabelNetIndexField.POS.toString()));
	    List<String> wnOffsets =
	    	Arrays.asList(doc.getValues(BabelNetIndexField.WORDNET_OFFSET.toString()));
	    List<String> translationMappings =
    		Arrays.asList(doc.getValues(BabelNetIndexField.TRANSLATION_MAPPING.toString()));

	    String[] imageNames = doc.getValues(BabelNetIndexField.IMAGE.toString());
	    List<BabelImage> images = new ArrayList<BabelImage>();
	    for (String imageName : imageNames)
	    {
	    	BabelImage babelImage = new BabelImage(imageName);
	    	if (!USE_IMAGE_FILTER || !babelImage.isBadImage())
	    		images.add(babelImage);
	    }
	    
	    String[] categoryNames = doc.getValues(BabelNetIndexField.CATEGORY.toString());
	    List<BabelCategory> categories = new ArrayList<BabelCategory>();
	    for (String categoryName : categoryNames)
	    	categories.add(BabelCategory.fromString(categoryName));
	    
	    String[] lemmas = doc.getValues(BabelNetIndexField.LEMMA.toString());
	    String[] lemmaSources = doc.getValues(BabelNetIndexField.LEMMA_SOURCE.toString());
	    String[] lemmaLanguages = doc.getValues(BabelNetIndexField.LEMMA_LANGUAGE.toString());
	    String[] lemmaWeights = doc.getValues(BabelNetIndexField.LEMMA_WEIGHT.toString());
	    String[] lemmaSensekeys = doc.getValues(BabelNetIndexField.LEMMA_SENSEKEY.toString());
	    
	    List<BabelSense> synsetSenses = new ArrayList<BabelSense>();
	    BabelSynset babelSynset = 
	    	new BabelSynset(
	    			id, 
	    			pos,
	    			synsetSource,
	    			wnOffsets,
	    			synsetSenses,
	    			translationMappings,
	    			images,
	    			categories,
	    			mainSense,
	    			synsetType);
	    
	    for (int k = 0; k < lemmas.length; k++)
	    {
	    	String lemma = lemmas[k];
	    	Language lemmaLanguage = Language.valueOf(lemmaLanguages[k]);
	    	String lemmaWeight = lemmaWeights[k];
	    	BabelSenseSource lemmaSource = BabelSenseSource.valueOf(lemmaSources[k]);
	    	String lemmaSenseskey = lemmaSensekeys[k];
	    	String[] senseOffsetTriples = lemmaSenseskey.split("\t");
	    	
	    	for(int j = 0; j < senseOffsetTriples.length; j += 3)
			{
	    		String lemmaSensekey = senseOffsetTriples[j];
	    		String wordnetOffset = senseOffsetTriples.length == 1 ? null : senseOffsetTriples[j+1];
	    		
	    		// note: position within the WordNet synset is in hex format (always "1" for Wikipedia titles)
	    		String position = senseOffsetTriples.length == 1 ? "1" : senseOffsetTriples[j+2];
	    		
	    		BabelSense sense = 
					new BabelSense(
							lemmaLanguage,
							lemma,
							pos,
							lemmaSource,
							lemmaSensekey,
							wordnetOffset,
							Integer.valueOf(String.valueOf(position), 16),
							lemmaWeight,
							babelSynset);
			
	    		synsetSenses.add(sense);
			}
	    }
	    
	    return babelSynset;
	}

	/**
	 * @deprecated use getSynsetsFromWordNetOffset
	 */
	public List<BabelSynset> getBabelSynsetsFromWordNetOffset(String offset) throws IOException
	{
		return getSynsetsFromWordNetOffset(offset);
	}
	
	/**
	 * Get the {@link BabelSynset}s corresponding to an input WordNet offset
	 * 
	 * @param offset
	 *            a WordNet offset
	 * @return a {@link List} of {@link BabelSynset}s corresponding to the input
	 *         WordNet offset
	 * @throws IOException
	 */
	public List<BabelSynset> getSynsetsFromWordNetOffset(String offset) throws IOException
	{
		TermQuery q = new TermQuery(new Term(BabelNetIndexField.WORDNET_OFFSET.toString(), offset));
		
		// interroga l'indice e restituisce il synset se esiste
		TopDocs docs = dictionary.search(q, MAXIMUM_NUMBER_OF_SYNSETS);

		// nessun synset trovato
		if (docs.totalHits == 0) return null;
		
		// restituisce i synset
	    List<BabelSynset> synsets = new ArrayList<BabelSynset>();
	    
		for (ScoreDoc scoreDoc : docs.scoreDocs)
		{
			
		    Document doc = getMappingDocumentFromLicenseId(dictionary.doc(scoreDoc.doc).get(BabelNetIndexField.ID.toString()));

		    //recupera tutte le info dagli altri indici
		    BabelSynset babelSynset = getBabelSynsetFromDocument(doc);
		    synsets.add(babelSynset);
		}
		
		return synsets;
	}
	
	/**
	 * @deprecated Use getSynsetsFromWikipediaTitle
	 */
	public List<BabelSynset> getBabelSynsetsFromWikipediaTitle(Language language, String title,
			POS pos) throws IOException
	{
		return getSynsetsFromWikipediaTitle(language, title, pos);
	}
	
	/**
	 * Given a Wikipedia title, returns its {@link BabelSynset}s.
	 * 
	 * @param language
	 *            the language of the input Wikipedia title.
	 * @param title
	 *            the language of the Wikipedia page.
	 * @param pos
	 *            the PoS of the Wikipedia title.
	 * @return the set of {@link BabelSynset}s associated to the given Wikipedia title
	 */
	public List<BabelSynset> getSynsetsFromWikipediaTitle(Language language, String title,
			POS pos) throws IOException
	{
		title = title.replaceAll(" ", "_");
		
		BooleanQuery q = new BooleanQuery();
		q.add(new BooleanClause(new TermQuery(new Term(BabelNetIndexField.LEMMA.toString(), title)), Occur.MUST));
		
		if (pos != null)
			q.add(new BooleanClause(new TermQuery(new Term(BabelNetIndexField.POS.toString(),Character.toString(pos.getTag()))), Occur.MUST));

		Multimap<String, Document> id2Docs = getDictionaryDocuments(q);
		
		List<BabelSynset> synsets = new ArrayList<BabelSynset>();
		for (String id : id2Docs.keySet())
		{
		    Document doc = mergeDictionaryDocuments(id2Docs.get(id));
			Field[] lemmas = doc.getFields(BabelNetIndexField.LEMMA.toString());
			Field[] lemmasLanguages = doc.getFields(BabelNetIndexField.LEMMA_LANGUAGE.toString());
			Field[] lemmasSource = doc.getFields(BabelNetIndexField.LEMMA_SOURCE.toString());
			
			for (int i = 0; i < lemmas.length; i++)
				if (Language.valueOf(lemmasLanguages[i].stringValue()) == language 
						&& lemmas[i].stringValue().equals(title) 
						&& BabelSenseSource.valueOf(lemmasSource[i].stringValue()).isFromWikipedia())
					synsets.add(getSynsetFromAllDocuments(doc));
		}
		
		return synsets;
	}

	/**
	 * Given a Babel id, collects the successors of the concept denoted by the
	 * id
	 * 
	 * @param concept
	 *            a concept identifier (babel synset ID)
	 * @return a stringified representation of the edges departing from the
	 *         Babel synset denoted by the input id
	 * @throws IOException
	 */
	public List<String> getSuccessors(String concept) throws IOException
	{
		TermQuery q = new TermQuery(new Term(BabelNetIndexField.ID.toString(), concept));
		
		TopDocs docs = graph.search(q, 1);
		Document doc = graph.doc(docs.scoreDocs[0].doc);
		
		String successors = doc.get(BabelNetIndexField.RELATION.toString());
		
		return Arrays.asList(successors.split("\t"));
	}
	
	/**
	 * Given a Babel id, collects the successor {@link BabelNetGraphEdge} of the
	 * concept denoted by the id
	 * 
	 * @param concept
	 *            a concept identifier (babel synset ID)
	 * @return the edges departing from the Babel synset denoted by the input id
	 * @throws IOException
	 */
	public List<BabelNetGraphEdge> getSuccessorEdges(String concept) throws IOException
	{
		List<BabelNetGraphEdge> related = new ArrayList<BabelNetGraphEdge>();
		List<String> relatedEdgeStrings = getSuccessors(concept);
		
		for (String relatedEdgeString : relatedEdgeStrings)
		{
			if (relatedEdgeString.isEmpty()) continue;
			BabelNetGraphEdge edge = BabelNetGraphEdge.fromString(relatedEdgeString);
			related.add(edge);
		}
		
		return related;
	}

	/**
	 * Gets translations of an input word.
	 * 
	 * @param language
	 *            the language of the input word.
	 * @param word
	 *            the word whose senses are to be retrieved.
	 * @return the translations of the input words in different languages, each
	 *         weighted by the number of times the input word was translated as
	 *         such
	 * @throws IOException
	 */
	public Multimap<Language, ScoredItem<String>> getTranslations(
			Language language, String word) throws IOException
	{
		Map<Language, IntegerCounter<String>> traslationCounters =
				new HashMap<Language, IntegerCounter<String>>();
		
		List<BabelSynset> synsets = getSynsets(language, word);
		for (BabelSynset synset : synsets)
		{
			// helps considering only once multiple source-target pairs in a
			// specific language with the same lemma (e.g., overblown
			// redirections)
			Set<Triple<String, String, Language>> translationsDone =
				new HashSet<Triple<String,String,Language>>();
			
			Multimap<BabelSense, BabelSense> translations = synset.getTranslations();
			List<BabelSense> senses = synset.getSenses(language, word);
			
			for (BabelSense sense : senses)
			{
				String senseLemma = sense.getSimpleLemma().toLowerCase();
				Collection<BabelSense> senseTranslations = translations.get(sense);
				for (BabelSense senseTranslation : senseTranslations)
				{
					Language translationLanguage = senseTranslation.getLanguage();
					if (translationLanguage == language) continue;
					String translation = senseTranslation.getSimpleLemma().toLowerCase();
					
					Triple<String, String, Language> translationTriple =
						new Triple<String, String, Language>(
							senseLemma, translation, translationLanguage);
					if (translationsDone.contains(translationTriple)) continue;
					translationsDone.add(translationTriple);
					
					// valid translation: count it!
					IntegerCounter<String> translationCounter =
						traslationCounters.get(translationLanguage);
		    		if (translationCounter == null)
		    		{
		    			translationCounter = new IntegerCounter<String>();
		    			traslationCounters.put(translationLanguage, translationCounter);
		    		}
		    		translationCounter.count(translation);
				}
			}
		}
		
		Multimap<Language, ScoredItem<String>> babelTraslations =
				new HashMultimap<Language, ScoredItem<String>>();
		for (Language otherLanguage : traslationCounters.keySet())
		{
			IntegerCounter<String> translationCounter = traslationCounters.get(otherLanguage);
			for (String translation : translationCounter.keySet())
				babelTraslations.put(otherLanguage,
									 new ScoredItem<String>(translation,
															translationCounter.get(translation)));
		}
		return babelTraslations;
	}
	
	/**
	 * Get the glosses of a specific Babel synset, given a concept identifier
	 * (Babel synset ID).
	 * 
	 * @param concept
	 *            a concept identifier (babel synset ID)
	 * @return the glosses of a specific {@link BabelSynset} to which the input
	 *         Babel synset ID corresponds
	 * @throws IOException
	 */
	public List<BabelGloss> getGlosses(String concept) throws IOException
	{
		TermQuery q = new TermQuery(new Term(BabelNetIndexField.ID.toString(), concept));
		List<BabelGloss> glossList = new ArrayList<BabelGloss>();

		Document mappingDoc = getMappingDocumentFromBabelNetId(concept);
		if (mappingDoc == null) return null;
		String[] othersId = mappingDoc.getValues(BabelNetIndexField.LICENSE_ID.toString());
		List<Document> docsThroughOtherLicense = new ArrayList<Document>();
		for(String id : othersId){
			q = new TermQuery(new Term(BabelNetIndexField.ID.toString(), id));
			BabelLicense bl = BabelLicense.getLongName(id.split(":")[0]);
			
			if (!licenseToGlosses.containsKey(bl)) continue;
			IndexSearcher glossLicense = licenseToGlosses.get(bl);
			TopDocs docs = glossLicense.search(q, MAXIMUM_NUMBER_OF_SYNSETS);
			for (ScoreDoc scoreDoc : docs.scoreDocs)
				docsThroughOtherLicense.add(glossLicense.doc(scoreDoc.doc));
		}
		
		// missing licenses
		if(docsThroughOtherLicense.size() == 0)	return glossList;

		Document doc = mergeGlossDocument(concept, docsThroughOtherLicense.remove(0),docsThroughOtherLicense);
		 String[] storedGlosses = doc.getValues(BabelNetIndexField.GLOSS.toString());
		 for (int i = 0; i < storedGlosses.length; i++)
		   {
		    	String storedGloss = storedGlosses[i];
		    	String[] split = storedGloss.split("\t");
		    	if (split.length != 4)
		    		throw new RuntimeException("Invalid gloss: " + storedGloss);
		    	
		    	Language language = Language.valueOf(split[0]);
		    	
		    	// backward compatibility
		    	if(split[1].equals("WIKIWN")) split[1] = "WN";
		    	BabelSenseSource senseSource = BabelSenseSource.valueOf(split[1]);
		    	String sense = split[2];
		    	String gloss = split[3];
		    	
		    	BabelGloss bGloss =
		    		new BabelGloss(senseSource, sense, language, gloss);
		    	glossList.add(bGloss);
		    }
		return glossList;
	}
	
	/**
	 * Creates a new instance of {@link BabelSynsetIterator}.
	 * 
	 * @return an instance of a {@link BabelSynsetIterator}.
	 * @throws IOException
	 */
	public BabelSynsetIterator getSynsetIterator() {
		//old version
		if(babelnet == null)
			return new BabelSynsetIterator(dictionary);
		else
			return new BabelSynsetIterator(babelnet);
	}

	/**
	 * Creates a new instance of {@link BabelOffsetIterator}.
	 * 
	 * @return an instance of a {@link BabelOffsetIterator}.
	 * @throws IOException
	 */
	public BabelOffsetIterator getOffsetIterator() {
		if(babelnet == null)
			return new BabelOffsetIterator(dictionary);
		else
			return new BabelOffsetIterator(babelnet);
	}
	
	/**
	 * Creates a new instance of {@link BabelLexiconIterator}.
	 * 
	 * @return an instance of a {@link BabelLexiconIterator}.
	 * @throws IOException
	 */
	public BabelLexiconIterator getLexiconIterator()
	{
		return new BabelLexiconIterator(lexicon);
	}
	
}
