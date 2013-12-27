package harlequinmettle.financialsnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class DataUtil {
	public static boolean debug = false;
	public static TreeSet<Integer> sizes = new TreeSet<Integer>();
	public static TreeSet<String> outline = new TreeSet<String>();
	public static TreeMap<Integer, FrequencyMap> freqs = new TreeMap<Integer, FrequencyMap>();

	// public FrequencyMap fmS = new FrequencyMap(FrequencyMap.INTEGER);
	static {
		for (int i = 0; i < 16; i++) {
			freqs.put(i, new FrequencyMap(FrequencyMap.INTEGER));
		}
	}

	// ////////////////////////////////////////////////////
	// seems to work fine; objects stored to file and using unique retrieval
	// methods objects are restored
	public static void memorizeObject(Object ob, String obFileName) {
		System.out.println("memorizing object ... ");
		File nextFile = new File(obFileName);
		try {
			nextFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FileOutputStream fileout = new FileOutputStream(obFileName);

			ObjectOutputStream objout = new ObjectOutputStream(fileout);
			objout.writeObject(ob);
			objout.flush();
			objout.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("UNABLE TO SAVE OBJECT TO: " + obFileName);
		}
		System.out.println("done memorizing object to: " + obFileName);
	}

	// /////////// ////////////////////////////
	@SuppressWarnings("unchecked")
	public static   float[][] restoreData(String findFromFile) {
		 float[][] dataSet = null;
		try {
			FileInputStream filein = new FileInputStream(findFromFile);
			ObjectInputStream objin = new ObjectInputStream(filein);
			try {
				dataSet = ( float[][]) objin.readObject();
			} catch (ClassCastException cce) {
				cce.printStackTrace();
			}
			objin.close();
		} catch (Exception ioe) {
			ioe.printStackTrace();
			System.out.println("NO resume: saver");
		}
		return dataSet;
	} // /////////// ////////////////////////////
	// /////////// ////////////////////////////
	@SuppressWarnings("unchecked")
	public static TreeMap<String, float[]> restoreTM1D(String findFromFile) {
		TreeMap<String, float[]> dataSet = null;
		try {
			FileInputStream filein = new FileInputStream(findFromFile);
			ObjectInputStream objin = new ObjectInputStream(filein);
			try {
				dataSet = (TreeMap<String, float[]>) objin.readObject();
			} catch (ClassCastException cce) {
				cce.printStackTrace();
			}
			objin.close();
		} catch (Exception ioe) {
			ioe.printStackTrace();
			System.out.println("NO resume: saver");
		}
		return dataSet;
	} // /////////// ////////////////////////////

	// /////////// ////////////////////////////
	@SuppressWarnings("unchecked")
	public static TreeMap<String, float[][]> restoreTM2D(String findFromFile) {
		TreeMap<String, float[][]> dataSet = null;
		try {
			FileInputStream filein = new FileInputStream(findFromFile);
			ObjectInputStream objin = new ObjectInputStream(filein);
			try {
				dataSet = (TreeMap<String, float[][]>) objin.readObject();
			} catch (ClassCastException cce) {
				cce.printStackTrace();
			}
			objin.close();
		} catch (Exception ioe) {
			ioe.printStackTrace();
			System.out.println("NO resume: saver");
		}
		return dataSet;
	} // /////////// ////////////////////////////
	// /////////// ////////////////////////////
	@SuppressWarnings("unchecked")
	public static   float[][][] restore3D(String findFromFile) {
		float[][][] dataSet = null;
		try {
			FileInputStream filein = new FileInputStream(findFromFile);
			ObjectInputStream objin = new ObjectInputStream(filein);
			try {
				dataSet = (float[][][]) objin.readObject();
			} catch (ClassCastException cce) {
				cce.printStackTrace();
				return null;
			}
			objin.close();
		} catch (Exception ioe) {
		//	ioe.printStackTrace();
			System.out.println("NO resume: saver");
		return null;
		}
		return dataSet;
	} // /////////// ////////////////////////////
	// /////////// ////////////////////////////
	@SuppressWarnings("unchecked")
	public static   float[][]  restore2D(String findFromFile) {
		  float[][] dataSet = null;
		try {
			FileInputStream filein = new FileInputStream(findFromFile);
			ObjectInputStream objin = new ObjectInputStream(filein);
			try {
				dataSet = (  float[][]) objin.readObject();
			} catch (ClassCastException cce) {
				cce.printStackTrace();
				return null;
			}
			objin.close();
		} catch (Exception ioe) {
			//ioe.printStackTrace();
			System.out.println("NO resume: saver");
			return null;
		}
		return dataSet;
	} // /////////// ////////////////////////////
	// /////////// ////////////////////////////
	@SuppressWarnings("unchecked")
	public static float[] restore1D(String findFromFile) {
		float[] dataSet = null;
		try {
			FileInputStream filein = new FileInputStream(findFromFile);
			ObjectInputStream objin = new ObjectInputStream(filein);
			try {
				dataSet = (float[]) objin.readObject();
			} catch (ClassCastException cce) {
				cce.printStackTrace();
			return null;
			}
			objin.close();
		} catch (Exception ioe) {
			//ioe.printStackTrace();
			System.out.println("NO resume: saver");
			return null;
		}
		return dataSet;
	} // /////////// ////////////////////////////
	// /////////// ////////////////////////////
	@SuppressWarnings("unchecked")
	public static Float restoreFloat(String findFromFile) {
		Float dataSet = null;
		try {
			FileInputStream filein = new FileInputStream(findFromFile);
			ObjectInputStream objin = new ObjectInputStream(filein);
			try {
				dataSet = (Float) objin.readObject();
			} catch (ClassCastException cce) {
				cce.printStackTrace();
				return null;
			}
			objin.close();
		} catch (Exception ioe) {
			//ioe.printStackTrace();
			System.out.println("NO resume: saver");
			return null;
		}
		return dataSet;
	} // /////////// ////////////////////////////
		// /////////// ////////////////////////////

	public static TreeMap<String, float[]> loadTM1D(String findFromFile) {
		TreeMap<String, float[]> dataSet = null;

		return dataSet;
	} // /////////// ////////////////////////////

	// /////////// ////////////////////////////
	public static TreeMap<String, float[][]> loadTM2D(String findFromFile) {
		TreeMap<String, float[][]> dataSet = null;

		return dataSet;
	} // /////////// ////////////////////////////

	private static TreeMap<String, float[]> loadData1D(String location) {
		TreeMap<String, float[]> map = new TreeMap<String, float[]>();
		BufferedReader br = null;
		InputStream is;
		try {
			is = new FileInputStream(location);
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int counter = 0;
			String a;
			while ((a = br.readLine()) != null) {
				String[] enter = a.split("\\^");
				map.put(enter[0].trim(),
						convertToFloatArray1D(enter[1], 1000000));
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;

	}

	static TreeMap<String, float[][]> loadData(String location) {
		TreeMap<String, float[][]> map = new TreeMap<String, float[][]>();
		BufferedReader br = null;
		InputStream is;
		try {
			is = new FileInputStream(location);
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int counter = 0;
			String a;
			while ((a = br.readLine()) != null) {
				String[] enter = a.split("\\^");
				map.put(enter[0].trim(), convertToFloatArray2D(enter[1]));
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	static TreeMap<String, String> loadStringData(String location) {
		TreeMap<String, String> map = new TreeMap<String, String>();
		BufferedReader br = null;
		InputStream is;
		try {
			is = new FileInputStream(location);
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int counter = 0;
			String a;
			while ((a = br.readLine()) != null) {
				String[] enter = a.split("\\^");
				map.put(enter[0].trim(), enter[1].trim());
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	static void loadStringData(String location, TreeMap<String, String> map) {

		BufferedReader br = null;
		InputStream is;
		try {
			is = new FileInputStream(location);
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int counter = 0;
			String a;
			while ((a = br.readLine()) != null) {
				String[] enter = a.split("\\^");
				map.put(enter[0].trim(), enter[1].trim());
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static void loadData1D(File f, TreeMap<String, float[]> map) {

		BufferedReader br = null;
		InputStream is;
		try {
			is = new FileInputStream(f);
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int counter = 0;
			String a;
			while ((a = br.readLine()) != null) {
				String[] enter = a.split("\\^");
				map.put(enter[0].trim(),
						convertToFloatArray1D(enter[1], 1000000));
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void loadData(File f, TreeMap<String, float[][]> map) {

		BufferedReader br = null;
		InputStream is;
		try {
			is = new FileInputStream(f);
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int counter = 0;
			String a;
			while ((a = br.readLine()) != null) {
				String[] enter = a.split("\\^");
				map.put(enter[0].trim(), convertToFloatArray2D(enter[1]));
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static float[][] convertToFloatArray2D(String string) {

		float[][] theData = null;

		String[] segments = string.split(" ");
		theData = new float[segments.length][];
		for (int i = 0; i < theData.length; i++) {
			theData[i] = convertToFloatArray1D(segments[i], i);
		}

		return theData;

	}

	static float[] convertToFloatArray1D(String string, int id) {
		// possibly add # replacement value
		// big data is pre" "splitting arrays - overall length should always be
		// one
		if (debug) {
			String reform = string.replaceAll("@", "_");
			String[] overall = reform.split(" ");
			String generally = "";
			for (int i = 0; i < overall.length && i < 16; i++) {
				if (id > 1000) {
					freqs.get(i).add(overall[i].split("_").length);
				} else {
					try {
						if (id < 16)
							freqs.get(id).add(overall[i].split("_").length);
					} catch (NullPointerException npe) {
						System.out.println(freqs.size() + "   " + id + "    "
								+ i);
					}
				}
			}
			outline.add("" + overall.length + ": " + generally);
		}
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
				"Aug", "Sep", "Oct", "Nov", "Dec" };
		String[] datain = string.replaceAll("@", " ").replaceAll("_", " ")
				.split(" ");

		float dub = 0;
		float factor = 1;
		float[] processed = new float[datain.length];
		for (int i = 0; i < datain.length; i++) {
			factor = 1;
			String data = datain[i].replace("$", "");// remove unrecognized
														// symbols
			data = data.replace("%", "");
			data = data.replaceAll("--", "");
			data = data.replaceAll("\\(", "-");// NEG IN ACCOUNTING IN
												// PARENTHESIS
			data = data.replaceAll("\\)", "");
			data = data.replaceAll("NM", "");
			data = data.replaceAll("Dividend", "");

			// replaced individual if stmts with for loop HOPE STILL TO WORK
			// convert month text into number
			for (int j = 0; j < months.length; j++) {
				if (data.equals(months[j])) {
					data = "" + j;
				}
			}
			// would like to use industry rank info better 12|50?
			// possibly convert directly to ratio
			if (data.contains("|"))
				data = data.substring(0, data.indexOf("|"));
			// cnn recommendations converted to numbers
			if (data.equals("Sell"))
				data = "-100";
			if (data.equals("Underperform"))
				data = "-10";
			if (data.equals("Hold"))
				data = "1";
			if (data.equals("Outperform"))
				data = "10";
			if (data.equals("Buy"))
				data = "100";// note the B would cause billions
			if (data.equals("#"))
				processed[i] = Float.NaN;// number to replace blanks #

			if (data.contains("T")) {// for when billions are abreviated B
				factor = 1000000000000f;
				data = data.replaceAll("T", "");
			}
			if (data.contains("B")) {// for when billions are abreviated B
				factor = 1000000000;
				data = data.replaceAll("B", "");
			}
			if (data.contains("M")) {
				factor = 1000000;
				data = data.replaceAll("M", "");
			}
			if (data.contains("K")) {
				factor = 1000;
				data = data.replaceAll("K", "");
			}
			if (data != null) {
				String dat = data.replaceAll(",", "");// remove commas from
														// zeros1,000
				dub = doFloat(dat);
				processed[i] = dub * factor;
			} else {
				// System.out.println("** unknown string default -> -1e-7 "+
				// data);
				processed[i] = Float.NaN;
			}
		}
		if (debug) {
			String reform = string.replaceAll("@", "_");
			String[] overall = reform.split(" ");
			String generally = "";
			ArrayList<Float> d = new ArrayList<Float>();
			for (float f : processed) {
				d.add(f);
			}
			for (int i = 0; i < overall.length && i < 16; i++) {
				if (id > 1000) {

					freqs.get(i).addToData(d, overall[i].split("_").length);
				} else {
					try {
						if (id < 16)
							freqs.get(id).addToData(d,
									overall[i].split("_").length);
					} catch (NullPointerException npe) {
						System.out.println("ERROR  " + freqs.size() + "   "
								+ id + "    " + i);
					}
				}
			}
			outline.add("" + overall.length + ": " + generally);
		}
		return processed;
	}

	static float[] convertToFloatArray1D(String string) {
		// possibly add # replacement value
		// big data is pre" "splitting arrays - overall length should always be
		// one

		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
				"Aug", "Sep", "Oct", "Nov", "Dec" };
		String[] datain = string.replaceAll("@", " ").replaceAll("_", " ")
				.split(" ");

		float dub = 0;
		float factor = 1;
		float[] processed = new float[datain.length];
		for (int i = 0; i < datain.length; i++) {
			factor = 1;
			String data = datain[i].replace("$", "");// remove unrecognized
														// symbols
			data = data.replace("%", "");
			data = data.replaceAll("--", "");
			data = data.replaceAll("\\(", "-");// NEG IN ACCOUNTING IN
												// PARENTHESIS
			data = data.replaceAll("\\)", "");
			data = data.replaceAll("NM", "");
			data = data.replaceAll("Dividend", "");

			// replaced individual if stmts with for loop HOPE STILL TO WORK
			// convert month text into number
			for (int j = 0; j < months.length; j++) {
				if (data.equals(months[j])) {
					data = "" + j;
				}
			}
			// would like to use industry rank info better 12|50?
			// possibly convert directly to ratio
			if (data.contains("|"))
				data = data.substring(0, data.indexOf("|"));
			// cnn recommendations converted to numbers
			if (data.equals("Sell"))
				data = "-100";
			if (data.equals("Underperform"))
				data = "-10";
			if (data.equals("Hold"))
				data = "1";
			if (data.equals("Outperform"))
				data = "10";
			if (data.equals("Buy"))
				data = "100";// note the B would cause billions
			if (data.equals("#"))
				processed[i] = Float.NaN;// number to replace blanks #

			if (data.contains("T")) {// for when billions are abreviated B
				factor = 1000000000000f;
				data = data.replaceAll("T", "");
			}
			if (data.contains("B")) {// for when billions are abreviated B
				factor = 1000000000;
				data = data.replaceAll("B", "");
			}
			if (data.contains("M")) {
				factor = 1000000;
				data = data.replaceAll("M", "");
			}
			if (data.contains("K")) {
				factor = 1000;
				data = data.replaceAll("K", "");
			}
			if (data != null) {
				String dat = data.replaceAll(",", "");// remove commas from
														// zeros1,000
				dub = doFloat(dat);
				processed[i] = dub * factor;
			} else {
				// System.out.println("** unknown string default -> -1e-7 "+
				// data);
				processed[i] = Float.NaN;
			}
		}

		return processed;
	}

	static float[] validSmallDataSet(String string, int[] pattern) {
		// possibly add # replacement value
		// big data is pre" "splitting arrays - overall length should always be
		// one
		String[] validateBySize = string.split(" ");
		for (int i = 0; i < validateBySize.length; i++) {
			int size = validateBySize[i].replaceAll("@", " ")
					.replaceAll("_", " ").split(" ").length;
			// /COMPARE SIZES TO PATTERN i - IF SIZE IS WRONG REPLACE WITH NAN
		}
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
				"Aug", "Sep", "Oct", "Nov", "Dec" };
		 
		String[] datain = string.replaceAll("@", " ").replaceAll("_", " ")
				.split(" ");
		int len = datain.length;
		if (len != 175)
			Database.invalid++;
		else
			Database.valid++;

		float dub = 0;
		float factor = 1;
		float[] processed = new float[datain.length];
		for (int i = 0; i < datain.length; i++) {
			factor = 1;
			String data = datain[i].replace("$", "");// remove unrecognized
														// symbols
			data = data.replace("%", "");
			data = data.replaceAll("--", "");
			data = data.replaceAll("\\(", "-");// NEG IN ACCOUNTING IN
												// PARENTHESIS
			data = data.replaceAll("\\)", "");
			data = data.replaceAll("NM", "");
			data = data.replaceAll("Dividend", "");

			// replaced individual if stmts with for loop HOPE STILL TO WORK
			// convert month text into number
			for (int j = 0; j < months.length; j++) {
				if (data.equals(months[j])) {
					data = "" + j;
				}
			}
			// would like to use industry rank info better 12|50?
			// possibly convert directly to ratio
			if (data.contains("|"))
				data = data.substring(0, data.indexOf("|"));
			// cnn recommendations converted to numbers
			if (data.equals("Sell"))
				data = "-100";
			if (data.equals("Underperform"))
				data = "-10";
			if (data.equals("Hold"))
				data = "1";
			if (data.equals("Outperform"))
				data = "10";
			if (data.equals("Buy"))
				data = "100";// note the B would cause billions
			if (data.equals("#"))
				processed[i] = Float.NaN;// number to replace blanks #

			if (data.contains("T")) {// for when billions are abreviated B
				factor = 1000000000000f;
				data = data.replaceAll("T", "");
			}
			if (data.contains("B")) {// for when billions are abreviated B
				factor = 1000000000;
				data = data.replaceAll("B", "");
			}
			if (data.contains("M")) {
				factor = 1000000;
				data = data.replaceAll("M", "");
			}
			if (data.contains("K")) {
				factor = 1000;
				data = data.replaceAll("K", "");
			}
			if (data != null) {
				String dat = data.replaceAll(",", "");// remove commas from
														// zeros1,000
				dub = doFloat(dat);
				processed[i] = dub * factor;
			} else {
				// System.out.println("** unknown string default -> -1e-7 "+
				// data);
				processed[i] = Float.NaN;
			}
		}

		return processed;
	}

	private static float doFloat(String value) {
		try {
			return Float.parseFloat(value);

		} catch (Exception e) {
			return Float.NaN;
		}
	}

	// ////////////////////////////////
	/*
	 * calulates percent change from one double value to another
	 * 
	 * @param was former value
	 * 
	 * @param is later value
	 */
	static double changes(double was, double is) {
		if (was == 0)
			return 1;
		if (is == 0)
			return -1;
		if (was == -1e-7 || is == -1e-7
				|| (int) (1e9 * ((is - was) / was)) == -9999 || was < 0
				|| is < 0) {
			// System.out.println("PERCENTAGE CHANGE ERROR VALUE: -1E-7");
			return -1e-7;
		}

		return (is - was) / was;
	}

}
