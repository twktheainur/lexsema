package org.getalp.lexsema.translation;

import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.util.dataitems.Pair;
import org.getalp.lexsema.util.dataitems.PairImpl;
import org.getalp.lexsema.util.rest.RestfulQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class YandexAPITranslator implements Translator {
    private static Logger logger = LoggerFactory.getLogger(YandexAPITranslator.class);

    private String key;

    public YandexAPITranslator(String key) {
        this.key = key;
    }

    @Override
    public String translate(String source, Language sourceLanguage, Language targetLanguage) {
        List<Pair<String,String>> parameters = new ArrayList<>();
        parameters.add(new PairImpl<>("key",key));
        parameters.add(new PairImpl<>("lang", String.format("%s-%s", sourceLanguage.getISO2Code(), targetLanguage.getISO2Code())));
        parameters.add(new PairImpl<>("text",source));
        try {
            URLConnection connection = RestfulQuery.restfulQuery("https://translate.yandex.net/api/v1.5/tr/translate",parameters);
            String response = RestfulQuery.getRequestOutput(connection);
            String output = "";
            try {
                OutputXMLParser handler = new OutputXMLParser();
                XMLReader saxReader = XMLReaderFactory.createXMLReader();
                saxReader
                        .setContentHandler(handler);
                saxReader.parse(new InputSource(new StringReader(response)));
                output = handler.getTranslatedText();
            } catch (IOException | SAXException t) {
                logger.error(t.getLocalizedMessage());
            }
            return output;
        } catch (IOException e) {
            logger.warn(e.getLocalizedMessage());
        }
        return "";
    }

    @Override
    public void close() {

    }

    private class OutputXMLParser implements ContentHandler{

        String translatedText = "";
        boolean inText = false;

        @Override
        public void setDocumentLocator(Locator locator) {

        }

        @Override
        public void startDocument() throws SAXException {

        }

        @Override
        public void endDocument() throws SAXException {

        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {

        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {

        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if(localName.equals("text"));{
                inText = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if(localName.equals("text"));{
                inText = false;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if(inText){
                for(int i=start; i<start+length;i++) {
                    translatedText += ch[i];
                }
            }
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {

        }

        @Override
        public void skippedEntity(String name) throws SAXException {

        }

        public String getTranslatedText() {
            return translatedText;
        }
    }
}
