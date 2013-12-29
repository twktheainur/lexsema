/**
 *
 */
package org.getalp.lexsema.ontology.graph.defaultimpl;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.sparql.core.Var;
import org.getalp.lexsema.lexicalresource.LexicalResource;
import org.getalp.lexsema.ontology.graph.Node;
import org.getalp.lexsema.ontology.graph.Relation;
import org.getalp.lexsema.ontology.graph.queries.ARQQuery;
import org.getalp.lexsema.ontology.graph.queries.ARQSelectQuery;

import java.util.ArrayList;
import java.util.List;


public abstract class DefaultNode implements Node {
    private RDFNode node;
    private List<Relation> propertyCache;
    private LexicalResource lexicalResource;

    protected DefaultNode(String uri, LexicalResource lr) {
        lexicalResource = lr;
        node = load(uri);
        lexicalResource.parseURI(this);
    }

    private void loadProperties() {
        if (propertyCache == null) {
            propertyCache = new ArrayList<>();
        }
        if (propertyCache.isEmpty()) {

        }
    }

    @Override
    public List<Relation> getRelated() {
        if (propertyCache == null) {
            loadProperties();
        }
        return propertyCache;
    }

    @Override
    public LexicalResource getLexicalResource() {
        return lexicalResource;
    }

    protected RDFNode load(String uri) {
        Triple match = Triple.create(
                Var.alloc("s"),
                Var.alloc("r"),
                NodeFactory.createURI(uri));

        ARQQuery q = new ARQSelectQuery();
        q.addToWhereStatement(match);
        ResultSet rs = q.runQuery(1, "s");

        RDFNode result;

        if (rs.hasNext()) {
            QuerySolution sol = rs.next();
            result = sol.get("s");
        } else {
            throw new NotFoundException("uri");
        }
        return result;
    }

    @Override
    public String getURI() {
        return node.toString();
    }
}
