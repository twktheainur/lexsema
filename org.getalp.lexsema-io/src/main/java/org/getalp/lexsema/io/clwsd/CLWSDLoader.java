package org.getalp.lexsema.io.clwsd;


import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class CLWSDLoader {
    private String directory;

    public CLWSDLoader(String directory) {
        this.directory = directory;
    }

    public void load(){
        File dir = new File(directory);
        if(dir.exists()){
            File[] datafiles = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.contains(".data");
                }
            });

            for(File datafile: datafiles){

            }
        }
    }

}
