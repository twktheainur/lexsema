package org.getalp.lexsema.acceptali.graph.processing;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.jgrapht.Graph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import scala.util.parsing.combinator.lexical.Lexical;

import java.util.*;

public class TranslationProcessing {

    public Collection<Set<LexicalEntry>> getCliques(Graph<LexicalEntry,DefaultEdge> translationGraph){
        BronKerboschCliqueFinder bk = new BronKerboschCliqueFinder(translationGraph) ;
        Collection<Set<LexicalEntry>> cliques = bk.getAllMaximalCliques() ;
        return cliques ;
    }

    public Collection<Set<LexicalEntry>> getAmbiguitySets(Collection<Set<LexicalEntry>> cliques){
        Collection<Set<LexicalEntry>> ambigSets = new ArrayList<Set<LexicalEntry>>() ;
        ArrayList<Set<LexicalEntry>> cliquesList = (ArrayList<Set<LexicalEntry>>) cliques ;
        for(int i = 0 ; i<cliquesList.size() ; i++){
            for(int j = i+1 ; j<cliquesList.size() ; j++){
                Set<LexicalEntry> set = getAmbiguitySet(cliquesList.get(i),cliquesList.get(j)) ;
                if(set.size()>0 && !ambigSets.contains(set)){
                    ambigSets.add(set) ;
                }
            }
        }
        return ambigSets ;
    }

    private Set<LexicalEntry> getAmbiguitySet(Set<LexicalEntry> clique1, Set<LexicalEntry> clique2){
        Set<LexicalEntry> ambigSet = new HashSet<LexicalEntry>() ;
        for(LexicalEntry le : clique1){
            if(clique2.contains(le)){
                ambigSet.add(le);
            }
        }
        return ambigSet ;
    }

    private String seeSet(Set<LexicalEntry> set){
        String setString = "" ;
        for(LexicalEntry le : set){
            setString = setString+le.getLemma()+"_"+le.getNumber()+"_"+le.getPartOfSpeech()+"_"+le.getLanguage().getISO2Code()+" , " ;
        }
        setString = setString.substring(0,setString.length()-3) ;
        setString = "("+setString+")";
        return setString ;
    }

    public String seeSets(Collection<Set<LexicalEntry>> sets){
        String setsString = "" ;
        for(Set<LexicalEntry> sle : sets){
            setsString = setsString+seeSet(sle)+"\n" ;
        }
        setsString = "{\n"+setsString+"}";
        return setsString ;
    }


    /**
     * Algorithm SenseUniformPaths from "Compiling a Massive, Multilingual Dictionary via Probabilistic inference"
     */
    public Map<LexicalEntry,Double> senseUniformPaths(Graph<LexicalEntry,DefaultEdge> g, LexicalEntry v1, LexicalEntry v2, Collection<Set<LexicalEntry>> ambiguitySets){
        int ng = 2000 ; // TODO ng a definir
        int nr = 2000 ; // TODO  nr a definir
        double pe = 0.6 ; // TODO pe a definir
        int maxCircuitLength = 7 ; // TODO maxCircuitLength a definir

        Map<LexicalEntry,int[]> rp = new HashMap<>() ;

        Set<LexicalEntry> allVertices = g.vertexSet() ;
        for(LexicalEntry v : allVertices) {
            // rp[v][i] = 0
            int[] prob = new int[ng] ;
            for(int i = 0 ; i<ng ; i++){
                prob[i] = 0 ;
            }
            rp.put(v,prob) ;
        }

        for(int i = 0 ; i<ng ; i++){
            // creation d'un "sample graph" (ng fois)
            Graph<LexicalEntry,DefaultEdge> sampleGraph = sampleGraph(g,pe) ;

            Set<LexicalEntry> translationCircuits = new HashSet<LexicalEntry>() ;
            for(int j = 0 ; j<nr ; j++){
                translationCircuits.addAll(randomWalk(sampleGraph,v1,v2,ambiguitySets,maxCircuitLength)) ;
            }

            Set<LexicalEntry> allVertex = sampleGraph.vertexSet() ;
            for(LexicalEntry v : allVertex){
                if(translationCircuits.contains(v)){
                    // rp[v][i] = 1
                    int[] tab = rp.get(v) ;
                    tab[i] = 1 ;
                    rp.put(v,tab) ;
                }
            }
        }
        // probabilite que v soit une traduction de v1 et v2 : somme(rp[v][i])/ng
        Map<LexicalEntry,Double> translationProbability = new HashMap<LexicalEntry,Double>() ;
        for(LexicalEntry v : allVertices) {
            //System.out.print(v+" : ") ;
            int[] tab = rp.get(v) ;
            int sum = 0 ;
            for(int i = 0 ; i<ng ; i++){
                //System.out.print(tab[i]) ;
                sum = sum+tab[i] ;
            }
            //System.out.println() ;
            double prob = ((double)sum)/((double)ng) ;
            translationProbability.put(v,prob) ;
        }
        return translationProbability ;
    }

