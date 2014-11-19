package org.getalp.lexsema.ontolex.dbnary.uriparsers;

import org.getalp.lexsema.ontolex.uri.URIParser;

import java.util.HashMap;
import java.util.Map;


public class DBNaryLexicalSenseURIParser implements URIParser {
    @Override
    public Map<String, String> parseURI(String uri) {
        Map<String, String> extractedTokens = new HashMap<>();
        String[] uri_parts = uri.split("/");
        String canonicalURI = uri_parts[uri_parts.length - 1];
        String[] uri_tokens = canonicalURI.split("__");
        extractedTokens.put("senseNumber", uri_tokens[1].split("_")[1]);
        extractedTokens.put("canonicalFormWrittenRep", uri_tokens[1].replace("^__ws_[0-9]+_", ""));
        extractedTokens.put("partOfSpeech", uri_tokens[2]);
        extractedTokens.put("lexicalEntryNumber", uri_tokens[3]);
        return extractedTokens;
    }
}
