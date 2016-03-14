package org.getalp.lexsema.examples.points;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.lexsema.util.URIUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by boucherj on 04/02/16.
 */

import cern.colt.matrix.tdouble.DoubleMatrix1D;
        import org.getalp.lexsema.similarity.Sense;
        import org.getalp.lexsema.util.URIUtils;

        import java.util.ArrayList;
        import java.util.Iterator;
        import java.util.List;

public class PointClusterImpl implements PointCluster{
    private String id;
    private List<Point> members;
    private List<DoubleMatrix1D> memberLocations;
    private List<Double> weights;

    public PointClusterImpl(String id) {
        this.id = id;
        members = new ArrayList<>();
        weights = new ArrayList<>();
        memberLocations = new ArrayList<>();
    }


    public void addMember(Point member, DoubleMatrix1D location, double weight){
        members.add(member);
        memberLocations.add(location);
        weights.add(weight);
    }


    public Point getMember(int index){
        return members.get(index);
    }


    public DoubleMatrix1D getMemberLocation(int index){
        return memberLocations.get(index);
    }


    public double getMemberWeight(int index){
        return weights.get(index);
    }


    public int memberCount(){
        return  members.size();
    }

  /*  @Override
    public String toString() {
        StringBuilder retBuilder = new StringBuilder();
        retBuilder.append(String.format("Cluster|%s| {%s", id, System.lineSeparator()));
        retBuilder.append("\t");
        for(int i=0;i<members.size();i++) {
            retBuilder.append(String.format("\t%s (%s) -- %s %s", URIUtils.getCanonicalURI(members.get(i).getNode().toString()), weights.get(i),members.get(i).getDefinition(),System.lineSeparator()));
        }
        retBuilder.append(String.format("%s}", System.lineSeparator()));
        return retBuilder.toString();
    }*/


    public Iterator<Point> iterator() {
        return members.iterator();
    }
}