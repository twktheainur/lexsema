package org.getalp.lexsema.wsd.method.aca.environment.solution;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.ConfidenceConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.aca.environment.Environment;
import org.getalp.lexsema.wsd.method.aca.environment.graph.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class EnergyPathsSolutionGenerator implements SolutionGenerator{
    private final Random random = new Random();

    @Override
    public Configuration generateSolution(Environment environment, Document document) {
        Configuration configuration = new ConfidenceConfiguration(document);
        List<Node> words = environment.words();

        for(int i=0;i<words.size();i++){
            Node wordNode = words.get(i);
            List<Node> senses = environment.getNestsForNode(wordNode.getPosition());
            int max = findMaximumNumberOfPaths(senses,environment);
//            configuration.setSense(i,max);
            configuration.setSense(i,max);
        }
        return configuration;
    }

    private int findMaximumNumberOfPaths(Collection<Node> senses, Environment environment){
        double maxScore = 0;
        int maxIndex = 0;
        int index = 0;
        double totalEnergy = senses.stream().mapToDouble(Node::getEnergy).sum();
        double totalNeighbours = senses.stream().mapToInt(node -> environment.getOutgoingNodes(node.getPosition()).size()).sum();
        double totalPheromone = 0;
        for(Node node: senses){
            List<Integer> neighbours = environment.getOutgoingNodes(node.getPosition());
            totalPheromone += neighbours.stream().mapToDouble(n -> environment.getPheromone(node.getPosition(),n)).sum();
        }
        for(Node node: senses){
            List<Integer> neighbours = environment.getOutgoingNodes(node.getPosition());
            double score = calculateSenseScore(environment, node,neighbours,totalEnergy,totalNeighbours, totalPheromone);
            if(score>maxScore){
                maxIndex = index;
                maxScore = score;
            }
            index++;
        }
        return maxIndex;
    }

    private double calculateSenseScore(Environment environment, Node node, Collection<Integer> neighbourhood, double totalEnergy, double totalNeighbours, double totalPheromone){
        double pheromone = neighbourhood.stream().mapToDouble(n -> environment.getPheromone(node.getPosition(),n)).sum();
        return (node.getEnergy()/totalEnergy + neighbourhood.size()/(double)totalNeighbours + pheromone/totalPheromone)/3d;
    }

}
