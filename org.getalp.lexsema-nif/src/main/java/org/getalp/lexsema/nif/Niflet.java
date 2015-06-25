package org.getalp.lexsema.nif;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.io.text.EnglishDKPTextProcessor;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.measures.lesk.ACExtendedLeskSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.CuckooSearchDisambiguator;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorerWithCache;
import org.nlp2rdf.core.vocab.NIFDatatypeProperties;
import org.nlp2rdf.core.vocab.NIFObjectProperties;
import org.nlp2rdf.core.vocab.NIFOntClasses;

import cern.colt.Arrays;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class Niflet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
   
    public Niflet() 
    {
        
    }
/*
	public List<RDFNode> getContext(OntModel model)
	{
		List<Resource> types = getTypes(model);
		if (types.contains(NIFOntClasses.Context.getOntClass(model)))
		{
			return getContextOfNifContext(model);
		}
		if (types.contains(NIFOntClasses.Sentence.getOntClass(model)))
		{
			return getContextOfNifSentence(model);
		}
		else if (types.contains(NIFOntClasses.Word.getOntClass(model)))
		{
			return getContextOfNifWord(model);
		}
		return new ArrayList<RDFNode>();
	}
	/*
	private List<RDFNode> getContextOfNifContext(OntModel model)
	{
		List<RDFNode> context = new ArrayList<RDFNode>();
		ResIterator sentenceIterator = model.listSubjectsWithProperty(NIFObjectProperties.referenceContext.getObjectProperty(model));
		while(sentenceIterator.hasNext())
		{
			Resource sentence = sentenceIterator.next();
			if(getTypes(sentence).contains(NIFOntClasses.Sentence.getOntClass(model)))
				context.addAll(getContextOfNifSentence(sentence));
		}
		
		return context;
	}
	
	public List<RDFNode> getContextOfNifSentence(OntModel model)
	{
		// Get the sentence of the node
		List<RDFNode> context = new ArrayList<RDFNode>();

		NodeIterator wordIterator = model.listObjectsOfProperty(NIFObjectProperties.word.getObjectProperty(model));
		while(wordIterator.hasNext())
			context.add(wordIterator.next());
		
		Collections.sort(context, new Comparator<RDFNode>()
		{
			@Override
			public int compare(RDFNode word1, RDFNode word2)
			{
				int beginIndex1 = model.listObjectsOfProperty((Resource)word1, NIFDatatypeProperties.beginIndex.getDatatypeProperty(model)).next().asLiteral().getInt();
				int beginIndex2 = model.listObjectsOfProperty((Resource)word2, NIFDatatypeProperties.beginIndex.getDatatypeProperty(model)).next().asLiteral().getInt();
				
				int endIndex1 = model.listObjectsOfProperty((Resource)word1, NIFDatatypeProperties.endIndex.getDatatypeProperty(model)).next().asLiteral().getInt();
				int endIndex2 = model.listObjectsOfProperty((Resource)word2, NIFDatatypeProperties.endIndex.getDatatypeProperty(model)).next().asLiteral().getInt();

				// Same word (w1 == w2)
				if(beginIndex1 == beginIndex2 && endIndex1 == endIndex2)
					return 0;
				
				// w1 w2
				if(endIndex1 < beginIndex2) return -1;
				
				// w2 w1
				else return 1;
			}
		});
		
		return context;
	}
	
	public List<RDFNode> getContextOfNifWord(OntModel model)
	{
		// Get the sentence of the node
		List<RDFNode> context = new ArrayList<RDFNode>();

		RDFNode sentence = model.listObjectsOfProperty(NIFObjectProperties.sentence.getObjectProperty(model)).next();
		List<RDFNode> sentenceContext = getContextOfNifSentence(sentence);
		sentenceContext.remove(node);
		
		context.add(node);
		context.addAll(sentenceContext);
		
		return sentenceContext;
	}
	
    
	public List<Resource> getTypes(OntModel model)
	{
		List<Resource> isA = new ArrayList<Resource>();
		NodeIterator iterator = model.listObjectsOfProperty(model.createProperty(model.getNsPrefixURI("rdf") + "type"));
		while(iterator.hasNext())
		{
			RDFNode isANode = iterator.next();
			if(isANode instanceof Resource)
			{
				isA.add((Resource)isANode);
			}
		}
		return isA;
	}
	*/
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
	    response.getOutputStream().println("i = " + request.getParameter("i"));
	    response.getOutputStream().println("f = " + request.getParameter("f"));
	    if (request.getParameter("f").equals("text"))
	    {
	    	EnglishDKPTextProcessor txtProcessor = new EnglishDKPTextProcessor();
	    	Text txt = txtProcessor.process(request.getParameter("i"), "osef");
	        LRLoader lrloader = new WordnetLoader("/home/coyl/data/wordnet/3.0/dict");
	        lrloader.loadDefinitions(true);
	        lrloader.loadSenses(txt);
	        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new ACExtendedLeskSimilarity());
	        CuckooSearchDisambiguator cuckooDisambiguator = new CuckooSearchDisambiguator(new StopCondition(StopCondition.Condition.SCORERCALLS, 100), 5, 0.5, 1, 0, scorer, true);
	        Configuration c = cuckooDisambiguator.disambiguate(txt);
	        for (int i = 0 ; i < c.size() ; i++)
	        {
	        	response.getOutputStream().println(c.getDocument().getWord(i).getLemma() + " = " + c.getAssignment(i));
	        }
	    }
	    /*
	    else
	    {
			OntModel model = new OntModelImpl(OntModelSpec.OWL_DL_MEM);
	        
			response.getOutputStream().println("Loading resource...");
	        model.read(request.getParameter("i"));
	        response.getOutputStream().println("Resource loaded.");
	        
	        /*
	        List<Resource> types = getTypes(model);
	        response.getOutputStream().println(Arrays.toString(types.toArray()));
	        */
	        /*
			NodeIterator iterator = model.listObjectsOfProperty(model.createProperty(model.getNsPrefixURI("nif") + "word"));
			while(iterator.hasNext())
			{
				RDFNode isANode = iterator.next();
				if(isANode instanceof Resource)
				{
					StmtIterator stmIt = isANode.asResource().listProperties();
			        while (stmIt.hasNext())
			        {
			        	Statement stm = stmIt.next();
			        	if (stm.getPredicate().toString().equals(model.getNsPrefixURI("nif") + "anchorOf"))
			        	{
							response.getOutputStream().println(stm.getObject().toString());
			        	}
			        }
				}
			}
	        */
	        /*
	        StmtIterator stmIt = model.listStatements();
	        while (stmIt.hasNext())
	        {
	        	Statement stm = stmIt.next();
	        	if (stm.getPredicate().toString().equals(model.getNsPrefixURI("rdf") + "type") &&
	        		)
	        	{
	            	response.getOutputStream().println(stm.getSubject().getProperty(model.createProperty(nameSpace, localName)).toString());
	        	}
	        }
	        
	        //response.getOutputStream().println(model.toString());
	    
		}
	*/
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		
	}
}
