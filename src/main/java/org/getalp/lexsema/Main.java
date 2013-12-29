package org.getalp.lexsema;

import org.getalp.lexsema.lexicalresource.LexicalResource;
import org.getalp.lexsema.lexicalresource.dbnary.DBNary;
import org.getalp.lexsema.ontology.OntologyModel;
import org.getalp.lexsema.ontology.lemon.LexicalEntry;
import org.getalp.lexsema.ontology.storage.Store;
import org.getalp.lexsema.ontology.storage.StoreHandler;
import org.getalp.lexsema.ontology.storage.VirtuosoTripleStore;

import java.io.IOException;
import java.util.Locale;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws IOException {


        /*
         * Initializing and registering store interface
         */
        //VirtuosoTripleStore.connect("jdbc:virtuoso://kopi.imag.fr:1982","dba","dba");
        Store vts = new VirtuosoTripleStore("jdbc:virtuoso://localhost:1111", "dba", "dba");
        StoreHandler.registerStoreInstance(vts);

        OntologyModel otm = vts.getModel();

        // Creating DBnary wrapper
        LexicalResource lr = new DBNary(otm, Locale.ENGLISH);

        LexicalEntry len = new LexicalEntry(lr, "http://kaiko.getalp.org/dbnary/eng/be__Verb__1");
        System.out.println(len.getURI());

        //VirtuosoTripleStore.getInstance().
        /*OntologyModel model = new OntologyModel();
        model.showHierarchy(System.err, model.getJenaModel());
		for (OntProperty op : model.getJenaModel().listOntProperties().toList()) {
			System.out.println("Property: " + op.getLocalName());
			if (op.getRange() != null) {
				System.out.println("\tRange: " + op.getRange().getLocalName());
			}
			if (op.getDomain() != null) {
				System.out.println("\tDomain: " + op.getDomain().getLocalName());
			}
		}*/

        StoreHandler.release();

    }

}
