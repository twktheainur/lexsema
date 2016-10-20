package org.getalp.lexsema.similarity;


import com.hp.hpl.jena.graph.Node;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.util.Language;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class NullWord implements Word{

    @Override
    public void addPrecedingInstance(Word precedingNonInstance) {

    }

    @Override
    public Sentence getEnclosingSentence() {
        return new NullSentence();
    }

    @Override
    public void setEnclosingSentence(Sentence enclosingSentence) {
    }

    @Override
    public void setLexicalEntry(LexicalEntry le) {
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getSurfaceForm() {
        return "";
    }

    @Override
    public String getSenseAnnotation() {
        return "";
    }

    @Override
    public void setSemanticTag(String semanticTag) {

    }

    @Override
    public int getBegin() {
        return 0;
    }

    @Override
    public int getEnd() {
        return 0;
    }

    @Override
    public Iterable<Word> precedingNonInstances() {
        return Collections.emptyList();
    }

    @Override
    public void loadSenses(Collection<Sense> senses) {

    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public Iterator<Sense> iterator() {
        final List<Sense> emptyList = Collections.emptyList();
        return emptyList.iterator();
    }

    @Override
    public String getLemma() {
        return "";
    }

    @Override
    public void setLemma(String lemma) {

    }

    @Override
    public String getPartOfSpeech() {
        return "";
    }

    @Override
    public void setPartOfSpeech(String partOfSpeech) {

    }

    @Override
    public int getNumber() {
        return 0;
    }

    @Override
    public void setNumber(int number) {

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
