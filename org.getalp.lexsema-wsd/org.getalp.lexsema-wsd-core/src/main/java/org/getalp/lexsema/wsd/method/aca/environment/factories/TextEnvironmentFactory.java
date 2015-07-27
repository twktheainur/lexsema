package org.getalp.lexsema.wsd.method.aca.environment.factories;


import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.wsd.method.aca.environment.Environment;
import org.getalp.lexsema.wsd.method.aca.environment.EnvironmentImpl;
import org.getalp.lexsema.wsd.method.aca.environment.graph.EnvironmentNode;
import org.getalp.lexsema.wsd.method.aca.environment.graph.NestNode;
import org.getalp.lexsema.wsd.method.aca.environment.graph.Node;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;

public class TextEnvironmentFactory implements EnvironmentFactory{

    private final Text text;
    private final int initialEnergy;
    private final int vectorLength;
    private final int initialPheromone;

    public TextEnvironmentFactory(Text text, int initialEnergy,int initialPheromone, int vectorLength) {
        this.text = text;
        this.initialEnergy = initialEnergy;
        this.vectorLength = vectorLength;
        this.initialPheromone = initialPheromone;
    }

    @Override
    public Environment build() {
        List<Node> nodes = new ArrayList<>();
        Map<Integer,Node> nests = new HashMap<>();

        //Creating nodes
        createNodes(nodes,nests);

        //Creating adjacency matrix
        INDArray adjacency = Nd4j.create(nodes.size(),nodes.size());
        populateAdjacency(adjacency);

        return new EnvironmentImpl(nodes,nests,adjacency);
    }

    /**
     * Creating nodes
     * @param nodes The list that will contain the nodes
     * @param nestIndex The list that will contain the nests
     */
    private void createNodes(Collection<Node> nodes, Map<Integer,Node> nestIndex){
        int currentPosition = 0;
        int currentWordIndex = 0;


        ++currentPosition;
        //Creating text root node
        //The text id is used as the node identifier
        nodes.add(new EnvironmentNode(currentPosition,text.getId(),initialEnergy,vectorLength));

        //Creating sentence nodes
        for(Sentence sentence: text.sentences()){
            nodes.add(new EnvironmentNode(currentPosition,sentence.toString(),initialEnergy,vectorLength));
            currentPosition++;

            for(Word w: sentence){
                nodes.add(new EnvironmentNode(currentPosition,w.getId(),initialEnergy,vectorLength));
                currentPosition++;

                for(Sense sense: text.getSenses(currentWordIndex)){
                    Node nestNode = new NestNode(currentPosition, sense.getId(), initialEnergy, sense.getSemanticSignature());
                    nodes.add(nestNode);
                    nestIndex.put(currentPosition, nestNode);
                    currentPosition++;
                }
                currentWordIndex++;
            }
        }
    }

    /**
     * Creating nodes
     * @param adjacency The adjacency matrix
     */
    private void populateAdjacency(INDArray adjacency){
        int currentPosition = 0;
        int currentWordIndex = 0;

        //Text node
        ++currentPosition;

        //Sentence nodes
        for(Sentence sentence: text.sentences()){

            //Sentence to text links
            adjacency.put(currentPosition,currentPosition-1,initialPheromone);
            //Text to sentence links
            adjacency.put(currentPosition-1,currentPosition,initialPheromone);

            currentPosition++;
            for(Word w: sentence){
                //Word to sentence links
                adjacency.put(currentPosition,currentPosition-1,initialPheromone);
                //Sentence to words links
                adjacency.put(currentPosition-1,currentPosition,initialPheromone);

                currentPosition++;
                for(Sense sense: text.getSenses(currentWordIndex)){
                    //Sense to word links
                    adjacency.put(currentPosition,currentPosition-1,initialPheromone);
                    //Word to sense links
                    adjacency.put(currentPosition-1,currentPosition,initialPheromone);
                    currentPosition++;
                }
                currentWordIndex++;
            }
        }
    }

}
