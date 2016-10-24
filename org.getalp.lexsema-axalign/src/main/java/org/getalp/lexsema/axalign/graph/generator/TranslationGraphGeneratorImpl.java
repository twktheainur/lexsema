package org.getalp.lexsema.axalign.graph.generator;

import org.getalp.lexsema.axalign.graph.tools.GraphIOTools;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Translation;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.util.Language;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.List;

public class TranslationGraphGeneratorImpl implements TranslationGraphGenerator {
    private final DBNary dbNary;
    private final LexicalEntry lexicalEntry;

    private boolean includeStartingSet;

    private TranslationGraphGeneratorImpl(final DBNary dbNary, final LexicalEntry lexicalEntry) {
        this.dbNary = dbNary;
        this.lexicalEntry = lexicalEntry;
    }

    public static TranslationGraphGenerator createTranslationGraphGenerator(final DBNary dbNary, final LexicalEntry lexicalEntry) {
        return new TranslationGraphGeneratorImpl(dbNary, lexicalEntry).includeStartingSet(true);
    }

    @Override
    public Graph<LexicalEntry,DefaultEdge> generateGraph(int degree) {
        return recurseTranslation(lexicalEntry, lexicalEntry.getLanguage(), degree, true);
    }

    @Override
    public Graph<LexicalEntry,DefaultEdge> generateGraph() {
        return generateGraph(1);
    }

    @SuppressWarnings("all")
    private Graph<LexicalEntry,DefaultEdge> recurseTranslation(LexicalEntry entity, Language startingLanguage, int degree, boolean topLevel) {
        //LexicalResourceTranslationClosure<LexicalSense> closure = new LexicalResourceTranslationClosureImpl();
        Graph<LexicalEntry,DefaultEdge> translationGraph = new SimpleGraph<LexicalEntry,DefaultEdge>(DefaultEdge.class) ;
        if (degree >= 0) {
            Language language = entity.getLanguage();
            // useless as LexicalEntry is wanted and not LexicalSense
            /*if (includeStartingSet || !includeStartingSet && !language.equals(startingLanguage)) {
                List<LexicalSense> senses = dbNary.getLexicalSenses(entity);
                closure.addSenses(language, entity, senses);
            }*/
            List<Translation> sourceTranslations = dbNary.getTranslations(entity, language);
            String pos = entity.getPartOfSpeech();
            //translationGraph.addVertex(entity);
            for (Translation translation : sourceTranslations) {
                List<LexicalEntry> entries = getTargetEntries(translation, startingLanguage);
                for (LexicalEntry le : entries) {
                    if (le.getPartOfSpeech().equals(pos)) {
                        //LexicalResourceTranslationClosure<LexicalSense> localClosure;
                        Graph<LexicalEntry,DefaultEdge> localTranslation = new SimpleGraph<LexicalEntry, DefaultEdge>(DefaultEdge.class);
                        /*boolean b =*/ localTranslation.addVertex(entity);
                        //System.out.println("ajout de "+entity+" -> "+b) ;
                        /*b =*/ localTranslation.addVertex(le);
                        //System.out.println("ajout de "+le+" -> "+b) ;
                        localTranslation.addEdge(entity,le) ;
                        if (topLevel && includeStartingSet) {
                            //localClosure = recurseClosure(le, startingLanguage, degree, false);
                            localTranslation = GraphIOTools.importGraph(localTranslation,recurseTranslation(le, startingLanguage, degree, false)) ;
                        } else {
                            //localClosure = recurseClosure(le, startingLanguage, degree - 1, false);
                            localTranslation = GraphIOTools.importGraph(localTranslation,recurseTranslation(le, startingLanguage, degree - 1, false));
                        }
                        //closure.importClosure(localClosure);
                        translationGraph = GraphIOTools.importGraph(translationGraph,localTranslation) ;
                    }
                }
            }
        }
        //return closure;
        return translationGraph ;
    }

    private List<LexicalEntry> getTargetEntries(Translation translation, Language startingLanguage) {
        Language lang = translation.getLanguage();
        if (lang != null && !lang.equals(startingLanguage)) {
            try {
                                /*
                                 * Removing language tag...
                                 */
                String writtenForm = translation.getWrittenForm();
                if (writtenForm.contains("@")) {
                    writtenForm = writtenForm.split("@")[0];
                }
                                /*
                                 * Removing tonic accent marker (Russian...)
                                 */
                writtenForm = writtenForm.replace("́", "");
                Vocable tv = dbNary.getVocable(writtenForm, lang);

                return dbNary.getLexicalEntries(tv);
            } catch (NoSuchVocableException ignored) {
            }
        }
        return new ArrayList<>();
    }


    @SuppressWarnings({"PublicMethodNotExposedInInterface", "BooleanParameter"})
    private TranslationGraphGenerator includeStartingSet(boolean includeStartingSet) {
        this.includeStartingSet = includeStartingSet;
        return this;
    }
}
