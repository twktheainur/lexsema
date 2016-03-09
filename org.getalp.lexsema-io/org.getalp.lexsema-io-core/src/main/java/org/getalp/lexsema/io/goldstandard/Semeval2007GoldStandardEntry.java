package org.getalp.lexsema.io.goldstandard;

/**
 * Semeval 2007 implementation of GoldStandardEntry
 */
public class Semeval2007GoldStandardEntry implements GoldStandardEntry {

    private String textId;
    private String sentenceId;
    private String wordId;
    private String annotation;
    private String comment;

    public Semeval2007GoldStandardEntry(String entryLine) {
        String[] commentSplit = entryLine.split("!!");
        if (commentSplit.length > 1) {
            comment = commentSplit[1];
        }

        String[] fields = commentSplit[0].trim().split(" ");
        textId = fields[0];
        String[] idFields = fields[1].split("\\.");
        sentenceId = idFields[1];
        wordId = idFields[2];

        annotation = "";
        for (int i = 2; i < fields.length; i++) {
            annotation += String.format("%s ", fields[i]);
        }
        annotation = annotation.trim();
    }

	@Override
	public String getTextId() {
		return textId;
	}

	@Override
	public String getSentenceId() {
		return sentenceId;
	}

	@Override
	public String getWordId() {
		return wordId;
	}

	@Override
	public String getAnnotation() {
		return annotation;
	}

	@Override
	public String getComment() {
		return comment;
	}
}
