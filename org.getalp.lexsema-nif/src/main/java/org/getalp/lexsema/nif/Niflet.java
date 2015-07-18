package org.getalp.lexsema.nif;

import java.io.File;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.mit.jwi.*;
import edu.mit.jwi.Dictionary;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.writer.RDFStreamWriter;
import org.aksw.rdfunit.io.writer.RDFWriter;
import org.aksw.rdfunit.io.writer.RDFWriterFactory;
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
import org.nlp2rdf.webservice.NIFParameterWebserviceFactory;

import com.google.common.collect.HashBiMap;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class Niflet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    protected void doOptions(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    {
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE");
        httpServletResponse.setHeader("Access-Control-Max-Age", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    {
        handle(httpServletRequest, httpServletResponse);
    }

    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    {
        handle(httpServletRequest, httpServletResponse);
    }

    private void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    {
        try
        {
            doOptions(httpServletRequest, httpServletResponse);
            String defaultPrefix = httpServletRequest.getRequestURL().toString() + "#";
            NIFParameters nifParameters = NIFParameterWebserviceFactory.getInstance(httpServletRequest, defaultPrefix);
            System.out.println(nifParameters);
            Data data = new Data(nifParameters);
            tokenize(data);
            annotate(data);
            disambiguate(data);
            OntModel out = data.model;
            out.setNsPrefix("p", defaultPrefix);
            write(httpServletResponse, out, nifParameters.getOutputFormat());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static class Data
    {
        public Data(NIFParameters np) { nifParameters = np; model = np.getInputModel(); }
        public NIFParameters nifParameters;
        public OntModel model;
        public Text text;
        public HashBiMap<Word, Resource> words;
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

        WordnetLoader lrloader = new WordnetLoader(new Dictionary(new File("/home/coyl/lig/data/wordnet/3.0/dict")));
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

    private void write(HttpServletResponse httpServletResponse, OntModel out, String format) throws Exception 
    {
        OutputStream outputStream = httpServletResponse.getOutputStream();

        RDFWriter outputWriter = null;
        String contentType = "";

        switch (format.toLowerCase())
        {
            case "turtle":
                outputWriter = new RDFStreamWriter(outputStream, "TURTLE");
                contentType = "text/turtle";
                break;
            case "rdfxml":
                outputWriter = new RDFStreamWriter(outputStream, "RDF/XML");
                contentType = "application/rdf+xml";
                break;
            case "n3":
                outputWriter = new RDFStreamWriter(outputStream, "N3");
                contentType = "text/rdf+n3";
            case "ntriples":
                outputWriter = new RDFStreamWriter(outputStream, "NTRIPLES");
                contentType = "text/rdf+n3";
                break;
            case "html": {
                outputWriter = RDFWriterFactory.createHTMLWriter(TestCaseExecutionType.rlogTestCaseResult, outputStream);
                contentType = "text/html";
                break;
            }
            case "text": {
                contentType = "text";
                break;
            }
            default: {
                outputStream.close();
                throw new InvalidParameterException("There is no " + format + " output implemented at the moment. Sorry!");
            }
        }

        httpServletResponse.setContentType(contentType);
        httpServletResponse.setCharacterEncoding("UTF-8");

        if (outputWriter != null)
        {
            outputWriter.write(out);
        }
        else  // "text"
        {
            outputStream.write(outputStream.toString().getBytes());
        }

        outputStream.close();
    }
}
