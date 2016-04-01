package org.getalp.lexsema.acceptali.graph.writer;

import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.acceptali.closure.writer.TranslationClosureWriter;
import org.getalp.lexsema.acceptali.graph.generator.TranslationGraphGenerator;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.util.Language;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

public class TranslationGraphWriterImpl implements TranslationGraphWriter {
    private File targetDirectory;

    private File lexicalEntryGraph ;

    public TranslationGraphWriterImpl(String targetDirectory) {
        this.targetDirectory = new File(targetDirectory);
        if (!this.targetDirectory.exists()) {
            this.targetDirectory.mkdirs();
        }

        lexicalEntryGraph = new File(targetDirectory + File.separator + "LexicalEntries.dot");

    }

    public void writeTranslation(Graph<LexicalEntry,DefaultEdge> g) {
        try {
            PrintWriter entryWriter = new PrintWriter(lexicalEntryGraph);

            entryWriter.println("graph{") ;
            Set<LexicalEntry> colVertex = g.vertexSet() ;
            for(LexicalEntry le : colVertex) {
                writeLexicalEntryVertex(le,entryWriter) ;
            }
            Set<DefaultEdge> colEdges = g.edgeSet() ;
            for(DefaultEdge de : colEdges){
                writeLexicalEntryEdge(g,de,entryWriter) ;
            }
            entryWriter.print("}") ;
            entryWriter.close();
        } catch (FileNotFoundException ignored) {
        }


    }

    private String writeLexicalEntry(Language language, LexicalEntry lexicalEntry) {
        String output = lexicalEntry.getLemma()+"_"+lexicalEntry.getNumber()+"\\n"+language.getISO2Code();
        return output ;
    }

    private void writeLexicalEntryVertex(LexicalEntry le, PrintWriter entryWriter){
        String leVertex = writeLexicalEntry(le.getLanguage(),le);
        String vertex = leVertex+" ;" ;
        entryWriter.println(vertex) ;
    }

    private void writeLexicalEntryEdge(Graph<LexicalEntry,DefaultEdge> g, DefaultEdge de, PrintWriter entryWriter){
        String source = writeLexicalEntry(g.getEdgeSource(de).getLanguage(),g.getEdgeSource(de));
        String target = writeLexicalEntry(g.getEdgeTarget(de).getLanguage(),g.getEdgeTarget(de));
        String edge = source+" -- "+target+" ;" ;
        entryWriter.println(edge) ;
    }

}