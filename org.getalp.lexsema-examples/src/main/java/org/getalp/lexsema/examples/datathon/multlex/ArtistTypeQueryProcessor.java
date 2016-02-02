package org.getalp.lexsema.examples.datathon.multlex;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.ontolex.Graph;
import org.getalp.lexsema.ontolex.queries.ARQSelectQueryImpl;
import org.getalp.lexsema.ontolex.queries.AbstractQueryProcessor;
import org.getalp.lexsema.util.Language;

import java.util.*;


public class ArtistTypeQueryProcessor extends AbstractQueryProcessor<ArtistType> {


    public ArtistTypeQueryProcessor(Graph graph) {
        super(graph);
        initialize();
    }

    @Override
    protected void defineQuery() {
        setQuery(new ARQSelectQueryImpl());
        String typeVar = "t";
        String lexVar ="l";
        addTriple(Var.alloc(typeVar), getNode("gvp:agentType"),Var.alloc("a"));
        addTriple(Var.alloc("a"),getNode("rdfs:label"), Var.alloc(lexVar));
        addResultVar("t");
        addResultVar("l");
        addResultVar("a");
    }

    @Override
    public List<ArtistType> processResults() {
        Map<String,ArtistType> artistTypeMap = new HashMap<>();
        while (hasNextResult()) {
            QuerySolution qs = nextSolution();
            RDFNode type = qs.get("t");
            String lexicalization = qs.get("l").toString();
            String[] descrStr = lexicalization.split("@");
            String language = "en";
            if(descrStr.length==2){
                language = descrStr[1];
            }
            if(language.equals("en")) {
                String lexString = descrStr[0];
                if (!artistTypeMap.containsKey(type.toString())) {
                    artistTypeMap.put(type.toString(), new ArtistType(type, lexString));
                }
                ArtistType artistType = artistTypeMap.get(type.toString());
                artistType.addLexicalization(Language.fromCode(language), lexString);
            }
        }
        return new ArrayList<>(artistTypeMap.values());
    }
}
