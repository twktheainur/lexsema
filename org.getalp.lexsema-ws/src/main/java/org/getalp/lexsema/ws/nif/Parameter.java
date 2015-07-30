package org.getalp.lexsema.ws.nif;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class Parameter
{
    public static enum InputFormat
    {
        TURTLE,
        TEXT
    }
    
    public static enum InputType
    {
        DIRECT,
        URL
    }
    
    public static enum OutputFormat
    {
        TURTLE,
        JSONLD,
        RDFXML,
        NTRIPLES
    }
    
    public static enum URIScheme
    {
        RFC5147String,
        CStringInst
    }
    
    public final String input;
    
    public final InputFormat inputFormat;
    
    public final InputType inputType;
    
    public final OutputFormat outputFormat;
    
    public final URIScheme uriScheme;
    
    public final String prefix;
    
    public Parameter(HttpServletRequest request) throws Exception
    {
        input = getInput(request);
        inputFormat = getInputFormat(request);
        inputType = getInputType(request);
        outputFormat = getOutputFormat(request);
        uriScheme = getURIScheme(request);
        prefix = getPrefix(request);
    }

    private static String getInput(HttpServletRequest request) throws Exception
    {
        String requestInput = request.getParameter("input");
        String requestI = request.getParameter("i");
        if (requestInput != null) return requestInput;
        if (requestI != null) return requestI;
        throw new Exception("You must give an input !");
    }
    
    private static InputFormat getInputFormat(HttpServletRequest request)
    {
        Map<String, InputFormat> possibleValues = new HashMap<>();
        possibleValues.put("turtle", InputFormat.TURTLE);
        possibleValues.put("text", InputFormat.TEXT);
        return getParameter(request.getParameter("f"), 
                            request.getParameter("informat"), 
                            possibleValues,
                            InputFormat.TURTLE);
    }
    
    private static InputType getInputType(HttpServletRequest request)
    {
        Map<String, InputType> possibleValues = new HashMap<>();
        possibleValues.put("direct", InputType.DIRECT);
        possibleValues.put("url", InputType.URL);
        return getParameter(request.getParameter("t"), 
                            request.getParameter("intype"), 
                            possibleValues,
                            InputType.DIRECT);
    }
    
    private static OutputFormat getOutputFormat(HttpServletRequest request)
    {
        Map<String, OutputFormat> possibleValues = new HashMap<>();
        possibleValues.put("turtle", OutputFormat.TURTLE);
        possibleValues.put("json-ld", OutputFormat.JSONLD);
        possibleValues.put("rdfxml", OutputFormat.RDFXML);
        possibleValues.put("ntriples", OutputFormat.NTRIPLES);
        return getParameter(request.getParameter("o"), 
                            request.getParameter("outformat"), 
                            possibleValues,
                            OutputFormat.TURTLE);
    }
    
    private static URIScheme getURIScheme(HttpServletRequest request)
    {
        Map<String, URIScheme> possibleValues = new HashMap<>();
        possibleValues.put("RFC5147String", URIScheme.RFC5147String);
        possibleValues.put("CStringInst", URIScheme.CStringInst);
        return getParameter(request.getParameter("u"), 
                            request.getParameter("urischeme"), 
                            possibleValues,
                            URIScheme.RFC5147String);
    }

    private static String getPrefix(HttpServletRequest request)
    {
        String requestPrefix = request.getParameter("prefix");
        String requestP = request.getParameter("p");
        if (requestPrefix != null) return requestPrefix;
        if (requestP != null) return requestP;
        return request.getRequestURL().toString() + "#";
    }
    
    private static <T> T getParameter(String shortFormRequest, String longFormRequest, Map<String, T> possibleValues, T defaultValue)
    {
        if (possibleValues.keySet().contains(longFormRequest)) return possibleValues.get(longFormRequest);
        if (possibleValues.keySet().contains(shortFormRequest)) return possibleValues.get(shortFormRequest);
        return defaultValue;
    }

}
