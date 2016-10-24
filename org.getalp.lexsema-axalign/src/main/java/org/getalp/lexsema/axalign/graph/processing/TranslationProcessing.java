package org.getalp.lexsema.axalign.graph.processing;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.jgrapht.Graph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

public class TranslationProcessing {

    private static final Logger logger = LoggerFactory.getLogger(TranslationProcessing.class);

    private static final RandomGenerator randomGenerator = new MersenneTwister();

    public Collection<Set<LexicalEntry>> getCliques(Graph<LexicalEntry, DefaultEdge> translationGraph) {
        BronKerboschCliqueFinder<LexicalEntry, DefaultEdge> bk = new BronKerboschCliqueFinder<>(translationGraph);
        return bk.getAllMaximalCliques();
    }

    public Collection<Set<LexicalEntry>> getAmbiguitySets(Collection<Set<LexicalEntry>> cliques) {
        Collection<Set<LexicalEntry>> ambigSets = new ArrayList<>();
        List<Set<LexicalEntry>> cliquesList = (List<Set<LexicalEntry>>) cliques;
        for (int i = 0; i < cliquesList.size(); i++) {
            for (int j = i + 1; j < cliquesList.size(); j++) {
                Set<LexicalEntry> set = getAmbiguitySet(cliquesList.get(i), cliquesList.get(j));
                if (!set.isEmpty() && !ambigSets.contains(set)) {
                    ambigSets.add(set);
                }
            }
        }
        return ambigSets;
    }

    public String displayAmbiguityInfo(Collection<Set<LexicalEntry>> ambiguitySets) {
        // le nombre d'ensembles d'ambiguité
        int nbSet = ambiguitySets.size();
        // le nombre d'entrées lexicales ambigues
        int nbAmbigEntries = getNbAmbigEntry(ambiguitySets);
        return MessageFormat.format("There are {0} ambiguity sets and {1} ambiguous entries.\n", nbSet, nbAmbigEntries);
    }

    private int getNbAmbigEntry(Iterable<Set<LexicalEntry>> ambiguitySets) {
        Collection<LexicalEntry> ambigEntries = new HashSet<>();
        for (Set<LexicalEntry> ambigSet : ambiguitySets) {
            ambigEntries.addAll(ambigSet);
        }
        return ambigEntries.size();
    }

    public Set<LexicalEntry> getAmbiguousEntries(Iterable<Set<LexicalEntry>> ambiguitySets) {
        Set<LexicalEntry> ambiguousEntries = new HashSet<>();
        for (Set<LexicalEntry> ambiguitySet : ambiguitySets) {
            ambiguousEntries.addAll(ambiguitySet);
        }
        return ambiguousEntries;
    }

    private Set<LexicalEntry> getAmbiguitySet(Iterable<LexicalEntry> clique1, Collection<LexicalEntry> clique2) {
        Set<LexicalEntry> ambigSet = new HashSet<>();
        for (LexicalEntry le : clique1) {
            if (clique2.contains(le)) {
                ambigSet.add(le);
            }
        }
        return ambigSet;
    }

    private String displaySetAsString(Iterable<LexicalEntry> set) {
        StringBuilder setBuilder = new StringBuilder();
        for (LexicalEntry le : set) {
            setBuilder.append(le.getLemma()).append("_")
                    .append(le.getNumber()).append("_").append(le.getPartOfSpeech())
                    .append("_").append(le.getLanguage().getISO2Code()).append(" , ");
        }
        setBuilder.append("(");
        String setString = setBuilder.toString().substring(0, setBuilder.length() - 3);
        return MessageFormat.format("({0})", setString);
    }

    public String displaySetsAsString(Iterable<Set<LexicalEntry>> sets) {
        StringBuilder setsString = new StringBuilder();
        for (Set<LexicalEntry> sle : sets) {
            setsString.append(displaySetAsString(sle)).append("\n");
        }
        return String.format("\"{\n%s}", setsString);
    }

