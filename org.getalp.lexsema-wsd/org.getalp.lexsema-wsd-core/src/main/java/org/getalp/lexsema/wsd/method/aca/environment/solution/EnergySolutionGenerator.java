package org.getalp.lexsema.wsd.method.aca.environment.solution;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.ConfidenceConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.aca.environment.Environment;
import org.getalp.lexsema.wsd.method.aca.environment.graph.Node;

import java.util.List;
import java.util.Random;

public class EnergySolutionGenerator implements SolutionGenerator{
    private final Random random = new Random();

    @Override
    public Configuration generateSolution(Environment environment, Document document) {
        Configuration configuration = new ConfidenceConfiguration(document);
        List<Node> words = environment.words();

        for(int i=0;i<words.size();i++){
            Node wordNode = words.get(i);
            List<Node> senses = environment.getNestsForNode(wordNode.getPosition());
            int max = findMaximumEnergyIndex(senses);
            configuration.setSense(i,max);
//            configuration.setSense(i,random.nextInt(senses.size()));
        }
        return configuration;
    }

    private int findMaximumEnergyIndex(Iterable<Node> senses){
        double energyValue = 0;
        int maxIndex = 0;
        int index = 0;
        for(Node node: senses){
            double envValue = node.getEnergy();
            if(envValue>energyValue){
                maxIndex = index;
                energyValue = envValue;
            }
            index++;
        }
        return maxIndex;
    }

}
