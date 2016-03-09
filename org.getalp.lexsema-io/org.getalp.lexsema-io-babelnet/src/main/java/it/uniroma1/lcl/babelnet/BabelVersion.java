package it.uniroma1.lcl.babelnet;

/**
 * 
 * Version of BabelNet
 * 
 * @author navigli
 *
 */
public enum BabelVersion
{
	UNKNOWN("unknown"),
	PRE_2_0("< 2.0"),
	V2_0("2.0"),
	V2_0_1("2.0.1"),
	V2_5("2.5"),
	V2_5_1("2.5.1");

	private String ver;
	public String toString() { return ver; }
	
	private BabelVersion(String ver) { this.ver = ver; }
	static public BabelVersion getLatestVersion() { return BabelVersion.values()[BabelVersion.values().length-1]; }
}
