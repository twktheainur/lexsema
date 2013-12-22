package org.getalp.dilsmllr;

import org.getalp.dilsmllr.ontology.OntologyModel;
import org.getalp.dilsmllr.ontology.graph.Graph;
import org.getalp.dilsmllr.ontology.graph.LexicalEntry;
import org.getalp.dilsmllr.ontology.storage.VirtuosoTripleStore;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws  IOException {
		//"jdbc:virtuoso://kopi.imag.fr:1982","dba","dba"
		//VirtuosoTripleStore.connect("jdbc:virtuoso://kopi.imag.fr:1982","dba","dba");
        VirtuosoTripleStore.connect("jdbc:virtuoso://localhost:1111","dba","dba");
        OntologyModel otm = new OntologyModel(VirtuosoTripleStore.getInstance().getModel());
        LexicalEntry len = new LexicalEntry("http://kaiko.getalp.org/dbnary/eng/be__Verb__1",new Graph("http://dbnary.imag.fr/en2",otm));

		//VirtuosoTripleStore.getInstance().
		/*OntologyModel model = new OntologyModel();
		model.showHierarchy(System.err, model.getModel());
		for (OntProperty op : model.getModel().listOntProperties().toList()) {
			System.out.println("Property: " + op.getLocalName());
			if (op.getRange() != null) {
				System.out.println("\tRange: " + op.getRange().getLocalName());
			}
			if (op.getDomain() != null) {
				System.out.println("\tDomain: " + op.getDomain().getLocalName());
			}
		}*/

	}

}
