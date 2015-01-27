package org.getalp.lexsema.io.clwsd;

import java.util.Iterator;

/**
 * Created by tchechem on 21/01/15.
 */
public interface TargetedWSDLoader extends Iterable<TargetWordEntry> {
    void load();

    @Override
    Iterator<TargetWordEntry> iterator();
}
