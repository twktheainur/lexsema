package org.getalp.lexsema.wsd.method.aca.environment.factories;


import org.getalp.lexsema.similarity.*;
import org.getalp.lexsema.wsd.method.aca.environment.Environment;
import org.getalp.lexsema.wsd.method.aca.environment.EnvironmentImpl;
import org.getalp.lexsema.wsd.method.aca.environment.graph.EnvironmentNode;
import org.getalp.lexsema.wsd.method.aca.environment.graph.NestNode;
import org.getalp.lexsema.wsd.method.aca.environment.graph.Node;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;

public class DocumentEnvironmentFactory implements EnvironmentFactory {

    private final Document text;
    private final int initialEnergy;
    private final int vectorLength;
    private final int initialPheromone;

    public DocumentEnvironmentFactory(Document text, int initialEnergy, int initialPheromone, int vectorLength) {
        this.text = text;
        this.initialEnergy = initialEnergy;
        this.vectorLength = vectorLength;
        this.initialPheromone = initialPheromone;
    }

    @Override
    public Environment build() {
        List<Node> nodes = new ArrayList<>();
        Map<Integer, Node> nestIndex = new HashMap<>();

        //Creating nodes
        createNodes(nodes, nestIndex);

        //Creating adjacency matrix
        INDArray adjacency = Nd4j.create(nodes.size(), nodes.size());
        populateAdjacency(adjacency);

        return new EnvironmentImpl(nodes, nestIndex, adjacency);
    }

    /**
     * Creating nodes
     *
     * @param nodes     The list that will contain the nodes
     * @param nestIndex The list that will contain the nests
     */
    private void createNodes(Collection<Node> nodes, Map<Integer, Node> nestIndex) {
        int currentPosition = 0;
        int currentWordIndex = 0;


        ++currentPosition;
        //Creating text root node
        //The text id is used as the node identifier
        nodes.add(new EnvironmentNode(currentPosition, text.getId(), initialEnergy, vectorLength));

        for (Word w : text) {
            nodes.add(new EnvironmentNode(currentPosition, w.getId(), initialEnergy, vectorLength));
            currentPosition++;

            for (Sense sense : text.getSenses(currentWordIndex)) {
                Node nestNode = new NestNode(currentPosition, sense.getId(), initialEnergy, sense.getSemanticSignature());
                nodes.add(nestNode);
                nestIndex.put(currentPosition, nestNode);
                currentPosition++;
            }
            currentWordIndex++;
        }

    }

    /**
     * Creating nodes
     *
     * @param adjacency The adjacency matrix
     */
    private void populateAdjacency(INDArray adjacency) {
        int currentPosition = 0;
        int currentWordIndex = 0;

        //Text node
        ++currentPosition;

        for (Word w : text) {
            //Word to sentence links
            adjacency.put(currentPosition, currentPosition - 1, initialPheromone);
            //Sentence to words links
            adjacency.put(currentPosition - 1, currentPosition, initialPheromone);

            currentPosition++;
            for (Sense sense : text.getSenses(currentWordIndex)) {
                //Sense to word links
                adjacency.put(currentPosition, currentPosition - 1, initialPheromone);
                //Word to sense links
                adjacency.put(currentPosition - 1, currentPosition, initialPheromone);
                currentPosition++;
            }
            currentWordIndex++;
        }
    }


}