    public Map<LexicalEntry, Double> fixedSenseUniformPaths(Graph<LexicalEntry, DefaultEdge> g, Collection<Set<LexicalEntry>> ambiguitySets, int ng, int nr, double pe, int maxCircuitLength) {
        Set<DefaultEdge> deSet = g.edgeSet();
        Object[] edgesTable = deSet.toArray();
        DefaultEdge de = (DefaultEdge) edgesTable[81];
        LexicalEntry v1 = g.getEdgeSource(de);
        LexicalEntry v2 = g.getEdgeTarget(de);
        logger.debug("Translation probabilities of {} and {}", v1, v2);
        return senseUniformPaths(g, v1, v2, ambiguitySets, ng, nr, pe, maxCircuitLength);
    }

    public Map<LexicalEntry, Double> randomSenseUniformPaths(Graph<LexicalEntry, DefaultEdge> g, Collection<Set<LexicalEntry>> ambiguitySets, int ng, int nr, double pe, int maxCircuitLength) {
        Set<DefaultEdge> deSet = g.edgeSet();
        int nbEdges = deSet.size();
        Object[] edgesTable = deSet.toArray();
        int r = new Random().nextInt(nbEdges);
        DefaultEdge de = (DefaultEdge) edgesTable[r];
        LexicalEntry v1 = g.getEdgeSource(de);
        LexicalEntry v2 = g.getEdgeTarget(de);
        logger.debug("Translation probabilities of {} and {}", v1, v2);
        return senseUniformPaths(g, v1, v2, ambiguitySets, ng, nr, pe, maxCircuitLength);
    }

    /**
     * Algorithm SenseUniformPaths from "Compiling a Massive, Multilingual Dictionary via Probabilistic inference"
     */
    public Map<LexicalEntry, Double> senseUniformPaths(Graph<LexicalEntry, DefaultEdge> g, LexicalEntry v1, LexicalEntry v2, Collection<Set<LexicalEntry>> ambiguitySets, int ng, int nr, double pe, int maxCircuitLength) {
        /*int ng = 2000 ;
        int nr = 2000 ;
        double pe = 0.6 ;
        int maxCircuitLength = 7 ;
        */

        Map<LexicalEntry, int[]> rp = new HashMap<>();

        Set<LexicalEntry> allVertices = g.vertexSet();
        for (LexicalEntry v : allVertices) {
            // rp[v][i] = 0
            int[] prob = new int[ng];
            for (int i = 0; i < ng; i++) {
                prob[i] = 0;
            }
            rp.put(v, prob);
        }

        for (int i = 0; i < ng; i++) {
            // creation d'un "sample graph" (ng fois)
            Graph<LexicalEntry, DefaultEdge> sampleGraph = sampleGraph(g, pe);

            Collection<LexicalEntry> translationCircuits = new HashSet<LexicalEntry>();
            for (int j = 0; j < nr; j++) {
                translationCircuits.addAll(randomWalk(sampleGraph, v1, v2, ambiguitySets, maxCircuitLength));
            }

            Set<LexicalEntry> allVertex = sampleGraph.vertexSet();
            for (LexicalEntry v : allVertex) {
                if (translationCircuits.contains(v)) {
                    // rp[v][i] = 1
                    int[] tab = rp.get(v);
                    tab[i] = 1;
                    rp.put(v, tab);
                }
            }
        }
        // probabilite que v soit une traduction de v1 et v2 : somme(rp[v][i])/ng
        Map<LexicalEntry, Double> translationProbability = new HashMap<LexicalEntry, Double>();
        for (LexicalEntry v : allVertices) {
            //System.out.print(v+" : ") ;
            int[] tab = rp.get(v);
            int sum = 0;
            for (int i = 0; i < ng; i++) {

                sum = sum + tab[i];
            }
            double prob = sum / (double) ng;
            translationProbability.put(v, prob);
        }
        return translationProbability;
    }

    private Graph<LexicalEntry, DefaultEdge> sampleGraph(Graph<LexicalEntry, DefaultEdge> g, double pe) {
        Graph<LexicalEntry, DefaultEdge> sample = new SimpleGraph<LexicalEntry, DefaultEdge>(DefaultEdge.class);
        Set<LexicalEntry> vertices = g.vertexSet();
        for (LexicalEntry le : vertices) {
            sample.addVertex(le);
        }
        Set<DefaultEdge> edges = g.edgeSet();
        for (DefaultEdge de : edges) {
            double d = Math.random();
            if (d < pe) {
                sample.addEdge(g.getEdgeSource(de), g.getEdgeTarget(de));
            }
        }
        return sample;
    }

