package it.uniroma1.lcl.babelnet;

import it.uniroma1.lcl.jlt.util.Language;

/** 
 * Enumeration of the different sources for the BabelNet senses.
 * 
 * @author navigli, ponzetto
 *
 */
public enum BabelSenseSource
{ 
	/**
	 * Lexicalization from Wikipedia
	 */
	WIKI,

	/**
	 * Lexicalization from a Wikipedia redirection
	 */
	WIKIRED,
	
	/**
	 * Lexicalization from WordNet
	 */
	WN,
	
	/**
	 * Lexicalization from an automatic translation of a Wikipedia concept
	 */
	WIKITR,

	/**
	 * Lexicalization from an automatic translation of a WordNet concept
	 */
	WNTR,
	
	/**
	 * Lexicalization from Open Multilingual WordNet
	 */
	OMWN,
	
	/**
	 * Lexicalization from OmegaWiki
	 */
	OMWIKI,
	
	/**
	 * Lexicalization from Wiktionary
	 */
	WIKT,
	
	/**
	 * Lexicalization from Wikidata
	 */
	WIKIDATA;
	
	public boolean isFromWikipedia()
	{
		 return this == WIKI || this == WIKIRED;
	}
	
	public boolean isTranslation()
	{
		return this == WIKITR || this == WNTR;
	}

	public static BabelLicense getLicense(BabelSenseSource source, Language language)
	{
		switch(source)
		{
		case WIKI: case WIKIRED: case WIKT: case WIKITR: case WIKIDATA: return BabelLicense.CC_BY_SA_30;
		case WN: case WNTR: return BabelLicense.UNRESTRICTED;
		case OMWIKI: return BabelLicense.CC_BY_30;
		case OMWN:
			switch(language)
			{
			case SQ: case FI: case IT: case CA: case GL: case ES:	case SV: return BabelLicense.CC_BY_30;
			case ZH: case DA: case FA: case HE: case JA: case ID: case NO: case PL: case TH: case MS: return BabelLicense.UNRESTRICTED;
			case EU: return BabelLicense.CC_BY_NC_SA_30;
			case AR: case PT: return BabelLicense.CC_BY_SA_30;
			case FR: return BabelLicense.CECILL_C;
			case EL: return BabelLicense.APACHE_20;

			default: return null;
			}
		}
		return null;
	}
	
	
	public static boolean isAutomatic(BabelSenseSource source, Language language)
	{
		switch(source)
		{
		case OMWIKI: case WN: case WIKI: case WIKIRED: case WIKT:  case WIKIDATA: return false;
		case WIKITR: case WNTR: return true;
		case OMWN:
			switch(language)
			{
			case FR: case PT: case JA: case ID: case MS: return true;
			case AR: case IT:  case SQ: case FI:  case CA: case GL: case ES:	case SV: case ZH: case DA: case FA: case HE: 
			case NO: case PL: case TH: case EU: case EL: return false;
			default: return true;
			}
		}
		return true;
	}
	
}
