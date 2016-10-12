package org.getalp.lexsema.ontolex.babelnet;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.getalp.lexsema.ontolex.AbstractLexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.util.Language;

@EqualsAndHashCode(callSuper = false)
@Data
public class TranslationImpl extends AbstractLexicalResourceEntity implements Translation {

    private String gloss;
    private Integer translationNumber;
    private String writtenForm;
    private Language language;

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
                           String writtenForm, Language targetLanguage) {
        super(r, uri, parent);
        this.gloss = gloss;
        this.translationNumber = translationNumber;
        this.writtenForm = writtenForm;
        language = targetLanguage;
    }

    @Override
    public void setLanguage(Language language) {
        this.language = language;
    }

	@Override
	public String getGloss() {
		return gloss;
	}

	@Override
	public void setGloss(String gloss) {
		this.gloss = gloss;
	}

	@Override
	public Integer getTranslationNumber() {
		return translationNumber;
	}

	@Override
	public void setTranslationNumber(Integer translationNumber) {
		this.translationNumber = translationNumber;
	}

	@Override
	public String getWrittenForm() {
		return writtenForm;
	}

	@Override
	public void setWrittenForm(String writtenForm) {
		this.writtenForm = writtenForm;
	}

	@Override
	public Language getLanguage() {
		return language;
	}
}
