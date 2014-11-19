/**
 *
 */
package org.getalp.lexsema.ontolex.graph;

/**
 * @author tchechem
 */
@SuppressWarnings({"unused", "EmptyClass"})
public class RelationImpl {
    /*private String start;
    private String end;
    private String uri;

    protected RelationImpl(String uri) {
        this.uri = uri;
    }

    public static <S extends LexicalResourceEntity> List<Relation> findRelationsForSource(S n, RelationType t) {
        return null;
    }


  /*  @SuppressWarnings("HardcodedFileSeparator")
    public static <E extends LexicalResourceEntity> List<Relation> findRelationsForTarget(E n, RelationType t,OntologyURIModel uriModel, Graph graph) {

        Collection<Triple> triples = new ArrayList<>(1);
        Collection<String> resultVars = new ArrayList<>(1);

        triples.add(Triple.create(Var.alloc("id"),
                NodeFactory.createURI(uriForName(uriModel,t.getPrefix(), t.getType().toString())),
                NodeFactory.createURI(n.getURI())));
        resultVars.add("id");

        ARQQuery q = new ARQSelectQueryImpl();

        ResultSet rs = q.runQuery(graph, triples, resultVars);
        List<Relation> rels = new ArrayList<>();
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            RDFNode rn = qs.get("id");
            Relation rel = new RelationImpl(rn.toString());
            String stStr = rn.toString();
            stStr = stStr.substring(stStr.lastIndexOf("/") + 1, stStr.length());
            String eStr = n.getURI();
            eStr = eStr.substring(stStr.lastIndexOf("/") + 1, stStr.length());
            rel.setEnd(eStr);
            rel.setStart(stStr.substring(stStr.lastIndexOf("/") + 1, stStr.length()));
            rels.add(rel);
        }
        return rels;
    }

    @Override
    public void setStart(String start) {
        this.start = start;
    }

    @Override
    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String getStart() {
        return start;
    }


    @Override
    public String getEnd() {
        return end;
    }

    @Override
    public String getURI() {
        return uri;
    }*/
}
