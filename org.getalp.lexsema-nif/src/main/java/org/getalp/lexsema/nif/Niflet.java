package org.getalp.lexsema.nif;

import java.util.*;

import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.DocumentImpl;
import org.getalp.lexsema.similarity.WordImpl;
import org.getalp.lexsema.similarity.measures.lesk.AnotherLeskSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.CuckooSearchDisambiguator;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorerWithCache;

import org.nlp2rdf.core.NIFParameters;
import org.nlp2rdf.core.vocab.NIFOntClasses;
import org.nlp2rdf.implementation.stanfordcorenlp.StanfordWrapper;
import org.nlp2rdf.webservice.NIFServlet;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class Niflet extends NIFServlet
{
    private static final long serialVersionUID = 1L;

	public OntModel execute(NIFParameters nifParameters) throws Exception 
	{
		OntModel model = tokenize(nifParameters);
		
        List<Resource> words = getWordNodes(model);

        model.add(model.createStatement(model.createResource(nifParameters.getPrefix() + "sense"),
                                        model.createProperty(model.getNsPrefixURI("rdf") + "type"),
                                        model.createResource(model.getNsPrefixURI("owl") + "DatatypeProperty")));
        
        Document d = new DocumentImpl();
        d.setId("");
        for (Resource res : words)
        {
            d.addWord(new WordImpl("", 
                    res.getProperty(model.createProperty(model.getNsPrefixURI("nif") + "lemma")).getObject().toString(), 
                    res.getProperty(model.createProperty(model.getNsPrefixURI("nif") + "anchorOf")).getObject().toString(), 
                    ""));
        }
        
        LRLoader lrloader = new WordnetLoader("/home/coyl/data/wordnet/3.0/dict");
        lrloader.loadDefinitions(true);
        lrloader.loadSenses(d);
        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new AnotherLeskSimilarity());
        CuckooSearchDisambiguator cuckooDisambiguator = new CuckooSearchDisambiguator(new StopCondition(StopCondition.Condition.SCORERCALLS, 100), 5, 0.5, 1, 0, scorer, true);
        Configuration c = cuckooDisambiguator.disambiguate(d);

        for (int j = 0 ; j < c.size() ; j++)
        {
            words.get(j).addProperty(model.createProperty(nifParameters.getPrefix() + "sense"), "" + c.getAssignment(j));
        }
        
        return model;
	}
	
	private OntModel tokenize(NIFParameters nifParameters)
	{
		OntModel model = nifParameters.getInputModel();
		StanfordWrapper stanfordWrapper = new StanfordWrapper();
		ExtendedIterator<Individual> eit = model.listIndividuals(NIFOntClasses.Context.getOntClass(model));
		while (eit.hasNext())
		{
			Individual context = eit.next();
			stanfordWrapper.processText(context, model, model, nifParameters);
		}
		return model;
	}

    private List<Resource> getWordNodes(OntModel model)
    {
        List<Resource> words = new ArrayList<Resource>();
        ResIterator nodeIterator = model.listSubjects();
        while (nodeIterator.hasNext())
        {
            Resource node = nodeIterator.next();
            StmtIterator stmIterator = node.listProperties();
            while (stmIterator.hasNext())
            {
                Statement stm = stmIterator.next();
                if (stm.getPredicate().toString().equals(model.getNsPrefixURI("rdf") + "type") &&
                    stm.getObject().toString().equals(model.getNsPrefixURI("nif") + "Word"))
                {
                    words.add(node);
                }
            }
        }
        return words;
    }
}
