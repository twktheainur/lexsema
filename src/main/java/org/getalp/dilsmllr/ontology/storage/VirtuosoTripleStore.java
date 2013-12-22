/**
 * 
 */
package org.getalp.dilsmllr.ontology.storage;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import org.getalp.dilsmllr.ontology.graph.Relation;
import virtuoso.jena.driver.VirtModel;
import virtuoso.jena.driver.VirtuosoQueryEngine;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import java.sql.SQLException;
import java.util.List;

public class VirtuosoTripleStore {

	private static VirtuosoTripleStore	instance;
	private VirtModel										model;

	// url = "jdbc:virtuoso://kopi.imag.fr:1982";"dba", "dba"
	private VirtuosoTripleStore(String uri, String username, String password) throws SQLException {
		model = VirtModel.openDefaultModel(uri,username,password);
	}

	public List<Relation> runQuery(Query q, Model m) {
		VirtuosoQueryEngine.register();

		QueryExecution qexec = VirtuosoQueryExecutionFactory.create(q.toString(),model);
        System.out.println(qexec.getQuery().toString(Syntax.defaultSyntax));
		try {
			ResultSet rs = qexec.execSelect();
			while(rs.hasNext()){
				QuerySolution	qs = rs.nextSolution();
				System.out.println(qs.get("s"));
			}
		} catch (Exception e) {
            e.printStackTrace();
		}

		VirtuosoQueryEngine.unregister();
		return null;
	}

	public static void connect(String uri, String username, String password) {
		if (instance == null) {
            try {
                instance = new VirtuosoTripleStore(uri, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}

	public static VirtuosoTripleStore getInstance() {
		return instance;
	}

    public VirtModel getModel() {
        return model;
    }
}
