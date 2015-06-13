package org.getalp.lexsema.util.rest;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

public class RestfulQuery {
    public static String restfulQuery(String uri, Map<String, String> parameters) throws IOException {

        StringBuilder params = new StringBuilder();
        if (!parameters.isEmpty()) {
            params.append("?");
            boolean first= true;
            for (String key : parameters.keySet()) {
                if(!first){
                    params.append("&");
                } else {
                    first = false;
                }
                params.append(key).append("=").append(URLEncoder.encode(parameters.get(key).trim(),"UTF-8"));
            }
        }
        URL url = new URL(uri+params);
        //make connection
        URLConnection urlc = url.openConnection();

        //use post mode
        //urlc.setDoOutput(true);
        //urlc.setAllowUserInteraction(false);

        //send query
        //PrintStream ps = new PrintStream(urlc.getOutputStream());
        //ps.print(query);
        //ps.close();

        //get result
        BufferedReader br = new BufferedReader(new InputStreamReader(urlc
                .getInputStream()));
        String l = br.readLine();
        String output = "";
        while (l != null) {
            output +=l;
            l = br.readLine();
        }
        br.close();
        return output.trim();
    }

    public enum Method {
        POST,GET;
    }
}
