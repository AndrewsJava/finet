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

	// public TreeMap<Double,Double> dateNumbers  = new TreeMap<Double,Double>();
	 public TreeMap<Double,Double> dateNumbers_s_y = new TreeMap<Double,Double>();
	 public TreeMap<Double,Double> dateNumbers_s_q = new TreeMap<Double,Double>();
	 public TreeMap<Double,Double> dateNumbers_l_y = new TreeMap<Double,Double>();
	 public TreeMap<Double,Double> dateNumbers_l_q = new TreeMap<Double,Double>();
	 
	public ProgramSettings() {
		dateNumbers_s_y.put(0.0, 0.0);
		dateNumbers_s_q.put(0.0, 0.0);
		dateNumbers_l_y.put(0.0, 0.0);
		dateNumbers_l_q.put(0.0, 0.0);
	}

}
