package org.getalp.lexsema.axalign.graph;

import org.jgrapht.Graph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public final class ProcessGraph {

    private static final Logger logger = LoggerFactory.getLogger(ProcessGraph.class);

    private final Graph<String, DefaultEdge> graph;
    private final Collection<Set<String>> cliques ;
    private final Collection<Set<String>> ambigSets ;
    private final Set<String> ambigEntries ;
    private final Collection<Set<String>> translationCircuits ;
    private final Set<String> translationEntries ;
    private double avgAmbigDegree ;
    private double avgNonAmbigDegree ;
    private double avgTransDegree ;
    private double avgNonTransDegree ;
    private Map<Integer,int[]> countByDegree ;

    private ProcessGraph(String fileName)throws IOException {
        graph = new SimpleGraph<>(DefaultEdge.class);
        BronKerboschCliqueFinder<String, ? extends DefaultEdge> bk;
        List<Set<String>> cliquesList;
        try (BufferedReader buf = new BufferedReader(new FileReader(fileName))) {
            buf.readLine();
            String s = buf.readLine(); // first line of "graph description"
            while (!s.equals("}")) { // while not last line of the file
                StringBuilder source = new StringBuilder();
                StringBuilder target = new StringBuilder();
                int i = 0;
                while (s.charAt(i) != '"') {
                    i = i + 1;
                }
                i = i + 1;
                while (s.charAt(i) != '"') {
                    source.append(s.charAt(i));
                    i = i + 1;
                }
                i = i + 1;
                while (s.charAt(i) != '"' && s.charAt(i) != ';') {
                    i = i + 1;
                }
                if (s.charAt(i) == '"') {
                    i = i + 1;
                    while (s.charAt(i) != '"') {
                        target.append(s.charAt(i));
                        i = i + 1;
                    }
                    graph.addEdge(source.toString(), target.toString());
                } else {
                    graph.addVertex(source.toString());
                }
                s = buf.readLine();

            }

        }

        bk = new BronKerboschCliqueFinder<>(graph);
        cliques = bk.getAllMaximalCliques() ;

        ambigSets = new ArrayList<>() ;
        cliquesList = (List<Set<String>>) cliques;
        for(int i = 0 ; i<cliquesList.size() ; i++){
            for(int j = i+1 ; j<cliquesList.size() ; j++){
                Set<String> set = getAmbiguitySet(cliquesList.get(i),cliquesList.get(j)) ;
                if(!set.isEmpty() && !ambigSets.contains(set)){
                    ambigSets.add(set) ;
                }
            }
        }

        ambigEntries = new HashSet<>() ;
        for(Set<String> ambigSet : ambigSets){
            ambigEntries.addAll(ambigSet) ;
        }

        translationCircuits = new ArrayList<>() ;
        translationEntries = new HashSet<>() ;
    }

    private Set<String> getAmbiguitySet(Collection<String> clique1, Collection<String> clique2){
        Set<String> ambigSet = new HashSet<>() ;
        for(String le : clique1){
            if(clique2.contains(le)){
                ambigSet.add(le);
            }
        }
        return ambigSet ;
    }

    private int nbVertices(){
        Set<String> vertexSet = graph.vertexSet() ;
        return vertexSet.size();
    }

    private int nbEdges(){
        Set<DefaultEdge> edgeSet = graph.edgeSet() ;
        return edgeSet.size();
    }

    private int nbCliques(){
        return cliques.size() ;
    }

    private int nbAmbigSets(){
        return ambigSets.size() ;
    }

    private int nbAmbigEntries(){
        return ambigEntries.size() ;
    }

    private String seeSet(Iterable<String> set){
        StringBuilder setString = new StringBuilder();
        for(String le : set){
            setString.append(le).append(" , ");
        }
        setString.substring(0,setString.length()-3) ;

        return String.format("(%s)", setString.toString());
    }

    public String seeSets(Collection<Set<String>> sets){
        String setsString = "" ;
        for(Set<String> sle : sets){
            setsString = setsString+seeSet(sle)+"\n" ;
        }
        setsString = "{\n"+setsString+"}";
        return setsString ;
    }

    public Map<String,Double> fixedSenseUniformPaths(int ng, int nr, double pe, int maxCircuitLength, int index){
        Set<DefaultEdge> deSet = graph.edgeSet() ;
        Object[] edgesTable = deSet.toArray();
        DefaultEdge de = (DefaultEdge)edgesTable[index] ;
        String v1 = graph.getEdgeSource(de) ;
        String v2 = graph.getEdgeTarget(de) ;
        System.out.println("Translation probabilities of "+v1+" and "+v2) ;
        return senseUniformPaths(graph,v1,v2,ng,nr,pe,maxCircuitLength) ;
    }

    public Map<String,Double> randomSenseUniformPaths(int ng, int nr, double pe, int maxCircuitLength){
        Set<DefaultEdge> deSet = graph.edgeSet() ;
        int nbEdges = deSet.size() ;
        Object[] edgesTable = deSet.toArray();
        int r = new Random().nextInt(nbEdges) ;
        DefaultEdge de = (DefaultEdge)edgesTable[r] ;
        String v1 = graph.getEdgeSource(de) ;
        String v2 = graph.getEdgeTarget(de) ;
        System.out.println("Translation probabilities of "+v1+" and "+v2) ;
        return senseUniformPaths(graph,v1,v2,ng,nr,pe,maxCircuitLength) ;
    }

    /**
     * Algorithm SenseUniformPaths from "Compiling a Massive, Multilingual Dictionary via Probabilistic inference"
     */
    public Map<String,Double> senseUniformPaths(Graph<String,DefaultEdge> g, String v1, String v2, int ng, int nr, double pe, int maxCircuitLength){

        Map<String,int[]> rp = new HashMap<>() ;

        Set<String> allVertices = g.vertexSet() ;
        for(String v : allVertices) {
            // rp[v][i] = 0
            int[] prob = new int[ng] ;
            for(int i = 0 ; i<ng ; i++){
                prob[i] = 0 ;
            }
            rp.put(v,prob) ;
        }

        for(int i = 0 ; i<ng ; i++){
            // creation d'un "sample graph" (ng fois)
            Graph<String,DefaultEdge> sampleGraph = sampleGraph(g,pe) ;

            Set<String> translationCircuits = new HashSet<String>() ;
            for(int j = 0 ; j<nr ; j++){
                Set<String> circuit = randomWalk(sampleGraph,v1,v2,maxCircuitLength) ;
                if(circuit.size()>0){
                    addCircuit(circuit) ;
                }
                translationCircuits.addAll(circuit) ;
            }
            translationEntries.addAll(translationCircuits) ;
            Set<String> allVertex = sampleGraph.vertexSet() ;
            for(String v : allVertex){
                if(translationCircuits.contains(v)){
                    // rp[v][i] = 1
                    int[] tab = rp.get(v) ;
                    tab[i] = 1 ;
                    rp.put(v,tab) ;
                }
            }
        }
        // probabilite que v soit une traduction de v1 et v2 : somme(rp[v][i])/ng
        Map<String,Double> translationProbability = new HashMap<String,Double>() ;
        for(String v : allVertices) {
            //System.out.print(v+" : ") ;
            int[] tab = rp.get(v) ;
            int sum = 0 ;
            for(int i = 0 ; i<ng ; i++){
                //System.out.print(tab[i]) ;
                sum = sum+tab[i] ;
            }
            //System.out.println() ;
            double prob = (double)sum / (double)ng;
            translationProbability.put(v,prob) ;
        }
        return translationProbability ;
    }

    private Graph<String,DefaultEdge> sampleGraph(Graph<String,DefaultEdge> g, double pe){
        Graph<String,DefaultEdge> sample = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class) ;
        Set<String> vertices = g.vertexSet() ;
        for(String le : vertices) {
            sample.addVertex(le);
        }
        Set<DefaultEdge> edges = g.edgeSet() ;
        for(DefaultEdge de : edges){
            double d = Math.random() ;
            if(d<pe){
                sample.addEdge(g.getEdgeSource(de), g.getEdgeTarget(de));
            }
        }
        return sample ;
    }

    /*
        graph has an edge connecting v1 to v2
     */
    private Set<String> randomWalk(Graph<String,DefaultEdge> g, String v1, String v2, int maxCircuitLength){
        int ambiguousVertices = 0 ;
        Set<String> translationCircuit = new HashSet<String>() ;
        translationCircuit.add(v1) ;
        translationCircuit.add(v2) ;
        String neighbor = getRandomNeighborNotPicked(g, v1,translationCircuit);
        if(neighbor==null){
            return new HashSet<String>() ;
        }
        if(isAmbiguous(neighbor)){
            ambiguousVertices = ambiguousVertices+1 ;
        }
        translationCircuit.add(neighbor) ;
        int i = 0 ;
        while(ambiguousVertices<2 && i<maxCircuitLength && !g.containsEdge(neighbor,v2) && !g.containsEdge(v2,neighbor)) {
            neighbor = getRandomNeighborNotPicked(g,neighbor,translationCircuit) ;
            if(neighbor==null){
                return new HashSet<String>() ;
            }
            if(isAmbiguous(neighbor)){
                ambiguousVertices = ambiguousVertices+1 ;
            }
            translationCircuit.add(neighbor) ;
            i = i+1 ;
        }
        if(i==maxCircuitLength || ambiguousVertices>=2){
            return new HashSet<String>() ;
        }else{
            return translationCircuit ;
        }
    }

    private String getRandomNeighbor(Graph<String,DefaultEdge> g,String v){
        Set<DefaultEdge> deSet = g.edgesOf(v) ;
        int i = 0 ;
        int size = deSet.size() ;
        if(size>0) {
            int r = new Random().nextInt(size);
            for (DefaultEdge de : deSet) {
                if (i == r) {
                    if (g.getEdgeSource(de).equals(v)) {
                        return g.getEdgeTarget(de);
                    } else {
                        return g.getEdgeSource(de);
                    }
                }
                i = i + 1;
            }
        }
        return null ;
    }

    private String getRandomNeighborNotPicked(Graph<String,DefaultEdge> g, String v, Collection<String> picked){
        String neighbor = getRandomNeighbor(g,v);
        if(neighbor == null){
            return null ;
        }
        if(picked.containsAll(getNeighbors(g,v))){
            return null ;
        }else{
            while(picked.contains(neighbor)){
                neighbor = getRandomNeighbor(g,v) ;
            }
            return neighbor ;
        }
    }

    private Collection<String> getNeighbors(Graph<String,DefaultEdge> g, String v){
        Collection<String> neighbors = new HashSet<String>() ;
        Set<DefaultEdge> deSet = g.edgesOf(v) ;
        for(DefaultEdge de : deSet){
            if(g.getEdgeSource(de).equals(v)){
                neighbors.add(g.getEdgeTarget(de)) ;
            }else{
                neighbors.add(g.getEdgeSource(de)) ;
            }
        }
        return neighbors ;
    }

    private boolean isAmbiguous(String v){
        return ambigEntries.contains(v) ;
    }

    private boolean isTranslated(String v){ return translationEntries.contains(v) ;}

    private boolean addCircuit(Set<String> circuit){
        boolean alreadyContained = false ;
        for(Set<String> currentCircuit : translationCircuits){
            if(currentCircuit.containsAll(circuit) && circuit.containsAll(currentCircuit)){
                alreadyContained = true ;
            }
        }
        if(!alreadyContained){
            translationCircuits.add(circuit) ;
        }
        return !alreadyContained ;
    }

    private int countAllCircuits(int ng, int nr, double pe, int maxCircuitLength){
        for(int i = 0; i< nbEdges() ; i++){
            fixedSenseUniformPaths(ng,nr,pe,maxCircuitLength, i) ;
        }
        return translationCircuits.size() ;
    }

    private void computeAvgDegrees(){
        int ambigDegree = 0 ;
        int nonAmbigDegree = 0 ;
        int transDegree = 0 ;
        int nonTransDegree = 0 ;
        for(String s : graph.vertexSet()){
            if(isAmbiguous(s)){
                ambigDegree = ambigDegree + graph.edgesOf(s).size() ;
            }else{
                nonAmbigDegree = nonAmbigDegree + graph.edgesOf(s).size() ;
            }
            if(isTranslated(s)){
                transDegree = transDegree + graph.edgesOf(s).size() ;
            }else{
                nonTransDegree = nonTransDegree + graph.edgesOf(s).size() ;
            }
        }
        avgAmbigDegree = ambigDegree/(double) ambigEntries.size();
        avgNonAmbigDegree = nonAmbigDegree/(double)(graph.vertexSet().size() - ambigEntries.size()) ;
        avgTransDegree = transDegree/(double) translationEntries.size();
        avgNonTransDegree = nonTransDegree/(double)(graph.vertexSet().size() - translationEntries.size()) ;
    }

    public void dispAvgDegrees(){
        System.out.println("Average Degree ambiguous entries : "+avgAmbigDegree+"\nAverage degree non ambiguous entries : "+avgNonAmbigDegree+"\nAverage degree translated entries : "+avgTransDegree+"\nAverage degree non translated entries : "+avgNonTransDegree) ;
    }

    public void computeVertexByDegree(){
        Map<Integer,Set<String>> degToVert = new HashMap<Integer,Set<String>>() ;
        for(String v : graph.vertexSet()){
            int degree = graph.edgesOf(v).size() ;
            if(degToVert.containsKey(degree)){
                Set<String> s = degToVert.get(degree) ;
                s.add(v) ;
                degToVert.put(degree,s) ;
            }else{
                Set<String> s = new HashSet<String>() ;
                s.add(v) ;
                degToVert.put(degree,s) ;
            }
        }
        countByDegree = new HashMap<Integer,int[]>() ;
        for(Integer d : degToVert.keySet()){
            int nbVert = 0 ;
            int nbAmbig = 0 ;
            int nbNonAmbig = 0 ;
            int nbTrans = 0 ;
            int nbNonTrans = 0 ;
            for(String v : degToVert.get(d)){
                nbVert = nbVert+1 ;
                if(isAmbiguous(v)){
                    nbAmbig = nbAmbig+1 ;
                }else{
                    nbNonAmbig = nbNonAmbig + 1 ;
                }
                if(isTranslated(v)){
                    nbTrans = nbTrans+1 ;
                }else{
                    nbNonTrans = nbNonTrans+1 ;
                }
            }
            int[] count = {nbVert,nbAmbig,nbNonAmbig,nbTrans,nbNonTrans} ;
            countByDegree.put(d,count) ;
        }
    }

    public void dispByDegree(){
        for(Integer d : countByDegree.keySet()){
            int[] count = countByDegree.get(d) ;
            System.out.println("Degree "+d+" : vertices "+count[0]+" , ambiguous "+count[1]+" , notAmbiguous "+count[2]+" , translated "+count[3]+" , notTranslated "+count[4]) ;
        }
    }

    public static void main(String args[])throws IOException{
        ProcessGraph graph = new ProcessGraph(args[0]);
        Graph<String,DefaultEdge> g = graph.graph;
        //Collection<Set<String>> cliques = graph.getCliques(graph);
        //Collection<Set<String>> ambigSets = graph.getAmbiguitySets(cliques);
        System.out.println("There are "+graph.nbVertices()+" vertices and "+graph.nbEdges()+" edges.") ;
        System.out.println("\nAmbiguity Sets : "+graph.seeSets(graph.ambigSets)+"\n");
        System.out.println("There are "+graph.nbCliques()+" cliques, "+graph.nbAmbigSets()+" ambiguity sets and "+graph.nbAmbigEntries()+" ambiguous entries.");
        int ng = 2000;
        int nr = 1000;
        double pe = 0.9;
        int maxCircuitLength = 6;
        int countCircuits = graph.countAllCircuits(ng,nr,pe,maxCircuitLength) ;
        System.out.println(countCircuits+" different translation circuits were made in this graph") ;
        System.out.println("There are "+graph.translationEntries.size()+" translation entries : "+graph.seeSet(graph.translationEntries)) ;
        graph.computeAvgDegrees() ;
        graph.dispAvgDegrees() ;
        graph.computeVertexByDegree();
        graph.dispByDegree();
        /*Map<String, Double> prob = graph.randomSenseUniformPaths(ng,nr,pe,maxCircuitLength);
        for (String v : prob.keySet()) {
            double d = prob.get(v) ;
            if(d>0.0) {
                System.out.println(v + " : " + d);
            }
        }*/
    }

}
