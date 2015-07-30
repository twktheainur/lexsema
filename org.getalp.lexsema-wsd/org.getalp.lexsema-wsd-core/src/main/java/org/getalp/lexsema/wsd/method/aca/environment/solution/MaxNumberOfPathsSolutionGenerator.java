package org.getalp.lexsema.wsd.method.aca.environment.solution;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.ConfidenceConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.aca.environment.Environment;
import org.getalp.lexsema.wsd.method.aca.environment.graph.Node;

import java.util.List;
import java.util.Random;

public class MaxNumberOfPathsSolutionGenerator implements SolutionGenerator{
    private final Random random = new Random();

    @Override
    public Configuration generateSolution(Environment environment, Document document) {
        Configuration configuration = new ConfidenceConfiguration(document);
        List<Node> words = environment.words();

        for(int i=0;i<words.size();i++){
            Node wordNode = words.get(i);
            List<Node> senses = environment.getNestsForNode(wordNode.getPosition());
            int max = findMaximumNumberOfPaths(senses,environment);
            configuration.setSense(i,max);
//            configuration.setSense(i,random.nextInt(senses.size()));
        }
        return configuration;
    }

    private int findMaximumNumberOfPaths(Iterable<Node> senses, Environment environment){
        int maxNumNeighbours = 0;
        int maxIndex = 0;
        int index = 0;
        for(Node node: senses){
            List<Integer> neighbours = environment.getOutgoingNodes(node.getPosition());
            int neighbourhoodSize = neighbours.size();
            if(neighbourhoodSize>maxNumNeighbours){
                maxIndex = index;
                maxNumNeighbours = neighbourhoodSize;
            }
            index++;
        }
        return maxIndex;
    }

}
