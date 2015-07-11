package org.getalp.lexsema.io.resource;


import org.getalp.lexsema.io.DSODefinitionExpender.DSODefinitionExpender;
import org.getalp.lexsema.io.definitionenricher.TextDefinitionEnricher;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Word;

import java.util.List;


public interface LRLoader {
    public List<Sense> getSenses(Word w);

    public void loadSenses(Document document);

    @SuppressWarnings("BooleanParameter")
    public LRLoader shuffle(boolean shuffle);

    @SuppressWarnings("BooleanParameter")
    public LRLoader extendedSignature(boolean hasExtendedSignature);

    public LRLoader loadDefinitions(boolean loadDefinitions);

    public LRLoader setLoadRelated(boolean loadRelated);

    public LRLoader setStemming(boolean stemming);

    public LRLoader setUsesStopWords(boolean usesStopWords);

	void loadSenses(Document document,
			TextDefinitionEnricher definitionExpender, int profondeur,
			DSODefinitionExpender contexteDSO);
}
