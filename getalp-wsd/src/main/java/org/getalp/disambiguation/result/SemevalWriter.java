package org.getalp.disambiguation.result;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Created by tchechem on 9/16/14.
 */
public class SemevalWriter  implements ConfigurationWriter {

    String path;

    public SemevalWriter(String path) {
        this.path = path;
    }

    @Override
    public void write(Document d, Configuration c) {
        try {
            PrintStream ps = new PrintStream(path);
            String id = d.getId();
            for(int i =0;i<d.getWords().size();i++){
                if(c.getAssignment(i)>=0){
                    ps.println(id+" "+d.getWords().get(i).getId()+" "+d.getSense().get(i).get(c.getAssignment(i)).getId());
                } else {
                    ps.println(id+" "+d.getWords().get(i).getId()+" ");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
