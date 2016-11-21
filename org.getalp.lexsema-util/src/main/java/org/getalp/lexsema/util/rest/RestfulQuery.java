package org.getalp.lexsema.util.rest;


import org.getalp.lexsema.util.dataitems.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;

public final class RestfulQuery {


    private RestfulQuery() {
    }

    public static URLConnection restfulQuery(String uri, Collection<Pair<String, String>> parameters) throws IOException {

        StringBuilder params = new StringBuilder();
        if (!parameters.isEmpty()) {
            params.append("?");
            boolean first= true;
            for (Pair <String, String> pair : parameters) {
                if (first) {
                    first = false;
                } else {
                    params.append("&");
                }
                params.append(pair.first()).append("=").append(URLEncoder.encode(pair.second().trim(),"UTF-8"));
            }
        }
        URL url = new URL(uri+params);
        //make connection
        URLConnection urlc = url.openConnection();
        urlc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        return urlc;
    }

    public static String getRequestOutput(URLConnection urlConnection) throws IOException{
        StringBuilder output = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection
                .getInputStream()))) {
            String line = br.readLine();
            while (line != null) {
                output.append(line);
                line = br.readLine();
            }
            br.close();
        }
        return output.toString().trim();
    }
}
