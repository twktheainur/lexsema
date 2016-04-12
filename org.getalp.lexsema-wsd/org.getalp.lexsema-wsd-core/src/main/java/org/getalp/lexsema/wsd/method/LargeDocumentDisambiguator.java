package org.getalp.lexsema.wsd.method;

import java.util.ArrayList;
import java.util.List;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.DocumentImpl;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;

public class LargeDocumentDisambiguator implements Disambiguator
{
    private Disambiguator disambiguator;
    
    private int maxWords;
    
    public LargeDocumentDisambiguator(Disambiguator disambiguator, int maxWords)
    {
        this.disambiguator = disambiguator;
        this.maxWords = maxWords;
    }

    @Override
    public Configuration disambiguate(Document document)
    {
        List<Document> documents = splitDocument(document);
        List<Configuration> configurations = new ArrayList<>();
        for (int i = 0 ; i < documents.size() ; i++)
        {
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
            Document newDocument = new DocumentImpl();
            for (int j = 0 ; j < maxWords ; j++)
            {
                newDocument.addWord(document.getWord((i * maxWords) + j));
            }
            ret.add(newDocument);
        }
        if (remainingWords > 0)
        {
            Document newDocument = new DocumentImpl();
            for (int j = 0 ; j < remainingWords ; j++)
            {
                newDocument.addWord(document.getWord((documentsNb * maxWords) + j));
            }
            ret.add(newDocument);
        }
        return ret;
    }
    
    private Configuration mergeConfigurations(List<Configuration> configurations, Document document)
    {
        Configuration ret = new ContinuousConfiguration(document, 0);
        int k = 0;
        for (int i = 0 ; i < configurations.size() ; i++)
        {
            Configuration oldConfiguration = configurations.get(i);
            for (int j = 0 ; j < oldConfiguration.size() ; j++)
            {
                ret.setSense(k++, oldConfiguration.getAssignment(j));
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
