package org.getalp.lexsema.ws.nif;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import org.getalp.lexsema.ws.core.WebServiceServlet;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class NIFWebService extends WebServiceServlet
{
    private static final long serialVersionUID = 1L;
    
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        setHeaders(request, response);   
        Parameter parameter = new Parameter(request);
        OntModel model = readAndProcess(parameter);
        write(response, model, parameter.outputFormat);
    }

    private void setHeaders(HttpServletRequest request, HttpServletResponse response)
    {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
    }
    
    private OntModel readAndProcess(Parameter parameter) throws Exception
    {
        TextToNIF textToNif = new TextToNIF();
        textToNif.tokenize(true);
        textToNif.disambiguate(true);
        textToNif.setPrefix(parameter.prefix);

        if (parameter.inputFormat == Parameter.InputFormat.TURTLE && parameter.inputType == Parameter.InputType.URL)
        {
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
            model.read(parameter.input, "Turtle");
            return textToNif.loadFromNIF(model);
        }
        else if (parameter.inputFormat == Parameter.InputFormat.TEXT && parameter.inputType == Parameter.InputType.URL)
        {
            String realInput = Resources.toString(new URL(parameter.input), Charsets.UTF_8);
            return textToNif.loadFromString(realInput);
        }
        else if (parameter.inputFormat == Parameter.InputFormat.TEXT && parameter.inputType == Parameter.InputType.DIRECT)
        {
            return textToNif.loadFromString(parameter.input);
        }
        else if (parameter.inputFormat == Parameter.InputFormat.TURTLE && parameter.inputType == Parameter.InputType.DIRECT)
        {
            InputStream inputStream = new ByteArrayInputStream(parameter.input.getBytes(Charsets.UTF_8));
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
            model.read(inputStream, parameter.prefix, "Turtle");
            return textToNif.loadFromNIF(model);
        }
        throw new Exception("Unexpected error. Sorry.");
    }

    private void write(HttpServletResponse response, OntModel model, Parameter.OutputFormat outputFormat) throws Exception 
    {
        if (outputFormat == Parameter.OutputFormat.TURTLE)
        {
            response.setContentType("text/turtle");
            RDFDataMgr.write(response.getOutputStream(), model, Lang.TURTLE);
        }
        else if (outputFormat == Parameter.OutputFormat.JSONLD)
        {
            response.setContentType("application/ld+json");
            RDFDataMgr.write(response.getOutputStream(), model, Lang.JSONLD);
        }
        else if (outputFormat == Parameter.OutputFormat.RDFXML)
        {
            response.setContentType("application/rdf+xml");
            RDFDataMgr.write(response.getOutputStream(), model, Lang.RDFXML);
        }
        else if (outputFormat == Parameter.OutputFormat.NTRIPLES)
        {
            response.setContentType("application/n-triples");
            RDFDataMgr.write(response.getOutputStream(), model, Lang.NTRIPLES);
        }
    }
}
