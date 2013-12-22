/**
 * 
 */
package org.getalp.dilsmllr.ontology.storage;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import java.io.FileNotFoundException;

public class VirtuosoSPARQLExample8 {

	/**
	 * Executes a SPARQL query against a virtuoso url and prints results.
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {

		String url;
		if(args.length == 0)
		    url = "jdbc:virtuoso://kopi.imag.fr:1982";
		else
		    url = args[0];

		
		VirtGraph set = new VirtGraph (url, "dba", "dba");
      

/*			STEP 3			*/
/*		Select all data in virtuoso	*/

		//Query sparql = QueryFactory.create();
        //System.out.println("\nexecute:"+ sparql.toString());
/*			STEP 4			*/
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create ("SELECT * FROM <http://dbnary.imag.fr/en2> WHERE {?s ?p ?o. ?s a <http://kaiko.getalp.org/dbnary#Translation>. }", set);

		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution rs = results.nextSolution();
				RDFNode graph = rs.get("s");
				graph.asResource();
				
		    RDFNode s = rs.get("s");
		    RDFNode p = rs.get("p");
		    RDFNode o = rs.get("o");
		    System.out.println(" { " + s + " " + p + " " + o + " . }");
		}	
	}
}
