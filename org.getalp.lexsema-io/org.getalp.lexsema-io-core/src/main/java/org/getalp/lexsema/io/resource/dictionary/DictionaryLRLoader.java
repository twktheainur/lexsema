package org.getalp.lexsema.io.resource.dictionary;

import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurus;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("BooleanParameter")
public class DictionaryLRLoader implements LRLoader {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryLRLoader.class);

    Map<String, List<Sense>> wordSenses;
    boolean indexed = false;

    public DictionaryLRLoader(File dictionaryFile) {
        this(dictionaryFile, true);
    }

    public DictionaryLRLoader(File dictionaryFile, boolean indexed) {
        this.indexed = true;
        wordSenses = new HashMap<>();
        try {
            XMLReader saxReader = XMLReaderFactory.createXMLReader();
            saxReader.setContentHandler(new DictionaryParser(wordSenses, indexed));
            saxReader.parse(new InputSource(new FileReader(dictionaryFile)));
        } catch (SAXException e) {
            logger.error(MessageFormat.format("Parser error :{0}", e.getLocalizedMessage()));
        } catch (FileNotFoundException e) {
            logger.error(MessageFormat.format("File not found :{0}", e.getLocalizedMessage()));
        } catch (IOException e) {
            logger.error(MessageFormat.format("Read|Write error :{0}", e.getLocalizedMessage()));
        }
    }

    @Override
    public List<Sense> getSenses(Word w) {
        String lemma = w.getLemma();
        String partOfSpeech = w.getPartOfSpeech();
        String tag = MessageFormat.format("{0}%{1}", lemma, partOfSpeech);
        if (wordSenses.get(tag) == null) {
            tag = MessageFormat.format("{0}%{1}", lemma.toLowerCase(), partOfSpeech);
        }
        return wordSenses.get(tag);
    }

    @Override
    public void loadSenses(Document document) {
        for (Word w : document) {
            List<Sense> senses = getSenses(w);
            if (senses != null) {
                document.addWordSenses(senses);
            } else {
                document.addWordSenses(new ArrayList<Sense>());
            }
        }
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader shuffle(boolean shuffle) {
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader extendedSignature(boolean hasExtendedSignature) {
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader loadDefinitions(boolean loadDefinitions) {
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader loadRelated(boolean loadRelated) {
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader stemming(boolean stemming) {
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader filterStopWords(boolean usesStopWords) {
        return this;
    }

    @Override
    public LRLoader addThesaurus(AnnotatedTextThesaurus thesaurus) {
        return this;
    }

    @Override
    public LRLoader index(boolean useIndex) {
        return this;
    }

}
