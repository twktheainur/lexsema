package org.getalp.lexsema.util;

public class URIUtils {
    public static String getCanonicalURI(String id) {
        String[] uriParts = id.split("/");
        return uriParts[uriParts.length - 1];
    }
}
