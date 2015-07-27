package org.getalp.lexsema.ws;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.Charsets;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.google.common.io.Resources;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class NIFWebService extends WebServiceServlet
{
    private static final long serialVersionUID = 1L;
    
    private static final TextToNIF textToNif = new TextToNIF();

    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        setHeaders(request, response);
        
        String prefix = request.getRequestURL().toString() + "#";
        String inputFormat = getInputFormat(request);
        String inputType = getInputType(request);
        String outputFormat = getOutputFormat(request);
        String input = getInput(request);
        
        textToNif.tokenize(true);
        textToNif.disambiguate(true);
        textToNif.setPrefix(prefix);
        
        if (inputFormat.equals("turtle") && inputType.equals("url"))
        {
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
            model.read(input, Lang.TURTLE.toString());
            model = textToNif.loadFromNIF(model);
            write(response, model, outputFormat);
        }
        else if (inputFormat.equals("text") && inputType.equals("url"))
        {
            String realInput = Resources.toString(new URL(input), Charsets.UTF_8);
            OntModel model = textToNif.loadFromString(realInput);
            write(response, model, outputFormat);
        }
        else if (inputFormat.equals("text") && inputType.equals("direct"))
        {
            OntModel model = textToNif.loadFromString(input);
            write(response, model, outputFormat);
        }
        else if (inputFormat.equals("turtle") && inputType.equals("direct"))
        {
            InputStream inputStream = new ByteArrayInputStream(input.getBytes(Charsets.UTF_8));
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
            model.read(inputStream, prefix, Lang.TURTLE.toString());
            model = textToNif.loadFromNIF(model);
            write(response, model, outputFormat);
        }
    }

    private void setHeaders(HttpServletRequest request, HttpServletResponse response)
    {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
    }

    private String getInput(HttpServletRequest request) throws Exception
    {
        if (request.getParameter("input") != null) return request.getParameter("input");
        if (request.getParameter("i") != null) return request.getParameter("i");
        else throw new Exception("You must give an input !");
    }
    
    private String getInputFormat(HttpServletRequest request)
    {
        return getParameter(request.getParameter("f"), 
                            request.getParameter("informat"), 
                            new String[]{"turtle", "text"}, 
                            "turtle");
    }
    
    private String getInputType(HttpServletRequest request)
    {
        return getParameter(request.getParameter("t"), 
                            request.getParameter("intype"), 
                            new String[]{"direct", "url"}, 
                            "direct");
    }
    
    private String getOutputFormat(HttpServletRequest request)
    {
        return getParameter(request.getParameter("o"), 
                            request.getParameter("outformat"), 
                            new String[]{"turtle", "json-ld", "rdfxml", "ntriples"}, 
                            "turtle");
    }
    
    private String getParameter(String shortFormRequest, String longFormRequest, String[] possibleValues, String defaultValue)
    {
        if (Arrays.asList(possibleValues).contains(longFormRequest)) return longFormRequest;
        if (Arrays.asList(possibleValues).contains(shortFormRequest)) return shortFormRequest;
        return defaultValue;
    }

    private void write(HttpServletResponse response, OntModel model, String outputFormat) throws Exception 
    {
        if (outputFormat.equals("turtle"))
        {
            response.setContentType("text/turtle");
            RDFDataMgr.write(response.getOutputStream(), model, Lang.TURTLE);
        }
        else if (outputFormat.equals("json-ld"))
        {
            response.setContentType("application/ld+json");
            RDFDataMgr.write(response.getOutputStream(), model, Lang.JSONLD);
        }
        else if (outputFormat.equals("rdfxml"))
        {
            response.setContentType("application/rdf+xml");
            RDFDataMgr.write(response.getOutputStream(), model, Lang.RDFXML);
        }
        else if (outputFormat.equals("ntriples"))
        {
            response.setContentType("application/n-triples");
            RDFDataMgr.write(response.getOutputStream(), model, Lang.NTRIPLES);
        }
    }
}
