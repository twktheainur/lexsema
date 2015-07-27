package org.getalp.lexsema.similarity;

import com.hp.hpl.jena.graph.Node;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.util.Language;

import java.util.Collections;
import java.util.Map;


public final class NullSense implements Sense {
    private static final Sense ourInstance = new NullSense();

    public static Sense getInstance() {
        return ourInstance;
    }

    private NullSense() {
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public Map<String, SemanticSignature> getRelatedSignatures() {
        return Collections.emptyMap();
    }

    @Override
    public SemanticSignature getSemanticSignature() {
        return null;
    }

    @Override
    public void setSemanticSignature(SemanticSignature semanticSignature) {
    }

    @Override
    public void setLexicalSense(LexicalSense lexicalSense) {
    }

    @Override
    public void addRelatedSignature(String key, SemanticSignature semanticSignature) {
    }

    @Override
    public double computeSimilarityWith(SimilarityMeasure measure, Sense other) {
        return 0;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String getDefinition() {
        return "";
    }

    @Override
    public void setDefinition(String definition) {

    }

    @Override
    public String getSenseNumber() {
        return "";
    }

    @Override
    public void setSenseNumber(String senseNumber) {

    }

    @Override
    public LexicalResource getLexicalResource() {
        return null;
    }

    @Override
    public OntologyModel getOntologyModel() {
        return null;
    }

    @Override
    public Node getNode() {
        return null;
    }

    @Override
    public LexicalResourceEntity getParent() {
        return null;
    }

    @Override
    public Language getLanguage() {
        return Language.UNSUPPORTED;
    }

    @Override
    public void setLanguage(Language language) {

    }

    @Override
    public int compareTo(LexicalResourceEntity o) {
        return 0;
    }
}
