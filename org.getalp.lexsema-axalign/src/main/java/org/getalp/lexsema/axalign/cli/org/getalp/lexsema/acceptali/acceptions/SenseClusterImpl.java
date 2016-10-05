package org.getalp.lexsema.axalign.cli.org.getalp.lexsema.acceptali.acceptions;


import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.util.URIUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class SenseClusterImpl implements SenseCluster {
    private final String id;
    private final List<Sense> members;
    private final List<DoubleMatrix1D> memberLocations;
    private final List<Double> weights;

    SenseClusterImpl(String id) {
        this.id = id;
        members = new ArrayList<>();
        weights = new ArrayList<>();
        memberLocations = new ArrayList<>();
    }

    @Override
    public void addMember(Sense member, DoubleMatrix1D location, double weight){
        members.add(member);
        memberLocations.add(location);
        weights.add(weight);
    }

    @Override
    public Sense getMember(int index){
        return members.get(index);
    }

    @Override
    public DoubleMatrix1D getMemberLocation(int index){
        return memberLocations.get(index);
    }

    @Override
    public double getMemberWeight(int index){
        return weights.get(index);
    }

    @Override
    public int memberCount(){
        return  members.size();
    }

    @Override
    public String toString() {
        StringBuilder retBuilder = new StringBuilder();
        retBuilder.append(String.format("Cluster|%s| {%s", id, System.lineSeparator()));
        retBuilder.append("\t");
        for(int i=0;i<members.size();i++) {
            retBuilder.append(String.format("\t%s (%s) -- %s %s", URIUtils.getCanonicalURI(members.get(i).getNode().toString()), weights.get(i),members.get(i).getDefinition(),System.lineSeparator()));
        }
        retBuilder.append(String.format("%s}", System.lineSeparator()));
        return retBuilder.toString();
    }

    @Override
    public Iterator<Sense> iterator() {
        return members.iterator();
    }
}
