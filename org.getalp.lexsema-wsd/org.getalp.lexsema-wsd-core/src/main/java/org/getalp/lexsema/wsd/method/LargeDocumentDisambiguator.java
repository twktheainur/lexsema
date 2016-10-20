package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.*;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LargeDocumentDisambiguator implements Disambiguator
{
    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.DEFAULT_DOCUMENT_FACTORY;

    private static final Logger logger = LoggerFactory.getLogger(LargeDocumentDisambiguator.class);

    private final Disambiguator disambiguator;
    
    private final int maxWords;
    
    private final boolean verbose;

    public LargeDocumentDisambiguator(Disambiguator disambiguator, int maxWords, boolean verbose)
    {
        this.disambiguator = disambiguator;
        this.maxWords = maxWords;
        this.verbose = verbose;
    }

    public LargeDocumentDisambiguator(Disambiguator disambiguator, int maxWords)
    {
        this(disambiguator, maxWords, false);
    }

    public LargeDocumentDisambiguator(Disambiguator disambiguator)
    {
        this(disambiguator, 300, false);
    }

    @Override
    public Configuration disambiguate(Document document)
    {
        List<Document> documents = splitDocument(document);
        if (verbose) {
            logger.debug("Document splitted in {}", documents.size());
        }
        Collection<Configuration> configurations = new ArrayList<>();
        for (int i = 0 ; i < documents.size() ; i++)
        {
            if (verbose) {
                logger.debug("Disambiguating document {} ...", i + 1);
            }
            configurations.add(disambiguator.disambiguate(documents.get(i)));
        }
        return mergeConfigurations(configurations, document);
    }
    
    private List<Document> splitDocument(Document document)
    {
        List<Document> ret = new ArrayList<>();
        int documentsNb = document.size() / maxWords;
        int remainingWords = document.size() - (documentsNb * maxWords);
        for (int i = 0 ; i < documentsNb ; i++)
        {
            Document newDocument = createSubDocument(document, 0, maxWords);
            ret.add(newDocument);
        }
        if (remainingWords > 0)
        {
            Document newDocument = createSubDocument(document,0, remainingWords);
            ret.add(newDocument);
        }
        return ret;
    }

    private Document createSubDocument(Document document, int start, int end){
        int documentsNb = document.size() / maxWords;
        Document newDocument = DOCUMENT_FACTORY.createDocument();
        for (int j = 0 ; j < end ; j++)
        {
            Word word = document.getWord((documentsNb * maxWords) + j);
            List<Sense> senses = document.getSenses((documentsNb * maxWords) + j);
            addWordToDocument(newDocument, word, senses);
        }
        return newDocument;
    }

    private void addWordToDocument(Document document, Word word, Iterable<Sense> senses){
        document.addWord(word);
        document.addWordSenses(senses);
    }
    
    private Configuration mergeConfigurations(Iterable<Configuration> configurations, Document document)
    {
        Configuration ret = new ContinuousConfiguration(document, 0);
        int k = 0;
        for (Configuration oldConfiguration : configurations) {
            for (int j = 0; j < oldConfiguration.size(); j++) {
                ret.setSense(k, oldConfiguration.getAssignment(j));
                k++;
            }
        }
        return ret;
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c)
    {
        return disambiguate(document);
    }

    @Override
    public void release()
    {
        disambiguator.release();
    }
}
