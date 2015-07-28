package org.getalp.lexsema.wsd.method.aca.environment;


import cern.jet.random.engine.MersenneTwister;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.wsd.method.aca.agents.Ant;
import org.getalp.lexsema.wsd.method.aca.environment.graph.Node;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Collection;
import java.util.List;

public interface Environment {
    INDArray getOutgoingVector(int position);

    List<Integer> getOutgoingNodes(int position);

    boolean isNest(int position);

    boolean areSiblings(int position1, int position2);

    boolean isFriendNest(int position, int antHome);

    double getPheromone(int startPosition, int targetPosition);

    void setPheromone(int startPosition, int targetPosition, double pheromone);

    double getEnergy(int position);

    void setEnergy(int position, double energy);

    List<Node> nodes();

    Collection<Ant> ants();

    List<Node> words();

    void addAnt(Ant ant);

    SemanticSignature getNodeSignature(int position);

    void removeDeadAnts();

    void depositSignature(List<SemanticSymbol> semanticSymbols, int position, MersenneTwister mersenneTwister);

    void createBridge(int start, int end);

    void cleanupBridges();

    boolean isBridge(int start, int end);

    Node getNode(int position);

    List<Node> getNestsForNode(int position);

    int numberOfBridges();
}