    /*
        g has an edge connecting v1 to v2
     */
    private Collection<LexicalEntry> randomWalk(Graph<LexicalEntry, DefaultEdge> g, LexicalEntry v1, LexicalEntry v2, Iterable<Set<LexicalEntry>> ambiguitySets, int maxCircuitLength) {
        int ambiguousVertices = 0;
        Collection<LexicalEntry> translationCircuit = new HashSet<>();
        Collection<LexicalEntry> returnSet = Collections.emptySet();
        translationCircuit.add(v1);
        translationCircuit.add(v2);
        LexicalEntry neighbor = getRandomNeighborNotPicked(g, v1, translationCircuit);
        if (neighbor != null) {

            if (isAmbiguous(neighbor, ambiguitySets)) {
                ambiguousVertices = ambiguousVertices + 1;
            }
            translationCircuit.add(neighbor);
            int i = 0;
            while (ambiguousVertices < 2 && i < maxCircuitLength && !g.containsEdge(neighbor, v2) && !g.containsEdge(v2, neighbor)) {
                neighbor = getRandomNeighborNotPicked(g, neighbor, translationCircuit);
                if (neighbor == null) {
                    return returnSet;
                }
                if (isAmbiguous(neighbor, ambiguitySets)) {
                    ambiguousVertices = ambiguousVertices + 1;
                }
                translationCircuit.add(neighbor);
                i = i + 1;
            }
            if (i < maxCircuitLength && ambiguousVertices < 2) {
                returnSet = translationCircuit;
            }
        }
        return returnSet;
    }

    private LexicalEntry getRandomNeighbor(Graph<LexicalEntry, DefaultEdge> g, LexicalEntry v) {
        LexicalEntry returnNeighbour = null;
        Set<DefaultEdge> deSet = g.edgesOf(v);
        int i = 0;
        int size = deSet.size();
        if (size > 0) {
            int r = randomGenerator.nextInt(size);
            Iterator<DefaultEdge> iterator = deSet.iterator();
            DefaultEdge currentDefaultEdge = iterator.next();
            while (iterator.hasNext() && i != r) {
                currentDefaultEdge = iterator.next();
                i = i + 1;
            }
            if (g.getEdgeSource(currentDefaultEdge).equals(v)) {
                returnNeighbour = g.getEdgeTarget(currentDefaultEdge);
            } else {
                returnNeighbour = g.getEdgeSource(currentDefaultEdge);
            }
        }
        return returnNeighbour;
    }


    private LexicalEntry getRandomNeighborNotPicked(Graph<LexicalEntry, DefaultEdge> g, LexicalEntry v, Collection<LexicalEntry> picked) {
        LexicalEntry neighbor = getRandomNeighbor(g, v);
        if (neighbor != null) {
            if (picked.containsAll(getNeighbors(g, v))) {
                return  null;
            } else {
                while (picked.contains(neighbor)) {
                    neighbor = getRandomNeighbor(g, v);
                }
            }
        }
        return neighbor;
    }

    private Collection<LexicalEntry> getNeighbors(Graph<LexicalEntry, DefaultEdge> g, LexicalEntry v) {
        Collection<LexicalEntry> neighbors = new HashSet<>();
        Set<DefaultEdge> deSet = g.edgesOf(v);
        for (DefaultEdge de : deSet) {
            if (g.getEdgeSource(de).equals(v)) {
                neighbors.add(g.getEdgeTarget(de));
            } else {
                neighbors.add(g.getEdgeSource(de));
            }
        }
        return neighbors;
    }

    private boolean isAmbiguous(LexicalEntry v, Iterable<Set<LexicalEntry>> ambiguitySets) {
        for (Set<LexicalEntry> ambiguitySet : ambiguitySets) {
            if (ambiguitySet.contains(v)) {
                return true;
            }
        }
        return false;
    }
}
