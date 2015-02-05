package org.getalp.lexsema.io.clwsd;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CLWSDLoader implements TargetedWSDLoader {
    private String directory;
    List<TargetWordEntry> targetEntries;

    public CLWSDLoader(String directory) {
        this.directory = directory;
        targetEntries = new ArrayList<>();
    }

    @Override
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
                TargetEntryLoader targetEntryLoader = new Semeval2013Task10EntryLoader(datafile.getAbsolutePath());
                targetEntryLoader.load();
                targetEntries.add(targetEntryLoader.getEntry());
            }
        }
    }

    @Override
    public Iterator<TargetWordEntry> iterator() {
        return targetEntries.iterator();
    }
}
