package org.getalp.lexsema.similarity;

import com.hp.hpl.jena.graph.Node;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.NullLexicalSense;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.NullSemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.util.Language;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class SenseImpl implements Sense {
    private SemanticSignature semanticSignature;
    private String id = "";
    private LexicalSense lexicalSense = NullLexicalSense.getInstance();
    private final Map<String, SemanticSignature> relatedSignatures;

    public SenseImpl(String id) {
        this.id = id;
        relatedSignatures = new HashMap<>();
        semanticSignature = NullSemanticSignature.getInstance();
    }

    public SenseImpl(LexicalSense sense) {
        if (!lexicalSense.isNull()) {
            final Node node = sense.getNode();
            id = node.getURI();
        }
        lexicalSense = sense;
        relatedSignatures = new HashMap<>();
        semanticSignature = new SemanticSignatureImpl(lexicalSense.getDefinition());
        setLanguage(lexicalSense.getLanguage());
    }

    @Override
    public String getId() {
        if (lexicalSense.isNull()) {
            return lexicalSense.toString();
        }
        return id;
    }

    @Override
    public Map<String, SemanticSignature> getRelatedSignatures() {
        return Collections.unmodifiableMap(relatedSignatures);
    }

    @Override
    public String toString() {
        if (lexicalSense.isNull()) {
            return String.format("Sense|%s|{'%s'}", id, semanticSignature.toString());
        } else {
            final Node node = getNode();
            final String uri = node.getURI();
            String[] uriParts = uri.split("/");
            String uriId = uriParts[uriParts.length - 1];
            return String.format("Sense|%s|{'%s'}", uriId, semanticSignature.toString());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Sense)) {
            return false;
        }
        Sense sense = (Sense) obj;
        return this ==obj || id.equals(sense.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String getDefinition() {
        return lexicalSense.getDefinition();
    }

    @Override
    public void setDefinition(String definition) {
        lexicalSense.setDefinition(definition);
    }

    @Override
    public String getSenseNumber() {
        return lexicalSense.getSenseNumber();
    }

    @Override
    public void setSenseNumber(String senseNumber) {
        lexicalSense.setSenseNumber(senseNumber);
    }

    @Override
    public LexicalResource getLexicalResource() {
        return lexicalSense.getLexicalResource();
    }

    @Override
    public OntologyModel getOntologyModel() {
        return lexicalSense.getOntologyModel();
    }

    @Override
    public Node getNode() {
        return lexicalSense.getNode();
    }

    @Override
    public LexicalResourceEntity getParent() {
        return lexicalSense.getParent();
    }

    @Override
    public Language getLanguage() {
        return lexicalSense.getLanguage();
    }

    @Override
    public void setLanguage(Language language) {
        lexicalSense.setLanguage(language);
        semanticSignature.setLanguage(language);
    }

    @Override
    public SemanticSignature getSemanticSignature() {
        return semanticSignature;
    }

    @Override
    public void setSemanticSignature(SemanticSignature semanticSignature) {
        this.semanticSignature = semanticSignature;
        semanticSignature.setLanguage(getLanguage());
    }

    @Override
    public void addRelatedSignature(String key, SemanticSignature semanticSignature) {
        relatedSignatures.put(key, semanticSignature);
    }

    @Override
    public double computeSimilarityWith(SimilarityMeasure measure, Sense other) {
        return semanticSignature.computeSimilarityWith(measure, other.getSemanticSignature(),
                getRelatedSignatures(), other.getRelatedSignatures());
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public void setLexicalSense(LexicalSense lexicalSense) {
        this.lexicalSense = lexicalSense;
        final Node node = lexicalSense.getNode();
        id = node.toString();
    }

    @Override
    public int compareTo(LexicalResourceEntity o) {
        if (!lexicalSense.isNull()) {
            final Node node = getNode();
            final String s = node.toString();
            final Node node1 = o.getNode();
            return s.compareTo(node1.toString());
        }
        return -1;
    }
}
