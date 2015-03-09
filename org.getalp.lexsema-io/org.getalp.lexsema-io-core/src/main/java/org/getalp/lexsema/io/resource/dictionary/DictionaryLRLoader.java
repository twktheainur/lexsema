package org.getalp.lexsema.io.resource.dictionary;

import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Word;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryLRLoader implements LRLoader {

    Map<String, List<Sense>> wordSenses;

    public DictionaryLRLoader(File dictionaryFile) {
        wordSenses = new HashMap<>();
        try {
            XMLReader saxReader = XMLReaderFactory.createXMLReader();
            saxReader.setContentHandler(new DictionaryParser(wordSenses));
            saxReader.parse(new InputSource(new FileReader(dictionaryFile)));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Sense> getSenses(Word w) {
        String tag = w.getLemma() + "%" + w.getPartOfSpeech();
        return wordSenses.get(tag);
    }

    @Override
    public void loadSenses(Document document) {
        for (Word w : document) {
            document.addWordSenses(getSenses(w));
        }
    }

    @Override
    public LRLoader shuffle(boolean shuffle) {
        return this;
    }

    @Override
    public LRLoader extendedSignature(boolean hasExtendedSignature) {
        return this;
    }

    @Override
    public LRLoader loadDefinitions(boolean loadDefinitions) {
        return this;
    }

    @Override
    public LRLoader setLoadRelated(boolean loadRelated) {
        return this;
    }

    @Override
    public LRLoader setStemming(boolean stemming) {
        return this;
    }

    @Override
    public LRLoader setUsesStopWords(boolean usesStopWords) {
        return this;
    }
}
