package org.getalp.lexsema.io.resource.babelnet;


import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.POS;
import it.uniroma1.lcl.babelnet.*;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurus;
import org.getalp.lexsema.similarity.*;
import org.getalp.lexsema.similarity.cache.SenseCache;
import org.getalp.lexsema.similarity.cache.SenseCacheImpl;
import org.getalp.lexsema.similarity.signatures.DefaultSemanticSignatureFactory;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("OverlyCoupledClass")
public class BabelNetAPILoader implements LRLoader {
    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.DEFAULT;
    private static final Logger logger = LoggerFactory.getLogger(BabelNetAPILoader.class);

    private final SenseCache senseCache;
    private final BabelNet babelNet;
    private final Language language;
    private boolean shuffle;
    private boolean hasExtendedSignature = true;
    private boolean loadDefinitions = true;
    private boolean loadRelated;

    public BabelNetAPILoader(String configurationFilePath, final Language language) {
        senseCache = SenseCacheImpl.getInstance();
        this.language = language;
        BabelNetConfiguration.getInstance().setConfigurationFile(new File(configurationFilePath));
        babelNet = BabelNet.getInstance();
    }

    public BabelNetAPILoader(final Language language) {
        senseCache = SenseCacheImpl.getInstance();
        this.language = language;
        babelNet = BabelNet.getInstance();
    }

    private List<Sense> getSenses(String lemma, String pos, Word w) {
        if (lemma == null) {
            return new ArrayList<>();
        }
        List<Sense> senses = new ArrayList<>();
        try {
            List<BabelSense> babelSenses =
                    babelNet.getSenses(toBabelNetLanguage(language),
                            lemma, posToWordnet(pos));


            for (BabelSense bs : babelSenses) {
                SemanticSignature signature = DefaultSemanticSignatureFactory.DEFAULT.createSemanticSignature();
                if (loadDefinitions) {
                    StringBuilder def = new StringBuilder();
                    List<BabelGloss> glosses = bs.getSynset().getGlosses(toBabelNetLanguage(language));
                    for (BabelGloss bg : glosses) {
                        def.append(" ").append(bg.getGloss());
                    }
                    addToSignature(signature, def.toString());
                }

                Sense sense = DOCUMENT_FACTORY.createSense(bs.getSynset().getId());

                if (loadRelated) {
                    Map<IPointer, List<BabelSynset>> related = bs.getSynset().getRelatedMap();
                    for (IPointer p : related.keySet()) {
                        for (BabelSynset babelSynset : related.get(p)) {
                            StringBuilder localDef = new StringBuilder();
                            List<BabelGloss> relatedGlosses = babelSynset.getGlosses(toBabelNetLanguage(language));
                            for (BabelGloss rbg : relatedGlosses) {
                                localDef.append(rbg.getGloss());
                            }
                            SemanticSignature localSignature = DefaultSemanticSignatureFactory.DEFAULT.createSemanticSignature();
                            addToSignature(localSignature, localDef.toString());
                            if (hasExtendedSignature) {
                                signature.appendSignature(localSignature);
                            }
                            sense.addRelatedSignature(p.getSymbol(), localSignature);
                        }
                    }
                }
                sense.setSemanticSignature(signature);
                senses.add(sense);
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }

        return senses;
    }

    @Override
    public List<Sense> getSenses(Word w) {
        List<Sense> senses;
        senses = senseCache.getSenses(w);
        if (senses == null) {
            if (w != null) {
                senses = getSenses(w.getLemma(), w.getPartOfSpeech(), w);
            }
            if (shuffle && senses != null) {
                Collections.shuffle(senses);
            }
            senseCache.addToCache(w, senses);
        }
        return senses;
    }

    @Override
    public Map<Word, List<Sense>> getAllSenses() {
        Map<Word, List<Sense>> allSenses = new HashMap<>();
        babelNet.getLexiconIterator().forEachRemaining(w -> {
            Word word = DOCUMENT_FACTORY.createWord(w.getId(),w.getWord(),w.getWord(),w.getPOS().toString());
                allSenses.put(word, getSenses(w.getWord(), w.getPOS().toString(), word));
        });
        return allSenses;
    }

    private void addToSignature(SemanticSignature signature, String def) {
        StringTokenizer st = new StringTokenizer(def, " ", false);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            signature.addSymbol(token, 1.0);
        }
    }

    private POS posToWordnet(final String pos) {
        POS finalpos = POS.NOUN;
        if (pos.toLowerCase().startsWith("n")) {
            finalpos = POS.NOUN;
        } else if (pos.toLowerCase().startsWith("v")) {
            finalpos = POS.VERB;
        } else if (pos.toLowerCase().startsWith("r") || pos.toLowerCase().startsWith("adv")) {
            finalpos = POS.ADVERB;
        } else if (pos.toLowerCase().startsWith("a") || pos.toLowerCase().startsWith("adj")) {
            finalpos = POS.ADJECTIVE;
        }
        return finalpos;
    }

    private it.uniroma1.lcl.jlt.util.Language toBabelNetLanguage(Language language) {
        return it.uniroma1.lcl.jlt.util.Language.valueOf(language.getISO2Code().toUpperCase());
    }

    @Override
    public void loadSenses(Document document) {
        for (Word w : document) {
            document.addWordSenses(getSenses(w));
        }
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader shuffle(boolean shuffle) {
        this.shuffle = shuffle;
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader extendedSignature(boolean hasExtendedSignature) {
        this.hasExtendedSignature = hasExtendedSignature;
        return this;
    }

    @Override
    public LRLoader loadDefinitions(boolean loadDefinitions) {
        this.loadDefinitions = loadDefinitions;
        return this;
    }

    @Override
    public LRLoader loadRelated(boolean loadRelated) {
        this.loadRelated = loadRelated;
        return this;
    }

    @Override
    public LRLoader stemming(boolean stemming) {
        return this;
    }

    @Override
    public LRLoader filterStopWords(boolean usesStopWords) {
        return this;
    }

    @Override
    public LRLoader addThesaurus(AnnotatedTextThesaurus thesaurus) {
        return this;
    }

    @Override
    public LRLoader index(boolean useIndex) {
        return this;
    }

    @Override
    public LRLoader distributed(boolean isDistributed) {
        return this;
    }
}
