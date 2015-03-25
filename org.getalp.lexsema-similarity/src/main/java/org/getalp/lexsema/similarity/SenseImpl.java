package org.getalp.lexsema.similarity;

import com.hp.hpl.jena.graph.Node;
import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class SenseImpl implements Sense {
    SemanticSignature semanticSignature;
    private String id;
    private LexicalSense lexicalSense;
    private Map<String, SemanticSignature> relatedSignatures;

    public SenseImpl(String id) {
        this.id = id;
        relatedSignatures = new HashMap<>();
    }

    public SenseImpl(LexicalSense sense) {
        if (lexicalSense != null) {
            id = sense.getNode().getURI();
        }
        lexicalSense = sense;
        relatedSignatures = new HashMap<>();
    }

    @Override
    public String getId() {
        if(lexicalSense!=null){
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
        if (lexicalSense != null) {
            String[] uriParts = getNode().getURI().split("/");
            String id = uriParts[uriParts.length - 1];
            return String.format("Sense|%s|{'%s'}", id, semanticSignature.toString());
        } else {
            return String.format("Sense|%s|{'%s'}", id, semanticSignature.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sense)) {
            return false;
        }

        Sense sense = (Sense) o;

        return id.equals(sense.getId());
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
    public void setSenseNumber(String i) {
        lexicalSense.setSenseNumber(i);
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
        if (lexicalSense != null) {
            return lexicalSense.getParent();
        }
        return null;
    }

    @Override
    public Language getLanguage() {
        if (lexicalSense == null) {
            return Language.UNSUPPORTED;
        } else {
            return lexicalSense.getLanguage();
        }
    }

    @Override
    public void setLanguage(Language language) {
        if (lexicalSense != null) {
            lexicalSense.setLanguage(language);
        }
    }

    @Override
    public SemanticSignature getSemanticSignature() {
        return semanticSignature;
    }

    @Override
    public void setSemanticSignature(SemanticSignature semanticSignature) {
        this.semanticSignature = semanticSignature;
    }

    @Override
    public void addRelatedSignature(String key, SemanticSignature semanticSignature) {
        relatedSignatures.put(key, semanticSignature);
    }

    @Override
    public void setLexicalSense(LexicalSense lexicalSense) {
        this.lexicalSense = lexicalSense;
        id = lexicalSense.getNode().toString();
    }

    @Override
    public int compareTo(LexicalResourceEntity o) {
        if(lexicalSense!=null) {
            return getNode().toString().compareTo(o.getNode().toString());
        }
        return -1;
    }
}
