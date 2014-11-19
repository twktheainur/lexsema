package org.getalp.lexsema.ontolex.dbnary.uriparsers;

import org.getalp.lexsema.ontolex.uri.URIParser;

import java.util.HashMap;
import java.util.Map;

public class DBNaryVocableURIParser implements URIParser {
    @Override
    public Map<String, String> parseURI(String uri) {
        Map<String, String> extractedTokens = new HashMap<>();
        String[] uri_parts = uri.split("/");
        String canonicalURI = uri_parts[uri_parts.length - 1];
        extractedTokens.put("vocable", canonicalURI);
        return extractedTokens;
    }
}
