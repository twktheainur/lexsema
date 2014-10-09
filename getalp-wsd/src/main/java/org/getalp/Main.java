package org.getalp;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.loaders.document.Semeval2007DocumentLoader;
import org.getalp.disambiguation.loaders.resource.WordnetLoader;
import org.getalp.disambiguation.method.SimplifiedLesk;
import org.getalp.disambiguation.result.SemevalWriter;
import org.getalp.similarity.local.SimilarityMeasure;
import org.getalp.similarity.local.string.TverskiIndex;

public class Main {
    public static void main(String[] args){
        Semeval2007DocumentLoader dl = new Semeval2007DocumentLoader("/Users/tchechem/wsgetalp/data/senseval2007_task7/test/eng-coarse-all-words.xml",true);
        WordnetLoader lrloader = new WordnetLoader("/Users/tchechem/wsgetalp/data/wordnet/2.1/dict", true,false);
        SimilarityMeasure sim;

        sim = new TverskiIndex(0d, 0d, false, false, new ScaledLevenstein());
        //sim = new FuzzyLovaszTversky(new ScaledLevenstein(),0.5d,0.5d,false,0.1);

        SimplifiedLesk sl = new SimplifiedLesk(10,sim,false,false,true,true,false,false,false);

        System.err.println("Loading texts");
        dl.load();
        for(Document d : dl.getDocuments()){
            System.err.println("Starting document "+d.getId());
            System.err.println("\tLoading senses...");
            d.setSense(lrloader.getAllSenses(d.getWords()));
            //System.err.println("\tDisambiguating... ");
            Configuration c = sl.disambiguate(d);
            SemevalWriter sw = new SemevalWriter(d.getId()+".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d,c);
            System.err.println("done!");
        }

    }
}
