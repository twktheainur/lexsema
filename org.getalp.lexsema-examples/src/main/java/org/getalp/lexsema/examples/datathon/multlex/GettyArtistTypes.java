package org.getalp.lexsema.examples.datathon.multlex;


import org.getalp.lexsema.examples.datathon.multlex.lexicalization.Confidence;
import org.getalp.lexsema.examples.datathon.multlex.lexicalization.Lexicalization;
import org.getalp.lexsema.examples.datathon.multlex.lexicalization.LexicalizationAlternatives;
import org.getalp.lexsema.ontolex.graph.DefaultGraph;
import org.getalp.lexsema.ontolex.Graph;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaTDBStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.getalp.lexsema.translation.*;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GettyArtistTypes {

    /**
     * ainuros@outlook.com account
     */
    public static final String BING_APP_ID = "dbnary_hyper";
    public static final String BING_APP_KEY = "IecT6H4OjaWo3OtH2pijfeNIx1y1bML3grXz/Gjo/+w=";

    public static final String YANDEX_KEY = "trnsl.1.1.20150612T083053Z.116892fa94abf4c3.41fa35d9770a4148842667b6aaa1cba5a78e40fa";

    public static final String BAIDU_KEY = "HmnrXaDYZ8XTn2NnGPU4kYbT";

    private static final String ONTOLOGY_PROPERTIES = "data/getty.properties";
    private static Logger logger = LoggerFactory.getLogger(GettyArtistTypes.class);
    private List<ArtistType> artistTypes;
    private List<Translator> translators = new ArrayList<>();

    public void load() throws IOException {
        OntologyModel model = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        Graph graph = new DefaultGraph("//vocab.getty.edu/ontology", model);
        ArtistTypeQueryProcessor artistTypeQueryProcessor = new ArtistTypeQueryProcessor(graph);
        artistTypeQueryProcessor.runQuery();
        artistTypes = artistTypeQueryProcessor.processResults();
        /*artistTypes = new ArrayList<>();
        ArtistType artistType = new ArtistType(null,"abstract artists");
        artistType.addLexicalization(Language.ENGLISH,"abstract artists@en");
        artistTypes.add(artistType);*/
    }

    public void process() throws IOException {
        FileWriter fileWriter = new FileWriter("output.tsv");
        PrintWriter pw = new PrintWriter(fileWriter);
        for(ArtistType artistType: artistTypes){
            GettyArtistTypeMultilingualLexicalizationGenerator gettyArtistTypeMultilingualLexicalizationGenerator = new
                    GettyArtistTypeMultilingualLexicalizationGenerator(artistType.getLexicalizationAlternatives());

            for(Translator translator: translators) {
                gettyArtistTypeMultilingualLexicalizationGenerator.registerTranslator(translator);
            }

            LexicalizationAlternatives lexicalizationAlternatives = gettyArtistTypeMultilingualLexicalizationGenerator.computeLexicalizations(artistType.getWrittenForm());

            Map<Language, List<Lexicalization>> map = lexicalizationAlternatives.computeDistribution();
            for(Language l : map.keySet()){
                List<Lexicalization> lexicalizations = map.get(l);
                if(!lexicalizations.isEmpty()) {
                    for (Lexicalization lexicalization : lexicalizations) {
                        String id= generateID(artistType.getWrittenForm());
                        String targetId = generateID(lexicalization.getLexicalization());
                        pw.println(artistType.getWrittenForm() +"\t"
                                +id+"\t"+ id+"_sense"+"\ttranslation_"+targetId + "\t"+ targetId+"_sense\t"+ targetId+"\t\""+ lexicalization.getLexicalization()+ "\"@"+l.getISO2Code() +"\t"+ Confidence.fromTreshold(lexicalization.getConfidence()));
                    }
                }
                pw.flush();
            }
        }
        pw.close();
    }


    public void addTranslator(Translator translator){
        translators.add(translator);
    }

    public String generateID(String writtenForm){
        return  writtenForm.replaceAll(" ","_").trim().toLowerCase()+"_artist";
    }

    public static void main(String[] args) throws IOException {
        Store store = new JenaTDBStore("/Users/tchechem/full/ulan");
        //Store store = new JenaRemoteSPARQLStore("http://vocab.getty.edu/sparql");
        StoreHandler.registerStoreInstance(store);
        StoreHandler.DEBUG_ON = true;
        GettyArtistTypes artistLabels = new GettyArtistTypes();
        artistLabels.addTranslator(new CachedTranslator("googletr",new GoogleWebTranslator()));
        //artistLabels.addTranslator(new CachedTranslator("baidutr",new BaiduAPITranslator(BAIDU_KEY)));
        artistLabels.addTranslator(new CachedTranslator("yandextr",new YandexAPITranslator(YANDEX_KEY)));
        artistLabels.addTranslator(new CachedTranslator("bingtr",new BingAPITranslator(BING_APP_ID,BING_APP_KEY)));
        //artistLabels.addTranslator(new CachedTranslator("apertiumtr",new ApertiumWebTranslator()));

        artistLabels.load();
        artistLabels.process();

    }
}
