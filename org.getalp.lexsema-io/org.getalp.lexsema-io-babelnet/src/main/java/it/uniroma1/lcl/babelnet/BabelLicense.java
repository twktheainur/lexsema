package it.uniroma1.lcl.babelnet;

import java.io.IOException;

/**
 * License information for a BabelNet item
 * 
 * @author navigli, vannella
 */
public enum BabelLicense
{
	UNRESTRICTED("UNR"), 
	CC_BY_SA_30("CBS30"), 
	CC_BY_30("CB30"), 
	CECILL_C("CEC"),
	APACHE_20("APCH20"),
	CC_BY_NC_SA_30("CBNS30"); 

	private String shortName;

	BabelLicense(String shortName)
	{
		this.shortName = shortName;
	}

	public String getShortName()
	{
		return shortName;
	}

	public static BabelLicense getLongName(String shortName) throws IOException
	{
		if(shortName.startsWith("bn")) return BabelLicense.UNRESTRICTED;
		for (BabelLicense bl : BabelLicense.values())
			if (bl.shortName.equals(shortName))
				return bl;
		throw new IOException(shortName + " is not a shortName of BabelLicense");
	}
}
