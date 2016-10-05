package org.getalp.lexsema.axalign.cli.org.getalp.lexsema.acceptali.acceptions;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.lexsema.similarity.Sense;

import java.util.Iterator;

/**
* Interface for a Sense CLuster that groups several sense together in one set.
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
