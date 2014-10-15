package org.getalp;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.loaders.document.Semeval2007DocumentLoader;
import org.getalp.disambiguation.loaders.resource.WordnetLoader;
import org.getalp.disambiguation.method.SupervisedR;
import org.getalp.disambiguation.result.SemevalWriter;

public class SupervisedRMain {
    public static void main(String[] args){
        Semeval2007DocumentLoader dl = new Semeval2007DocumentLoader("/Users/tchechem/wsgetalp/data/senseval2007_task7/test/eng-coarse-all-words.xml",false);
        WordnetLoader lrloader = new WordnetLoader("/Users/tchechem/wsgetalp/data/wordnet/2.1/dict", true,false);


        SupervisedR disambiguator = new SupervisedR(3,1,1,2,"/Users/tchechem/wsgetalp/data/supervised");

        System.err.println("Loading texts");
        dl.load();
        for(Document d : dl.getDocuments()){
            System.err.println("Starting document "+d.getId());
            System.err.println("\tLoading senses...");
            d.setSense(lrloader.getAllSenses(d.getWords()));
            //System.err.println("\tDisambiguating... ");
            Configuration c = disambiguator.disambiguate(d);
            SemevalWriter sw = new SemevalWriter(d.getId()+".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d,c);
            System.err.println("done!");
        }

    }
}
