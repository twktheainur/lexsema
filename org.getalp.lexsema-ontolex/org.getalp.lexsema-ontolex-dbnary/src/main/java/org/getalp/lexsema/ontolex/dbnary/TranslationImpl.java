package org.getalp.lexsema.ontolex.dbnary;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.ontolex.AbstractLexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;

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
                           String writtenForm, String targetLanguage) {
        super(r, uri, parent);
        this.gloss = gloss;
        this.translationNumber = translationNumber;
        this.writtenForm = removeLanguageTag(writtenForm);
        language = convertLexvoLanguageURI(targetLanguage);
    }

    private Language convertLexvoLanguageURI(String languageURI) {
        String[] uriComponents = languageURI.split("/");
        return Language.fromCode(uriComponents[uriComponents.length - 1]);

    }

    private String removeLanguageTag(String input) {
        return input.split("@")[0];
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

	@Override
	public void setLanguage(Language language) {
		this.language = language;
	}
}
