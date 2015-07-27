package org.getalp.lexsema.wsd.method.aca.agents.updates;

import cern.jet.random.engine.MersenneTwister;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.method.aca.agents.Ant;
import org.getalp.lexsema.wsd.method.aca.environment.Environment;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SchwabEtAl2012AntUpdater implements AntUpdater {

    private final MersenneTwister mersenneTwister;
    private final SimilarityMeasure similarityMeasure;

    public SchwabEtAl2012AntUpdater(MersenneTwister mersenneTwister, SimilarityMeasure similarityMeasure) {
        this.mersenneTwister = mersenneTwister;
        this.similarityMeasure = similarityMeasure;
    }

    @Override
    public void update(Ant ant, Environment environment) {
        int position = ant.getPosition();
        List<Integer> neighbours = environment.getOutgoingNodes(position);
        int neighbourhoodSize = neighbours.size();
        List<Double> nodeScores;
        List<Double> edgeScores;
        if (ant.isReturning()) {
            nodeScores = returnNodeScore(ant, neighbours, environment);
            edgeScores = returnEdgeScore(position, neighbours, environment);
        } else {
            nodeScores = roamingNodeScores(neighbours, environment);
            edgeScores = roamingEdgeScores(position, neighbours, environment);
        }

        List<Double> scores = new ArrayList<>();
        //noinspection resource
        IntStream.range(0, neighbourhoodSize).forEach(i -> scores.add(nodeScores.get(i) + edgeScores.get(i)));
        double totalScore = getSum(scores);
        List<Double> probabilities = scores.stream().map(score -> score / totalScore).collect(Collectors.toList());
        int targetIndex = selectDestination(probabilities);
        int selectedTarget = neighbours.get(targetIndex);

        environment.visit(ant.visitor(), selectedTarget);
    }

    private int selectDestination(Collection<Double> probabilities) {
        double rand = mersenneTwister.raw();
        List<Boolean> decision = probabilities
                .stream()
                .sorted()
                .map(p -> p > rand)
                .collect(Collectors.toList());
        boolean currentDecision = false;
        int i = 0;
        while (!currentDecision) {
            currentDecision = decision.get(i);
            i++;
        }
        return i;

    }

    private double getSum(Collection<Double> energies) {

        final Stream<Double> stream = energies.stream();
        final DoubleStream doubleStream = stream.mapToDouble(e -> e);
        return doubleStream.sum();
    }

    private List<Double> getNeighboursEnergy(Collection<Integer> neighbours, final Environment environment) {
        return neighbours.stream().map(environment::getEnergy).collect(Collectors.<Double>toList());
    }


    private List<Double> roamingNodeScores(Collection<Integer> neighbours, Environment environment) {
        List<Double> energy = getNeighboursEnergy(neighbours, environment);
        double sum = getSum(energy);
        return energy
                .stream()
                .map(e -> e / sum)
                .collect(Collectors.toList());
    }

    private List<Double> roamingEdgeScores(int position, Collection<Integer> targets, Environment environment) {
        return targets.stream()
                .map(target -> 1d - environment.getPheromone(position, target))
                .collect(Collectors.toList());
    }

    private List<Double> returnNodeScore(Ant ant, Collection<Integer> neighbours, Environment environment) {
        return neighbours.stream()
                .map(position -> similarityMeasure.compute(environment.getNodeSignature(position), ant.getSemanticSignature()))
                .collect(Collectors.<Double>toList());
    }

    private List<Double> returnEdgeScore(int position, Collection<Integer> targets, Environment environment) {
        return targets
                .stream()
                .map(target -> environment.getPheromone(position, target))
                .collect(Collectors.toList());
    }
}
