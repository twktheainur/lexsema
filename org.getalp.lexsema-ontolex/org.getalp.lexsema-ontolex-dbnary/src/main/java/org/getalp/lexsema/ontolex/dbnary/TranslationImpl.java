package org.getalp.lexsema.ontolex.dbnary;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.getalp.lexsema.ontolex.AbstractLexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;

@EqualsAndHashCode(callSuper = false)
@Data
public class TranslationImpl extends AbstractLexicalResourceEntity implements Translation {

    private String gloss;
    private Integer translationNumber;
    private String writtenForm;
    private String language;

    /**
     * Constructor for a DBNary Translation
     *
     * @param r      The lexical resource to which the entity belongs
     * @param uri    The uri of the entity
     * @param parent The parent entity
     */
    @SuppressWarnings("ConstructorWithTooManyParameters")
    public TranslationImpl(LexicalResource r, String uri, LexicalResourceEntity parent,
                           String gloss, int translationNumber,
                           String writtenForm, String targetLanguage) {
        super(r, uri, parent);
        this.gloss = gloss;
        this.translationNumber = translationNumber;
        this.writtenForm = writtenForm;
        language = targetLanguage;
    }
}
