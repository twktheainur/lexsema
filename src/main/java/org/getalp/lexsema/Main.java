package org.getalp.lexsema;

import org.getalp.lexsema.lexicalresource.lemon.LexicalSense;
import org.getalp.lexsema.lexicalresource.lemon.dbnary.DBNary;
import org.getalp.lexsema.lexicalresource.lemon.dbnary.Vocable;
import org.getalp.lexsema.ontology.OWLTBoxModel;
import org.getalp.lexsema.ontology.OntologyModel;
import org.getalp.lexsema.ontology.storage.Store;
import org.getalp.lexsema.ontology.storage.StoreHandler;
import org.getalp.lexsema.ontology.storage.VirtuosoTripleStore;
import org.getalp.lexsema.util.exceptions.dbnary.NoSuchVocableException;

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

        OntologyModel tBox = new OWLTBoxModel();

        // Creating DBnary wrapper
        DBNary lr = new DBNary(tBox, Locale.ENGLISH);


        try {
            Vocable v = lr.getVocable("dog");

            LexicalSense ls = lr.instanciateLexicalSense("__ws_1_Erstsprache__Substantiv__1", null);
            String def = ls.getDefinition();
            System.err.println(ls);
            /*System.out.println(v);
            List<LexicalEntry> les = v.getLexicalEntries();
            for (LexicalEntry le : les) {
                List<LexicalSense> senses;
                System.err.println(le);
            }*/
        } catch (NoSuchVocableException e) {
            e.printStackTrace();
        }


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


    }

}
