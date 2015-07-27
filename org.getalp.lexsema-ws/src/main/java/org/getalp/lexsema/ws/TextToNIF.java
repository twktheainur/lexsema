package org.getalp.lexsema.ws;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.getalp.lexsema.io.nif.NIFURI;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.io.text.EnglishDKPTextProcessor;
import org.getalp.lexsema.io.text.TextProcessor;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.measures.lesk.AnotherLeskSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.CuckooSearchDisambiguator;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorerWithCache;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import edu.mit.jwi.Dictionary;

public class TextToNIF
{
    private static OntModelSpec ontModelSpec = OntModelSpec.OWL_DL_MEM;
    
    private static TextProcessor txtProcessor = new EnglishDKPTextProcessor();
    
    private static WordnetLoader wnLoader = getWordnetLoader();
        
    private String prefix;
    
    private boolean doTokenization;
    
    private boolean doDisambiguation;
    
    public TextToNIF()
    {
        this.prefix = "";
        doTokenization = false;
        doDisambiguation = false;
    }

    public OntModel loadFromString(String textContent)
    {
        OntModel model = ModelFactory.createOntologyModel(ontModelSpec, ModelFactory.createDefaultModel());
        return loadFromNIF(model, createContext(model, textContent));
    }

    public OntModel loadFromNIF(OntModel model)
    {
        return loadFromNIF(model, getContext(model));
    }

    private OntModel loadFromNIF(OntModel model, Individual context)
    {
        model.setNsPrefix("nif", NIFURI.prefix);
        model.setNsPrefix("p", prefix);
        processContext(context, model);
        return model;
    }
    
    public TextToNIF tokenize(boolean doTokenization)
    {
        this.doTokenization = doTokenization;
        return this;
    }

    public TextToNIF disambiguate(boolean doDisambiguation)
    {
        this.doDisambiguation = doDisambiguation;
        return this;
    }
    
    public TextToNIF setPrefix(String prefix)
    {
        this.prefix = prefix;
        return this;
    }
    
    private Individual getContext(OntModel model)
    {
        ExtendedIterator<Individual> individualIterator = model.listIndividuals();
        while (individualIterator.hasNext())
        {
            Individual individual = individualIterator.next();
            if (individual.hasOntClass(model.createClass(NIFURI.Context)))
            {
                return individual;
            }
        }
        return null;
    }
    
    private Individual createContext(OntModel model, String textContent)
    {
        String contextStringEnd = String.valueOf(textContent.length());
        String uri = prefix + "char=0," + contextStringEnd;
        Individual context = model.createIndividual(uri, model.createClass(NIFURI.RFC5147String));
        context.addOntClass(model.createClass(NIFURI.Context));
        context.addProperty(model.createDatatypeProperty(NIFURI.isString), textContent);
        context.addProperty(model.createDatatypeProperty(NIFURI.beginIndex), "0");
        context.addProperty(model.createDatatypeProperty(NIFURI.endIndex), "" + textContent.length());
        return context;
    }

    private Individual createWord(Individual context, Individual previousWord, Word word, Sense sense, OntModel model)
    {
        String uri = prefix + "char=" + word.getBegin() + "," + word.getEnd();
        Individual string = model.createIndividual(uri, model.createClass(NIFURI.RFC5147String));
        string.addOntClass(model.createClass(NIFURI.Word));
        string.addProperty(model.createDatatypeProperty(NIFURI.anchorOf), word.getSurfaceForm());
        string.addProperty(model.createDatatypeProperty(NIFURI.beginIndex), "" + word.getBegin());
        string.addProperty(model.createDatatypeProperty(NIFURI.endIndex), "" + word.getEnd());
        string.addProperty(model.createObjectProperty(NIFURI.referenceContext), context);
        if (previousWord != null)
        {
            string.addProperty(model.createObjectProperty(NIFURI.previousWord), previousWord);
            previousWord.addProperty(model.createObjectProperty(NIFURI.nextWord), string);
        }
        if (sense != null)
        {
            string.addProperty(model.createDatatypeProperty(prefix + "sense"), "" + sense.getId());
        }
        return string;
    }

    private void processContext(Individual context, OntModel model)
    {
        String textContent = context.getProperty(model.createDatatypeProperty(NIFURI.isString)).getString();
        if (doTokenization)
        {
            List<Individual> words = new ArrayList<Individual>();
            Text text = txtProcessor.process(textContent, "");
            Configuration c = null;
            if (doDisambiguation)
            {
                c = disambiguate(text, prefix, model);
            }
            Individual previousWord = null;
            for (int i = 0 ; i < text.size() ; i++)
            {
                Word word = text.getWord(i);
                Sense sense = null;
                if (doDisambiguation && c.getAssignment(i) != -1)
                {
                    sense = text.getSenses(i).get(c.getAssignment(i));
                }
                previousWord = createWord(context, previousWord, word, sense, model);
                words.add(previousWord);
            }
            addWordsToContext(context, words, model);
        }
    }
    
    private static void addWordsToContext(Individual context, List<Individual> words, OntModel model)
    {
        context.addProperty(model.createObjectProperty(NIFURI.firstWord), words.get(0));
        context.addProperty(model.createObjectProperty(NIFURI.lastWord), words.get(words.size() - 1));
        for (int i = 1 ; i < words.size() - 1 ; i++)
        {
            context.addProperty(model.createObjectProperty(NIFURI.word), words.get(i));
        }
    }
    
    private static Configuration disambiguate(Text text, String prefix, OntModel model)
    {
        model.createDatatypeProperty(prefix + "sense");
        wnLoader.loadSenses(text);
        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new AnotherLeskSimilarity());
        Disambiguator cuckooDisambiguator = new CuckooSearchDisambiguator(new StopCondition(StopCondition.Condition.SCORERCALLS, 100), 5, 0.5, 1, 0, scorer, true);
        Configuration c = cuckooDisambiguator.disambiguate(text);
        cuckooDisambiguator.release();
        return c;
    }
    
    private static WordnetLoader getWordnetLoader()
    {
        WordnetLoader wnLoader = new WordnetLoader(new Dictionary(new File("/home/coyl/lig/data/wordnet/3.0/dict")));
        wnLoader.loadDefinitions(true);
        return wnLoader;
    }
}
