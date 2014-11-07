package org.getalp.lexsema.wsd.result;

import org.getalp.lexsema.io.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class SemevalWriter implements ConfigurationWriter {

    String path;

    public SemevalWriter(String path) {
        this.path = path;
    }

    @Override
    public void write(Document d, Configuration c) {
        PrintStream ps = null;
        try {
            ps = new PrintStream(path);
            String id = d.getId();
            for (int i = 0; i < d.getLexicalEntries().size(); i++) {
                if (c.getAssignment(i) >= 0) {
                    ps.println(id + " " + d.getLexicalEntries().get(i).getId() + " " + d.getSenses().get(i).get(c.getAssignment(i)).getId());
                } else {
                    ps.println(id + " " + d.getLexicalEntries().get(i).getId() + " ");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            ps.close();
        }

    }
}
