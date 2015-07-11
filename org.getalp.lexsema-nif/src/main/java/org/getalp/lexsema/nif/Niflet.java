package org.getalp.lexsema.nif;

import java.util.*;

import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.io.text.EnglishDKPTextProcessor;
import org.getalp.lexsema.io.text.TextProcessor;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.measures.lesk.AnotherLeskSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.CuckooSearchDisambiguator;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorerWithCache;
import org.nlp2rdf.core.NIFParameters;
import org.nlp2rdf.core.Span;
import org.nlp2rdf.core.Text2RDF;
import org.nlp2rdf.core.vocab.NIFDatatypeProperties;
import org.nlp2rdf.core.vocab.NIFOntClasses;
import org.nlp2rdf.webservice.NIFServlet;

import com.google.common.collect.HashBiMap;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class Niflet extends NIFServlet
{
    private static final long serialVersionUID = 1L;
    
    private static class Data
    {
        public Data(NIFParameters np) { nifParameters = np; model = np.getInputModel(); }
        public NIFParameters nifParameters;
        public OntModel model;
        public Text text;
        public HashBiMap<Word, Resource> words;
    }

    public OntModel execute(NIFParameters nifParameters) throws Exception 
    {
        Data data = new Data(nifParameters);
        tokenize(data);
        annotate(data);
        disambiguate(data);
        return data.model;
    }
    
    private void tokenize(Data data)
    {
        TextProcessor txtProcessor = new EnglishDKPTextProcessor();
        Text2RDF text2RDF = new Text2RDF();
        ExtendedIterator<Individual> eit = data.model.listIndividuals(NIFOntClasses.Context.getOntClass(data.model));
        if (eit.hasNext())
        {
            Individual context = eit.next();
            String contextString = context.getPropertyValue(NIFDatatypeProperties.isString.getDatatypeProperty(data.model)).asLiteral().getString();
            data.text = txtProcessor.process(contextString, "");
            TreeMap<Span, List<Span>> tokenizedText = new TreeMap<Span, List<Span>>();
            Span sentence = new Span(0, contextString.length(), contextString);
            List<Span> words = new ArrayList<Span>();
            for (Word word : data.text)
            {
                String wordSurfaceForm = word.getSurfaceForm();
                int wordBegin = word.getBegin();
                int wordEnd = word.getEnd();
                Span wordSpan = new Span(wordBegin, wordEnd, wordSurfaceForm);
                words.add(wordSpan);
            }
            tokenizedText.put(sentence, words);
            text2RDF.generateNIFModel(data.nifParameters.getPrefix(), context, data.nifParameters.getUriScheme(), data.model, tokenizedText);
        }
        populateBiMap(data);
    }
    
    private void populateBiMap(Data data)
    {
        data.words = HashBiMap.create();
        List<Resource> wordNodes = getWordNodes(data.model);
        for (Resource wordNode : wordNodes)
        {
            data.words.inverse().put(wordNode, getCorrespondingWord(wordNode, data.text, data.model));
        }
    }
    
    private Word getCorrespondingWord(Resource wordNode, Text txt, OntModel model)
    {
        for (Word word : txt)
        {
            if (wordNode.getProperty(model.createProperty(model.getNsPrefixURI("nif") + "anchorOf")).getObject().toString().equals("" + word.getSurfaceForm()) &&
                wordNode.getProperty(model.createProperty(model.getNsPrefixURI("nif") + "beginIndex")).getObject().toString().equals("" + word.getBegin()) && 
                wordNode.getProperty(model.createProperty(model.getNsPrefixURI("nif") + "endIndex")).getObject().toString().equals("" + word.getEnd()))
            {
                return word;
            }
        }
        return null;
    }
    
    private void annotate(Data data)
    {
        Property lemmaProperty = data.model.createProperty(data.model.getNsPrefixURI("nif") + "lemma");
        Property posTagProperty = data.model.createProperty(data.model.getNsPrefixURI("nif") + "posTag");
        for (Resource wordNode : data.words.values())
        {
            Word word = data.words.inverse().get(wordNode);
            if (!wordNode.hasProperty(lemmaProperty))
            {
                wordNode.addProperty(lemmaProperty, word.getLemma());
            }
            if (wordNode.hasProperty(posTagProperty))
            {
                wordNode.addProperty(posTagProperty, word.getPartOfSpeech());
            }
        }
    }
    
    private void disambiguate(Data data)
    {
        data.model.add(data.model.createStatement(data.model.createResource(data.nifParameters.getPrefix() + "sense"),
                                                  data.model.createProperty(data.model.getNsPrefixURI("rdf") + "type"),
                                                  data.model.createResource(data.model.getNsPrefixURI("owl") + "DatatypeProperty")));
        
        WordnetLoader lrloader = new WordnetLoader("/home/coyl/data/wordnet/3.0/dict");
        lrloader.loadDefinitions(true);
        lrloader.loadSenses(data.text);
        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new AnotherLeskSimilarity());
        CuckooSearchDisambiguator cuckooDisambiguator = new CuckooSearchDisambiguator(new StopCondition(StopCondition.Condition.SCORERCALLS, 100), 5, 0.5, 1, 0, scorer, true);
        Configuration c = cuckooDisambiguator.disambiguate(data.text);

        for (int j = 0 ; j < c.size() ; j++)
        {
            Word word = data.text.getWord(j);
            Resource wordNode = data.words.get(word);
            wordNode.addProperty(data.model.createProperty(data.nifParameters.getPrefix() + "sense"), "" + c.getAssignment(j));
        }
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