    private Graph<LexicalEntry,DefaultEdge> sampleGraph(Graph<LexicalEntry,DefaultEdge> g, double pe){
        Graph<LexicalEntry,DefaultEdge> sample = new SimpleGraph<LexicalEntry, DefaultEdge>(DefaultEdge.class) ;
        Set<LexicalEntry> vertices = g.vertexSet() ;
        for(LexicalEntry le : vertices) {
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
        g has an edge connecting v1 to v2
     */
    private Set<LexicalEntry> randomWalk(Graph<LexicalEntry,DefaultEdge> g, LexicalEntry v1, LexicalEntry v2, Collection<Set<LexicalEntry>> ambiguitySets, int maxCircuitLength){
        int ambiguousVertices = 0 ;
        Set<LexicalEntry> translationCircuit = new HashSet<LexicalEntry>() ;
        translationCircuit.add(v1) ;
        translationCircuit.add(v2) ;
        LexicalEntry neighbor = getRandomNeighborNotPicked(g, v1,translationCircuit);
        if(neighbor==null){
            return new HashSet<LexicalEntry>() ;
        }
        if(isAmbiguous(neighbor,ambiguitySets)){
            ambiguousVertices = ambiguousVertices+1 ;
        }
        translationCircuit.add(neighbor) ;
        int i = 0 ;
        while(ambiguousVertices<2 && i<maxCircuitLength && !g.containsEdge(neighbor,v2) && !g.containsEdge(v2,neighbor)) {
            neighbor = getRandomNeighborNotPicked(g,neighbor,translationCircuit) ;
            if(neighbor==null){
                return new HashSet<LexicalEntry>() ;
            }
            if(isAmbiguous(neighbor,ambiguitySets)){
                ambiguousVertices = ambiguousVertices+1 ;
            }
            translationCircuit.add(neighbor) ;
            i = i+1 ;
        }
        if(i==maxCircuitLength || ambiguousVertices>=2){
            return new HashSet<LexicalEntry>() ;
        }else{
            return translationCircuit ;
        }
    }

    private LexicalEntry getRandomNeighbor(Graph<LexicalEntry,DefaultEdge> g,LexicalEntry v){
        Set<DefaultEdge> deSet = g.edgesOf(v) ;
        int i = 0 ;
        int r = (new Random()).nextInt(deSet.size());
        for(DefaultEdge de : deSet){
            if(i==r){
                if(g.getEdgeSource(de).equals(v)){
                    return g.getEdgeTarget(de) ;
                }else{
                    return g.getEdgeSource(de) ;
                }
            }
            i = i+1 ;
        }
        return null ;
    }

    private LexicalEntry getRandomNeighborNotPicked(Graph<LexicalEntry,DefaultEdge> g,LexicalEntry v, Set<LexicalEntry> picked){
        LexicalEntry neighbor = getRandomNeighbor(g,v);
        if(picked.containsAll(getNeighbors(g,v))){
            return null ;
        }else{
            while(picked.contains(neighbor)){
                neighbor = getRandomNeighbor(g,v) ;
            }
            return neighbor ;
        }
    }

    private Set<LexicalEntry> getNeighbors(Graph<LexicalEntry,DefaultEdge> g,LexicalEntry v){
        Set<LexicalEntry> neighbors = new HashSet<LexicalEntry>() ;
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

    private boolean isAmbiguous(LexicalEntry v, Collection<Set<LexicalEntry>> ambiguitySets){
        for(Set<LexicalEntry> ambiguitySet : ambiguitySets){
            if(ambiguitySet.contains(v)){
                return true ;
            }
        }
        return false ;
    }
}
