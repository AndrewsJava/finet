package harlequinmettle.financialsnet;

import java.io.Serializable;
import java.util.ArrayList;
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
	public TreeMap<String, Integer> tickersPerFileInDatabase = new TreeMap<String, Integer>();
	public TreeMap<String, ArrayList<String>> tickersActual = new TreeMap<String, ArrayList<String>>();
	public int maxSize = 0;
	public TreeMap<Long,String> myHistory = new TreeMap<Long,String>();
	public TreeMap<Long,Trade> myTrades = new TreeMap<Long,Trade>();
	public TreeMap<String,Long> myPortfolio = new TreeMap<String,Long>();
	 
	public ProgramSettings() { }

}
