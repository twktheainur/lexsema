package org.getalp.lexsema.examples.points;

/**
 * Created by boucherj on 04/02/16.
 */
import cern.colt.matrix.tdouble.DoubleMatrix1D;

import java.awt.*;
import java.util.Iterator;

/**
 * Interface for a Sense CLuster that groups several sense together in one set.
 */
public interface PointCluster extends Iterable<Point> {
    void addMember(Point member, DoubleMatrix1D location, double weight);

    Point getMember(int index);

    DoubleMatrix1D getMemberLocation(int index);

    double getMemberWeight(int index);

    int memberCount();

    @Override
    String toString();

    @Override
    Iterator<Point> iterator();
}
