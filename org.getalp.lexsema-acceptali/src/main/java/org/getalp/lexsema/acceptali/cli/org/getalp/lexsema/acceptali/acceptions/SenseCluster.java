package org.getalp.lexsema.acceptali.cli.org.getalp.lexsema.acceptali.acceptions;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.lexsema.similarity.Sense;

import java.util.Iterator;

/**
 * Created by tchechem on 05/03/15.
 */
public interface SenseCluster extends Iterable<Sense> {
    void addMember(Sense member, DoubleMatrix1D location, double weight);

    Sense getMember(int index);

    DoubleMatrix1D getMemberLocation(int index);

    double getMemberWeight(int index);

    int memberCount();

    @Override
    String toString();

    @Override
    Iterator<Sense> iterator();
}
