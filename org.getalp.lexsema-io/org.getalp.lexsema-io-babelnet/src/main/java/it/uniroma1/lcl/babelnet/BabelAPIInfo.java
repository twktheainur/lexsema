package it.uniroma1.lcl.babelnet;

/**
 * Information about the BabelNet API
 * 
 * @author navigli
 */
public class BabelAPIInfo
{
	public final static String version = "2.5.1";
	public final static String authors = "Roberto Navigli, Simone Ponzetto and Daniele Vannella";
	public final static String contributors = "Francesco Cecconi";
	
	static public String getHeader() { return "BabelNet API v"+version+" written by "+authors+", with additional contributions by "+contributors; }
}
