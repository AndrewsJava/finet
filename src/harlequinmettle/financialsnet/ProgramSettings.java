package harlequinmettle.financialsnet;

import java.io.Serializable;
import java.util.TreeMap;

public class ProgramSettings implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3516273172413265509L;
	public String rootPathToTextDatabase = "";
	public String rootPathToDownloads = "";
	public int daysOfReportsToDownload = 20;// includes weekends/holidays
	public boolean autoLoadDatabase = false; 
	
	public TreeMap<String, Integer> tickersPerFile = new TreeMap<String, Integer>();
	 
	public ProgramSettings() { }

}
