/**
 * 
 */
package org.getalp.dilsmllr.ontology;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.Filter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * @author tchechem
 *
 */
public class OntologyModel {
	private OntModel model;
	private String propPath = "data/ontology.properties";
	private Properties properties;
	
	 private Map<AnonId, String> m_anonIDs = new HashMap< >();
   private int m_anonCount = 0;
	
	public OntologyModel() throws  IOException{
		loadProperties();
		createModel(null);
	}
	
	public OntologyModel(Model m) throws  IOException{
		loadProperties();
		createModel(m);
	}
	
	public OntologyModel(String propPath) throws  IOException{
		this.propPath = propPath;
		loadProperties();
		createModel(null);
		
	}
	
	private void createModel(Model m){
		if(m==null){
			model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		} else {
			model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, m);
		}
		if(properties.containsKey("ontologies")){
			String[] ontologies = properties.getProperty("ontologies").split(",");
			for(String ont: ontologies){
				model.read(ont.trim());
			}
		}
	}
	
	public void loadProperties() throws FileNotFoundException, IOException{
		properties = new Properties();
		properties.load(new FileInputStream(propPath));
	}

public OntModel getModel(){
	return model;
}


   // Constructors
   //////////////////////////////////

   // External signature methods
   //////////////////////////////////

   /** Show the sub-class hierarchy encoded by the given model */
   public void showHierarchy( PrintStream out, OntModel m ) {
       // create an iterator over the root classes that are not anonymous class expressions
       Iterator<OntClass> i = m.listHierarchyRootClasses()
                     .filterDrop( new Filter<OntClass>() {
                                   public boolean accept( OntClass o ) {
                                       return o.isAnon();
                                   }} );

       while (i.hasNext()) {
           showClass( out, i.next(), new ArrayList<OntClass>(), 0 );
       }
   }


   // Internal implementation methods
   //////////////////////////////////

   /** Present a class, then recurse down to the sub-classes.
    *  Use occurs check to prevent getting stuck in a loop
    */
   protected void showClass( PrintStream out, OntClass cls, List<OntClass> occurs, int depth ) {
       renderClassDescription( out, cls, depth );
       out.println();

       // recurse to the next level down
       if (cls.canAs( OntClass.class )  &&  !occurs.contains( cls )) {
           for (Iterator<OntClass> i = cls.listSubClasses( true );  i.hasNext(); ) {
               OntClass sub =  i.next();

               // we push this expression on the occurs list before we recurse
               occurs.add( cls );
               showClass( out, sub, occurs, depth + 1 );
               occurs.remove( cls );
           }
       }
   }


   /**
    * <p>Render a description of the given class to the given output stream.</p>
    * @param out A print stream to write to
    * @param c The class to render
    */
   public void renderClassDescription( PrintStream out, OntClass c, int depth ) {
       indent( out, depth );

       if (c.isRestriction()) {
           renderRestriction( out, c.as( Restriction.class ) );
       }
       else {
           if (!c.isAnon()) {
               out.print( "Class " );
               renderURI( out, c.getModel(), c.getURI() );
               out.print( ' ' );
           }
           else {
               renderAnonymous( out, c, "class" );
           }
       }
   }

   /**
    * <p>Handle the case of rendering a restriction.</p>
    * @param out The print stream to write to
    * @param r The restriction to render
    */
   protected void renderRestriction( PrintStream out, Restriction r ) {
       if (!r.isAnon()) {
           out.print( "Restriction " );
           renderURI( out, r.getModel(), r.getURI() );
       }
       else {
           renderAnonymous( out, r, "restriction" );
       }

       out.print( " on property " );
       renderURI( out, r.getModel(), r.getOnProperty().getURI() );
   }

   /** Render a URI */
   protected void renderURI( PrintStream out, PrefixMapping prefixes, String uri ) {
       out.print( prefixes.shortForm( uri ) );
   }

   /** Render an anonymous class or restriction */
   protected void renderAnonymous( PrintStream out, Resource anon, String name ) {
       String anonID = m_anonIDs.get( anon.getId() );
       if (anonID == null) {
           anonID = "a-" + m_anonCount++;
           m_anonIDs.put( anon.getId(), anonID );
       }

       out.print( "Anonymous ");
       out.print( name );
       out.print( " with ID " );
       out.print( anonID );
   }

   /** Generate the indentation */
   protected void indent( PrintStream out, int depth ) {
       for (int i = 0;  i < depth; i++) {
           out.print( "  " );
       }
   }
	
}
