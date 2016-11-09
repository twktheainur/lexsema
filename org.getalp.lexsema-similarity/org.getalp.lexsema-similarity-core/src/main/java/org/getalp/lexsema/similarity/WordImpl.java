package org.getalp.lexsema.similarity;

import org.getalp.lexsema.util.Language;

import java.util.*;

class WordImpl implements Word {
    private final String id;
    private final String surfaceForm;
    private String textPos;
    private String semanticTag = "";
    private Sentence enclosingSentence = new NullSentence();
    private final List<Word> precedingNonInstances = new ArrayList<>();
    private String lemma;
    private final int begin;
    private final int end;
    private final List<Sense> senses = new ArrayList<>();
    private final Language language;

    WordImpl(String id, String lemma, String surfaceForm, String pos) {
        this(id,lemma,surfaceForm,pos, Language.NONE);
    }


    WordImpl(String id, String lemma, String surfaceForm, String pos, int begin, int end) {
        this(id,lemma,surfaceForm,pos,Language.NONE, begin,end);
    }


    WordImpl(String id, String lemma, String surfaceForm, String pos, Language language) {
        this(id,lemma,surfaceForm,pos,language,0, (surfaceForm!=null)?surfaceForm.length():0);
    }

        WordImpl(String id, String lemma, String surfaceForm, String pos, Language language, int begin, int end){
            this.id = id;
            this.lemma = lemma;
            this.surfaceForm = surfaceForm;
            textPos = pos;
            this.begin = begin;
            this.end = end;
            this.language = language;
        }


        @Override
        public void addPrecedingInstance (Word precedingNonInstance){
            precedingNonInstances.add(precedingNonInstance);
        }

        @Override
        public Sentence getEnclosingSentence () {
            return enclosingSentence;
        }

        @Override
        public void setEnclosingSentence (Sentence enclosingSentence){
            this.enclosingSentence = enclosingSentence;
        }


        @Override
        public String getLemma () {
            return lemma;
        }

        @Override
        public void setLemma (String lemma){
            this.lemma = lemma;
        }

        @Override
        public String getPartOfSpeech () {
            return textPos;
        }

        @Override
        public void setPartOfSpeech (String partOfSpeech){
            textPos = partOfSpeech;
        }

        @Override
        public Language getLanguage () {
            return language;
        }


        @Override
        public String getId () {
            return id;
        }

        @Override
        public String getSurfaceForm () {
            return surfaceForm;
        }

        @Override
        public Iterator<Sense> iterator () {
            return senses.iterator();
        }

        @Override
        public String getSenseAnnotation () {
            return semanticTag;
        }

        @Override
        public void setSemanticTag (String semanticTag){
            this.semanticTag = semanticTag;
        }

        @Override
        public String toString () {
            return String.format("Word (%d -> %d) |%s#%s|",begin, end, lemma, textPos);
        }

        @Override
        public int getBegin () {
            return begin;
        }

        @Override
        public int getEnd () {
            return end;
        }

        @Override
        public Iterable<Word> precedingNonInstances () {
            return Collections.unmodifiableList(precedingNonInstances);
        }

        @Override
        public void loadSenses (Collection < Sense > senses) {
            senses.stream().forEachOrdered(this.senses::add);
        }

        @Override
        public boolean isNull () {
            return false;
        }
    }
