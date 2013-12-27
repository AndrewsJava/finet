package harlequinmettle.financialsnet;

import harlequinmettle.financialsnet.interfaces.Qi;
import harlequinmettle.financialsnet.interfaces.Yi;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
 

public class ShortDataCollectorUtil   implements Yi, Qi { 
	public static final String SPLITTER = "^";  
	private static double split = 0;
	private static double dividends = 0;
	public static final String yahoobase = "http://finance.yahoo.com/q";
	public static final String fcstbase = "http://money.cnn.com/quote/forecast/forecast.html?symb=";
	private static TreeMap<String, double[]> lightMap = new TreeMap<String, double[]>();
	 
	private static TreeMap<String, String> rawMap = new TreeMap<String, String>();
 
 
	  

	public static String fileTitle(String nasOrNy) {
		double time = System.currentTimeMillis();
		// convert to seconds
		time /= 1000;
		// convert to hours
		time /= 3600;
		// to days
		time /= 24;

		time = (double) ((int) (time * 10) / 10.0);

		return nasOrNy + "_" + time + ".txt";
		//
	}

	  
	// /////////////////////////////////////////////////////////////
	static void loadTickerSet(String fileName,ArrayList<String> tickers) {
		Scanner inFile = null;
		try {
			inFile = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (inFile == null) {
			System.out.println("invalid file no symbols loaded");
			return;
		}
		boolean firstLine = true;
		while (inFile.hasNextLine()) {
			String ticker = inFile.nextLine();
			if (firstLine) {
				firstLine = false;
				continue;
			}
			String[] getTicker = ticker.split(",");
			tickers.add(getTicker[0].replaceAll("\"", ""));
		}
	}

	// ///////////////////////////////////////////////////////////
	private static void makeSureFilesExist(String makeFileWithName) {
		File newFile = new File(makeFileWithName);
		try {
			newFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ////////////////////////////////////////////////////////////
	private void mapRawData(String[] tickers) {
		System.out.println("total number of tickers: " + tickers.length);
		int counter = 0;

		// (" 2 36 44 9 9 9 9 9 9 9 9 9 9 1 1 1");
		for (String tk : tickers) {
			StringBuilder saveAsText = new StringBuilder();
			saveAsText.append(cnnForecast(tk).trim());
			saveAsText.append(" ");
			saveAsText.append(analystEstimates(tk));
			saveAsText.append(" ");
			saveAsText.append(keyBasedData(yahoobase + "/ks?s=" + tk, keykeys));
			saveAsText.append(" ");
			saveAsText.append(limitToTen(pastPrices(tk)));
			saveAsText.append(" ");
			saveAsText.append(optionsCount(tk));
			// System.out.println(tk);
			if (!printQuickCount(saveAsText))
				System.out.println("\n--> " + saveAsText);
			rawMap.put(tk, saveAsText.toString());
	 
		}
	}

	// ///////////////////////////////////////////////
	public static void loadDataFromFile(String fileName) {

		Scanner previoustickers = null;
		try {
			previoustickers = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String key = "";
		String value = "";

		while (previoustickers.hasNextLine()) {
			String filedata = previoustickers.nextLine();
			if (filedata.equals(""))
				continue;
			String[] linedata = filedata.split("\\^");
			if (linedata.length > 1) {
				key = linedata[0];
				value = linedata[1];
				lightMap.put(key, dataLightSubprocessor(value));
			}
		}

	}

	private static void saveMapToTextFile(String fileToSaveTo) {
		FileWriter writeto = null;
		try {
			writeto = new FileWriter(fileToSaveTo, true);// true append
															// text
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (writeto == null) {
			System.out.println("file not initialized");
			return;
		}
		for (Map.Entry<String, String> entry : rawMap.entrySet()) {
			String textadd = entry.getKey() + SPLITTER + entry.getValue()
					+ "\n";
			char[] buffer = new char[textadd.length()];
			textadd.getChars(0, textadd.length(), buffer, 0);
			try {
				writeto.write(buffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			writeto.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// #################################
	// ////////////////////past and current estimates on earnings revenue growth
	public static String analystEstimates(String stock) {
		int counting = 0;
		// Year Ago EPS //after this key substring to Revenue Est
		String httpdata = getHtml(yahoobase + "/ae?s=" + stock);
		String chop = "";
		if (httpdata.contains("Earnings Est")
				&& httpdata.contains("Currency in USD")) {
			chop = httpdata
					.substring(httpdata.indexOf("Earnings Est"),
							httpdata.indexOf("Currency in USD"))
					.replaceAll("d><t", "d> <t").replaceAll("d></t", "d>@</t");
		}
		String rval = "";
		chop = removeHtml((chop), true).replaceAll("_", " ");
		for (String k : estimatekeys) {
			counting++;
			if (chop.contains(k)) {
				if (counting == 6 && chop.contains("Earnings Hist"))
					chop = chop.substring(chop.indexOf("Earnings Hist"));
				String datapart = chop.substring(chop.indexOf(k) + k.length());
				if (datapart.contains("@"))
					datapart = datapart.substring(0, datapart.indexOf("@"))
							.trim();
				for (int i = 0; i < 4 - datapart.split(" ").length; i++)
					datapart += "_#";
				rval += datapart + " ";
			} else
				rval += "# # # # ";
		}
		String backAt = (rval.replaceAll("_", " ").trim().replaceAll(" ", "_"));
		// System.out.println(backAt);

		return backAt;
	}

	// #################################
	// /////////////forecast data from cnn price analysts status
	public static String cnnForecast(String stock) {
		for (int i = 0; i < 4; i++) {
			String httpdata = getHtml(fcstbase + stock);
			if (httpdata == null)
				System.out.println("null forecast");
			if (httpdata.contains(">There is no")
					|| httpdata.contains("was not found")) {
				// System.out.println("NO CNN DATA FOR: " + stock);
				return "#_#";
			}
			String chop = "#_#";

			if (httpdata.contains(">Stock Price Forecast")) {
				if (i > 0)
					System.out.print("\n*" + i + "*");
				chop = httpdata.substring(httpdata
						.indexOf(">Stock Price Forecast"));
				if (chop.contains("Earnings and Sales Forecasts")
						&& chop.contains("The"))

					chop = chop.substring(chop.indexOf("The") + 3);
				String analysts = "#";
				String forecast = "#";
				try {
					if (chop.contains("analyst"))
						analysts = chop.substring(0, chop.indexOf("analyst"))
								.trim();

					if (chop.contains("represents a"))
						forecast = chop.substring(
								chop.indexOf("represents a") + 12,
								chop.indexOf("%")).replaceAll("_", "");
					if (forecast.contains(">"))
						forecast = forecast
								.substring(forecast.indexOf(">") + 1).trim();
				} catch (Exception e) {
				}
				chop = analysts + "_" + forecast;
			}
			return (chop);
		}
		return "#_#_#";
	}

	// //////////////////////////////////////
	public static boolean printQuickCount(StringBuilder saveAsText) {
		String[] sections = saveAsText.toString().replaceAll("@", " ")
				.split(" ");
		StringBuilder build = new StringBuilder();
		for (String s : sections) {
			build.append(" " + s.split("_").length);
		}
		// System.out.println(build);
		return build.toString().equals(" 2 36 44 9 9 9 9 9 9 9 9 9 9 1 1 1");
	}

	// ///////////////////////////////////////////////////////////////////
	public static String limitToTen(String pastPrices) {
		// TODO Auto-generated method stub
		if (pastPrices.contains("Split"))
			System.out.println("**S-->" + pastPrices);
		if (pastPrices.equals("HISTORIC"))
			return "#_#_#_#_#_#_#_#_#@#_#_#_#_#_#_#_#_#@#_#_#_#_#_#_#_#_#@#_#_#_#_#_#_#_#_#@#_#_#_#_#_#_#_#_#@#_#_#_#_#_#_#_#_#@#_#_#_#_#_#_#_#_#@#_#_#_#_#_#_#_#_#@#_#_#_#_#_#_#_#_#@#_#_#_#_#_#_#_#_#"
					+ " #" + " #";
		split = 1;
		dividends = 0;
		String[] days = pastPrices.split("@");
		StringBuilder reconstruct = new StringBuilder();
		int counter = 0;
		for (int i = 0; i < days.length; i++) {
			String[] data = days[i].split("_");
			if (data.length < 9) {
				System.out.println(pastPrices);
				String[] determine = days[i].split(":");
				try {
					if (determine.length < 2)
						dividends += doDouble(days[i].split("_")[3]);
					else if (counter < 5) {
						split = doDouble(determine[0].split("_")[3])
								/ doDouble(determine[1].toLowerCase()
										.replace("stock_split", "")
										.replaceAll("_", ""));
						System.out.println("SPLIT: " + split + "\nFrom: "
								+ pastPrices);
					}
				} catch (Exception e) {
					System.out.println(days[i]);
					e.printStackTrace();
				}
			} else {
				reconstruct.append(days[i] + "@");
				counter++;
				if (counter == 10)
					break;
			}
		}
		for (int j = counter - 1; j < 9; j++)
			reconstruct.append("#_#_#_#_#_#_#_#_#@");
		return reconstruct.append(" " + dividends + " " + split)
				.deleteCharAt(reconstruct.lastIndexOf("@")).toString();
	}

	// ///////////////////////////////////////////////////////////////////
	public static String optionsCount(String stock) {
		String httpdata = getHtml(yahoobase + "/op?s=" + stock);
		if (httpdata.contains(">There is no")
				|| httpdata.contains("Check your spelling")
				|| httpdata.indexOf("View By Expiration") < 0)
			return "0";
		try {
			httpdata = httpdata.substring(httpdata
					.indexOf("View By Expiration"));
			httpdata = httpdata.substring(0, httpdata.indexOf("table"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "" + (httpdata.toLowerCase().split("a href").length - 1);
	}

	/**
	 * Converts String data into double data removing commas, n/a's, and
	 * replacing M's, and B's with six and nine zeros respectively.
	 * 
	 * @param datain
	 *            raw data from stock data web site.
	 */
	static double[] dataLightSubprocessor(String datain) {
		// possibly add # replacement value
		String[] dataInSplit = datain.replaceAll("@", " ").replaceAll("_", " ")
				.split(" ");
		//
		if (dataInSplit.length != 175)
			System.out.println(dataInSplit.length);
		double[] processed = new double[dataInSplit.length];
		for (int i = 0; i < dataInSplit.length; i++) {
			double factor = 1;
			String data = dataInSplit[i].replace("$", "");// remove unrecognized
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
			if (data.equals("#"))
				processed[i] = -0.0000001;// number to replace blanks #

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
				double dub = doDouble(dat);
				processed[i] = dub * factor;
			} else {
				// System.out.println("** unknown string default -> -1e-7 "+
				// data);
				processed[i] = -0.0000001;
			}
		}
		return processed;
	}

	/*
	 * returns the double value of a string and returns -1E-7 if the string
	 * could not be parsed as a double.
	 * 
	 * @param value the string that gets converted into a double.
	 */
	private static double doDouble(String value) {
		try {
			double val = Double.parseDouble(value);
			if (val == val)// only return value if its not NaN , NaN==NaN is
							// false
				return val;
			else
				return -0.0000001;
		} catch (Exception e) {
			// System.out.println(" TEXT TO NUMBER ERR "+ value +" to -1e-7 ");
			return -0.0000001;
		}
	}

	// ///////////replace @,^,shorten to end of table,
	public static String reformat(String input) {
		String output = input.replaceAll("@", "_").replaceAll("^", "_")
				.replaceAll("\\*", "_");
		if (input.contains("</table>"))
			output = output.substring(0, output.indexOf("</table>"));
		output = output.replaceAll("d><t", "d> <t").replaceAll("h><t", "h> <t")
				.replaceAll("d></t", "d>@</t").replaceAll("&nbsp;", "-")
				.replaceAll("--", "");
		return output;
	}

	// /////////////////#######################################
	public static String pastPrices(String stock) {

		String httpdata = getHtml(yahoobase + "/hp?s=" + stock);
		if (!httpdata.contains("Adj Close"))
			return "HISTORIC";
		httpdata = removeHtml(
				reformat(httpdata.substring(httpdata.indexOf("Adj Close"))),
				false);
		httpdata = httpdata.substring(0, httpdata.lastIndexOf("@"));
		if (httpdata.indexOf("@") < 2)
			httpdata = httpdata.substring(httpdata.indexOf("@") + 1);
		if (httpdata.lastIndexOf("@") > 1)
			httpdata = (httpdata.substring(0, httpdata.lastIndexOf("@"))
					.replaceAll(",", ""));
		return httpdata;
	}

	/**
	 * This method returns a String of http data f
	 * 
	 * @param suf
	 *            The http address to get string from.
	 */
	static protected String getHtml(String suf) {

		URL url;
		InputStream is;
		InputStreamReader isr;
		BufferedReader r;
		String str = "";
		String nl = "";

		try {
			url = new URL(suf);
			is = url.openStream();
			isr = new InputStreamReader(is);
			r = new BufferedReader(isr);
			do {
				nl = r.readLine();
				if (nl != null) {
					nl = nl.trim() + " ";
				}
				str += nl;
			} while (nl != null);
		} catch (MalformedURLException e) {
			System.out.println("Must enter a valid URL");
		} catch (IOException e) {
			// System.out.println( "IO error getting  html data for site :  " +
			// suf);
		}
		return str;
	}

	/**
	 * This method colects into a string data from tables about stock in the
	 * form nnn_mmm_nnn_mmm@nnn_mmm_nnn_mmm@etc
	 * 
	 * @param addy
	 *            The internet address to get html from.
	 * @param keys
	 *            The text keys expected to be found in the html data
	 */
	public static String keyBasedData(String addy, String[] keys) {
		for (int i = 0; i < 10; i++) {// try 10 times to get html or else return
			if (i > 0)
				System.out.println("\nConnection Failure. Trying again: " + i);
			String httpdata = getHtml(addy);
			String yhdata = "";
			String str = httpdata;
			if (str.contains("was not found"))
				return "#_#_#";
			if (str.contains("Recommendation Trends")) {
				str = (str.substring(str.indexOf("Recommendation Trends")));
				str = str.replaceAll("d><t", "d> <t");
			}
			for (String key : keys) {
				if (str.contains(">" + key)) {
					String strx = str.substring(str.indexOf(">" + key) + 1);
					if (!strx.contains("</tr>"))
						return "#_#_#";
					strx = strx.substring(0, strx.indexOf("</tr>"));
					if (key.equals("Sector"))
						strx = strx.replaceAll(" ", "|");
					strx = removeHtml(strx, true).replaceAll("@", " ");// just
																		// in
																		// case
					if (strx.length() == 0)
						strx = "#";// placeholder if data does not exist
					yhdata += strx + "_";
				} else {
					yhdata += "#_";

				}
			}
			// return spacify(yhdata.replaceAll("--", "#"));
			return (yhdata.replaceAll("--", "#").replaceAll("_", " ").trim()
					.replaceAll(" ", "_"));
		}
		return "#_#";
	}

	// ///////////////////////////////////////////////////////////

	/**
	 * This method removes html tags such as <a href = ...> and
	 * <table>
	 * 
	 * @param withhtml
	 *            The string text with tags included
	 */
	public static String removeHtml(String withhtml, boolean colon) {
		String stripped = withhtml;//
		String save = "";
		if (colon)
			if (stripped.indexOf(":") > 0) {// jump ahead just past the colons
				stripped = withhtml.substring(withhtml.indexOf(":") + 1);
			}
		// skip all sections of html code between the <htmlcode>
		while (stripped.indexOf("<") >= 0 && stripped.indexOf(">") > 0) {
			stripped = stripped.substring(stripped.indexOf(">") + 1);
			if (stripped.indexOf("<sup") < 2 && stripped.indexOf("<sup") > -1)
				stripped = stripped.substring(stripped.indexOf("</sup>") + 6);
			if (stripped.indexOf("<") > 0)// keep any text inbetween
											// <code>keeptext<code>
				save += stripped.substring(0, stripped.indexOf("<"));
		}
		// save = save.replaceAll("-","_");//VITAL TO PRESERVE NEGATIVES
		save = save.replaceAll(" ", "_");
		save = save.replaceAll("___", "_");
		save = save.replaceAll("__", "_");
		save = save.replaceAll("_-_", "_");
		return save;

	}

	public static String[] keykeys = { "Market Cap", // 0
			"Enterprise Value", //
			"Trailing P/E",// 2
			"Forward P/E",// 3
			"PEG Ratio", // 4
			"Price/Sales", //
			"Price/Book",//
			"Enterprise Value/Revenue",// 7
			"Enterprise Value/EBITDA ",//
			"Profit Margin",//
			"Operating Margin",//
			"Return on Assets", //
			"Return on Equity",//
			"Revenue", // 13
			"Revenue Per Share",//
			"Qtrly Revenue Growth",//
			"Gross Profit",// 16
			"EBITDA",//
			"Net Income Avl to Common", //
			"Diluted EPS",// 19
			"Qtrly Earnings Growth",//
			"Total Cash",//
			"Total Cash Per Share",// 22
			"Total Debt",//
			"Total Debt/Equity",//
			"Current Ratio", // 25
			"Book Value Per Share",//
			"Operating Cash Flow",// 27
			"Levered Free Cash Flow", //
			"Beta",//
			"52-Week Change",// 30
			"50-Day Moving Average",//
			"200-Day Moving Average",// 32
			"Avg Vol (3 month)",//
			"Avg Vol (10 day)",//
			"Shares Outstanding",// 35
			"Float",//
			"% Held by Insiders",//
			"% Held by Institutions",// 38
			"Shares Short (as of",//
			"Short Ratio (as of",// 40
			"Short % of Float (as of",//
			"Shares Short (prior month)",//
			"Payout Ratio" // 43
	};// 44 values

	public static String[] estimatekeys = {
		"Avg. Estimate",// these 5 use
			"No. of Analysts",//
			"Low Estimate",//
			"High Estimate", // /
			"Year Ago EPS", // after
							// this
							// key
							// substring
							// to
							// Revenue
							// Est

			"EPS Est",// these 4 use
			"EPS Actual", //
			"Difference", //
			"Surprise %", };//

	public static String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
			"Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
 
}
