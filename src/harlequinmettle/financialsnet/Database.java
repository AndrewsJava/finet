package harlequinmettle.financialsnet;

import harlequinmettle.financialsnet.interfaces.DBLabels;
import harlequinmettle.financialsnet.interfaces.Qi;
import harlequinmettle.financialsnet.interfaces.Yi;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JTextField;

public class Database implements Qi, Yi, DBLabels {
	// public static final String ROOT = "sm/q";
	// public static final String OBJ_ROOT = "sm/OBJECTS";
	public static final int FIELD_COUNT = DBLabels.labels.length; 
	public static final TreeMap<Float, float[][]> DB_ARRAY = new TreeMap<Float, float[][]>();
	public static final TreeMap<Float, float[][]> DB_SUP = new TreeMap<Float, float[][]>();

	public static final TreeMap<String, TreeMap<Float, float[]>> TECHNICAL_PRICE_DATA = new TreeMap<String, TreeMap<Float, float[]>>();
	public static final TreeMap<Float, float[]> SUM_MARKET_PRICE_DATA = new TreeMap<Float, float[]>();

	public static final TreeMap<Float, float[][][]> DB_PRICES = new TreeMap<Float, float[][][]>();

	public static final TreeMap<Float, Float> WEEKLY_MARKETCHANGE = new TreeMap<Float, Float>();
	public static final TreeMap<Float, Float> MARKET = new TreeMap<Float, Float>();

	public static final TreeMap<Float, float[]> PRICES = new TreeMap<Float, float[]>();
	public static final TreeMap<Float, float[]> UNFORESEEN = new TreeMap<Float, float[]>();
	// apply limits to data indicators to contsruct bundles - then calculate
	// value changes
	public static final TreeMap<Integer, Point2D.Float> LIMITS = new TreeMap<Integer, Point2D.Float>();
	public static final TreeMap<Float, Point> SET_CHANGES = new TreeMap<Float, Point>();
	public static final TreeMap<Float, ArrayList<Integer>> BUNDLES = new TreeMap<Float, ArrayList<Integer>>();
	public static final TreeMap<Float, Float> BUNDLES_CHANGES = new TreeMap<Float, Float>();
	public static final TreeMap<Float, Integer> BUNDLES_SIZES = new TreeMap<Float, Integer>();

	public static ArrayList<StatInfo> statistics = new ArrayList<StatInfo>();

	public static final TreeMap<Float, Integer> VALID_COUNT = new TreeMap<Float, Integer>();

	public static final int LOAD_NASDAQ = 1500000;
	public static final int LOAD_NYSE = 5555;
	public static final int LOAD_BOTH = 22022;
	// public static String[] dbSet;
	public static ArrayList<String> dbSet;
	public static final TreeMap<String, String> DESCRIPTIONS = new TreeMap<String, String>();
	public static final TreeMap<String, Integer> WORD_STATS = new TreeMap<String, Integer>();
	public static int valid = 0;
	public static int invalid = 0;
	int totalNull = 0;
	TreeSet<String> losses = new TreeSet<String>();
	String[] files_q;
	String[] files_y;
	long time;

	int dataRead = 0;
	int dataNotRead = 0;
	static StatInfo dividendStats;

	static boolean loaded = false;

	public static float overallMarketChange;
	public static final TreeMap<String, Float> INDIVIDUAL_OVERALL_CHANGES = new TreeMap<String, Float>();
	public static StatInfo changesStats;
//MAP TICKER TO A MAPING OF DAY TO DIVIDEND AMOUNT eg:  <ABC, <15000.0,0.25>>
	public static final TreeMap<String,  TreeMap<Float, Float>> DIVIDEND_HISTORY = new TreeMap<String,  TreeMap<Float, Float>>();
	// PARSEING:
	// LOADING:

	// OPTION TO LOAD NASDAQ AND NYSE - JOIN FILENAMES COMPARE DATES - SHOULD BE
	// ONE FOR ONE BUT LESS THAN 2

	// CHECK IN OBJECTS FOLDER LOAD FIRST - THEN CHECK FOR NEW TEXT FILES
	// CONVERT TO OBJECT (SAVE IN OBJ FOLDER) LOAD INTO DB
	public Database() {
		dbSet = new ArrayList<String>(Arrays.asList(concat(QQ, YY)));
		loadDatabaseWithData("");
		calculateChanges();
		computeSuplementalFactors();
		fillTechnicals();
		mapChangesAndTheirStats();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy");
		for (Entry<Float, Float> ent : WEEKLY_MARKETCHANGE.entrySet()) {
			String formated = sdf.format(new Date(
					(long) (ent.getKey() * 24 * 3600 * 1000)));
			System.out.println("week    : " + formated + "   --d> "
					+ ((int) (1000 * ent.getValue()) / 1000.0));
		}

		System.out.println("VALID DATA: " + valid);
		System.out.println("INVALID DT: " + invalid);
		System.out.println("total Null: " + totalNull);
		System.out.println("actual Nll: " + losses.size() + " -->" + losses);

		System.out.println("data was readable " + dataRead);
		System.out.println("data NOT readable " + dataNotRead);

		for (Entry<Float, Integer> ent : VALID_COUNT.entrySet()) {

			System.out.println("week    : " + ent.getKey()
					+ "   --valid price data-> " + ent.getValue());
		}

	}

	private void mapChangesAndTheirStats() {
		overallMarketChange = (int) (100 * ((Database.SUM_MARKET_PRICE_DATA
				.lastEntry().getValue()[6]) - Database.SUM_MARKET_PRICE_DATA
				.ceilingEntry(15800f).getValue()[6]) / (Database.SUM_MARKET_PRICE_DATA
				.ceilingEntry(15800f).getValue()[6]));

		for (Entry<String, TreeMap<Float, float[]>> ent : TECHNICAL_PRICE_DATA
				.entrySet()) {
			TreeMap<Float, float[]> technicals = ent.getValue();
			int individualOverallChange = (int) (100 * ((technicals.lastEntry()
					.getValue()[6] - technicals.firstEntry().getValue()[6]) / (technicals
					.firstEntry().getValue()[6])));
			INDIVIDUAL_OVERALL_CHANGES.put(ent.getKey(),
					(float) individualOverallChange);
		}
		changesStats = new StatInfo(new ArrayList<Float>(
				INDIVIDUAL_OVERALL_CHANGES.values()));
	}

	public static float calculatePercentChange(int tickerId, float start,
			float end) {
		return (int) (100 * ((Database.TECHNICAL_PRICE_DATA
				.get(dbSet.get(tickerId)).floorEntry(end).getValue()[6]) - Database.TECHNICAL_PRICE_DATA
				.get(dbSet.get(tickerId)).ceilingEntry(start).getValue()[6]) / (Database.TECHNICAL_PRICE_DATA
				.get(dbSet.get(tickerId)).ceilingEntry(start).getValue()[6]));
	}

	public static float calculateMarketChange(float start, float end) {
		int change = Integer.MAX_VALUE;
		int tries = 0;
		while (change > 10000 && tries++ < 4) {
			change = (int) (100 * ((Database.SUM_MARKET_PRICE_DATA.floorEntry(
					end).getValue()[6]) - Database.SUM_MARKET_PRICE_DATA
					.ceilingEntry(start).getValue()[6]) / (Database.SUM_MARKET_PRICE_DATA
					.ceilingEntry(start).getValue()[6]));
			start--;
			end++;
		}
		return change;
	}

	private void fillTechnicals() {
		int better = 0;
		int worse = 0;
		for (String s : dbSet)
			TECHNICAL_PRICE_DATA.put(s, new TreeMap<Float, float[]>());

		for (float[][][] tech : Database.DB_PRICES.values()) {
			for (int id = 0; id < tech.length; id++) {
				for (int i = 0; i < tech[id].length; i++) {
					if (isDataBetter(tech, id, i)) {
						TECHNICAL_PRICE_DATA.get(dbSet.get(id)).put(
								tech[id][i][0], tech[id][i]);
						better++;
					} else {
						worse++;
					}
				}

			}
		}
		Database.testNewCode();
		System.out.println("\nbetter: " + better);
		System.out.println("\nworse: " + worse);
	}

	static double[] spawnTimeSeriesForFFT(String ticker) {
		TreeMap<Float, float[]> allTickerHistoricData = TECHNICAL_PRICE_DATA
				.get(ticker);
		double[] priceData = new double[allTickerHistoricData.size()];
		int i = 0;
		for (float[] data : allTickerHistoricData.values()) {
			priceData[i++] = data[6];
		}
		return priceData;
	}

	// ///////////
	static double[] spawnAveragesArrayFromPricePair(String ticker,
			int neighborsToCount) {
		double[] priceData = spawnTimeSeriesForFFT(ticker);
		double[] avgPriceData = new double[priceData.length];

		for (int J = neighborsToCount; J < (priceData.length - neighborsToCount); J++) {
			float sum = 0;
			int n = 0;
			for (int L = J - neighborsToCount; L <= J + 1 + 2
					* neighborsToCount
					&& L < priceData.length; L++) {
				n++;
				sum += priceData[L];
			}
			float average = sum / n;
			avgPriceData[(J)] = average;
		}
		return avgPriceData;
	}

	private boolean isDataBetter(float[][][] tech, int id, int i) {

		float[] technicals = TECHNICAL_PRICE_DATA.get(dbSet.get(id)).get(
				tech[id][i][0]);
		if (technicals == null)
			return true;
		int ok_old = 0;
		for (float f : technicals)
			if (f == f)
				ok_old++;

		int ok_new = 0;
		for (float f : tech[id][i])
			if (f == f)
				ok_new++;

		return (ok_new > ok_old);

	}

	private static void doMarketSum() {
		ArrayList<String> validDataTickers = new ArrayList<String>();
		for (Entry<String, TreeMap<Float, float[]>> individual : Database.TECHNICAL_PRICE_DATA
				.entrySet()) {
			if (isAllDataValid(individual.getValue())) {
				validDataTickers.add(individual.getKey());
			}
		}
		System.out
				.println("----------=====:::::::::>                         ----->)    "
						+ validDataTickers.size());
		for (String s : validDataTickers) {

			addToMarketSum(Database.TECHNICAL_PRICE_DATA.get(s));
		}
	}

	private static boolean isAllDataValid(TreeMap<Float, float[]> value) {
		for (float[] f : value.values()) {
			for (float f1 : f) {
				if (f1 != f1) {
					return false;

				}
			}
		}
		return true;
	}

	private static void addToMarketSum(TreeMap<Float, float[]> individual) {
		for (Entry<Float, float[]> ent : individual.entrySet()) {

			float[] dayData = Database.SUM_MARKET_PRICE_DATA.get(ent.getKey());
			if (dayData == null) {
				Database.SUM_MARKET_PRICE_DATA.put(ent.getKey(),
						new float[ent.getValue().length]);
			}
			addIndividualDataToMarketSum(ent);
		}
	}

	private static void addIndividualDataToMarketSum(Entry<Float, float[]> ent) {
		float[] dayData = Database.SUM_MARKET_PRICE_DATA.get(ent.getKey());
		// System.out.println("A: "+Arrays.toString(dayData) );
		for (int i = 0; i < ent.getValue().length; i++) {
			if (ent.getValue()[i] == ent.getValue()[i])
				dayData[i] += ent.getValue()[i];
		}
		// System.out.println(
		// "B: "+Arrays.toString(Database.SUM_MARKET_PRICE_DATA.get(ent.getKey()))
		// );
	}

	public static void testNewCode() {
		long startTime = System.currentTimeMillis();

		System.out.println(Database.SUM_MARKET_PRICE_DATA.size());

		doMarketSum();
		System.out.println(Database.SUM_MARKET_PRICE_DATA.size());
		for (Entry<Float, float[]> allPts : Database.SUM_MARKET_PRICE_DATA
				.entrySet())
			System.out.println(allPts.getKey() + "  :=:  "
					+ Arrays.toString(allPts.getValue()) + "    ");
		System.out.println("\ntime: " + formatTime(startTime));
	}

	private static String formatTime(long startTime) {
		return (int) (System.currentTimeMillis() - startTime) / 1000
				+ " seconds ";
	}

	// //////////////
	public Database(String root) {
		SystemMemoryUsage smu = new SystemMemoryUsage();
		dbSet = new ArrayList<String>(Arrays.asList(concat(QQ, YY)));
		loadDatabaseWithData(root);
		calculateChanges();
		computeSuplementalFactors();
		fillTechnicals();
		mapChangesAndTheirStats();
		for (Entry<Float, Float> ent : WEEKLY_MARKETCHANGE.entrySet()) {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy");
			String formated = sdf.format(new Date(
					(long) (ent.getKey() * 24 * 3600 * 1000)));
			System.out.println("week    : " + formated + "   --d> "
					+ ((int) (1000 * ent.getValue()) / 1000.0));
		}
		loaded = true;
		System.out.println("VALID DATA: " + valid);
		System.out.println("INVALID DT: " + invalid);
		System.out.println("total Null: " + totalNull);
		System.out.println("actual Nll: " + losses.size() + " -->" + losses);

		System.out.println("data was readable " + dataRead);
		System.out.println("data NOT readable " + dataNotRead);

		for (Entry<Float, Integer> ent : VALID_COUNT.entrySet()) {

			System.out.println("week    : " + ent.getKey()
					+ "   --valid price data-> " + ent.getValue());
		}
		calculateStatistics();
	}

	private void calculateStatistics() {
		for (int i = 0; i < FIELD_COUNT; i++) {
			statistics.add(generateStatistics(i));
		}
		dividendStats = (doStatsOnDividendSum());
	}

	private StatInfo doStatsOnDividendSum() {
		TreeMap<String,Float> lastDividends = new TreeMap<String,Float>();
		for(String ticker: dbSet){
			lastDividends.put(ticker, 0f);
		}
		float[] dividendSums = new float[dbSet.size()];
		for (Entry<Float, float[][]> ent : Database.DB_ARRAY.entrySet()) {
			int i = 0;
			for (float[] d : ent.getValue()) {
				String ticker = dbSet.get(i);
				// 82 is dividends 
				// over reporting because collecting for 2 weeks
				float lastDiv =lastDividends.get(ticker); 
				if (  lastDiv !=  d[82]) { 
					DIVIDEND_HISTORY.get(ticker).put(ent.getKey(), d[82]);
					dividendSums[i] += d[82] ;
					}
				lastDividends.put(ticker, d[82]);
				i++;
			}
		}
		ArrayList<Float> dataArray = new ArrayList<Float>();
		for (float f : dividendSums) {
			dataArray.add(f);
		}
		for(int i = 0 ; i<dbSet.size(); i++){
			if(dividendSums[i]>0)
			System.out.println(dbSet.get(i)+"  dividends: "+dividendSums[i]);
		}
		return new StatInfo(dataArray, true);
	}

	public StatInfo generateStatistics(int id) {
		ArrayList<Float> stats = new ArrayList<Float>();
		for (Entry<Float, float[][]> ent : Database.DB_ARRAY.entrySet()) {
			float[][] dats = ent.getValue();
			for (float[] d : dats) {
				stats.add(d[id]);
			}
		}
		return new StatInfo(stats, id, StatInfo.DONT_SHOW);
	}

	public void computeSuplementalFactors() {
		for (Entry<Float, float[][]> week : DB_ARRAY.entrySet()) {
			float[][] weekNumber = week.getValue();
			float[][][] techs = DB_PRICES.get(week.getKey());

			float[][] sups = new float[weekNumber.length][];
			for (int i = 0; i < weekNumber.length; i++) {
				float[] suplementalFactors = new float[DBLabels.COMPUTED.length];
				float[] fundies = weekNumber[i];
				float[][] techies = techs[i];
				suplementalFactors[0] = fundies[40] / fundies[41];
				// compute all other suplementals
				//
				//
				//
				//
				sups[i] = suplementalFactors;
			}
			DB_SUP.put(week.getKey(), sups);
		}
	}

	private void calculateChanges() {
		// Collection pcs = prices.values();
		TreeSet<Float> dates = new TreeSet<Float>(PRICES.keySet());
		// float[][] asArray = new float[dates.size()][dbSet.length];
		// allocate variables
		float[] startPrices = null;
		float[] endPrices;
		Iterator<Float> datadays = dates.iterator();
		float startDate = 0;
		// get initial prices array and timekey
		if (datadays.hasNext()) {
			startDate = datadays.next();
			startPrices = PRICES.get(startDate);
		}
		// cycle through the rest calculating changes
		while (datadays.hasNext()) {
			float endDate = datadays.next();
			endPrices = PRICES.get(endDate);
			// calculates price change array and adds it to UNFORESEEN
			// System.out.println("start: " + Arrays.toString(startPrices));
			// System.out.println("end  : " + Arrays.toString(endPrices));
			calculateAllChangesForInterval(startDate, startPrices, endPrices);
			startDate = endDate;
			startPrices = endPrices;
		}
	}

	private void calculateAllChangesForInterval(float startDate,
			float[] startPrices, float[] endPrices) {
		float[] changes = new float[dbSet.size()];
		int errors = 0;
		for (int i = 0; i < dbSet.size(); i++) {
			if (endPrices[i] != endPrices[i]
					|| startPrices[i] != startPrices[i])
				errors++;
			// can be NaN for now
			if (startPrices[i] > 0)
				changes[i] = (endPrices[i] - startPrices[i]) / startPrices[i];
			else
				changes[i] = 0.0f / 0.0f;// NaN
			// changes[i] = (endPrices[i]/startPrices[i] ) -1 ;//alternate
			// method
		}

		// System.out.println("changes: 	" + Arrays.toString(changes));
		WEEKLY_MARKETCHANGE.put(startDate, averageMarketChange(changes));
		UNFORESEEN.put(startDate, changes);
	}

	private Float averageMarketChange(float[] changes) {
		float sum = 0;
		float valid = 0;
		for (float f : changes) {
			if (f == f) {
				sum += f;
				valid++;
			}
		}
		// System.out.println("SUMMING GIVES :   " + sum);
		return sum / valid;
	}

	private void loadDatabaseWithData(String root) {
		time = System.currentTimeMillis();
		// try to load from obj else {
		root += File.separator + "sm" + File.separator;
		System.out.println("---*****************************--->>>>>>>" + root);
		files_q = new File(root + "q").list();
		Arrays.sort(files_q);
		files_y = new File(root + "y").list();
		Arrays.sort(files_y);
		System.out.println("loading database: " + files_q.length
				+ " files to load");
		// for each file store last price for each ticker

		for (int i = 0; i < files_q.length; i++) {
			System.out.println("loading files " + files_q[i] + "    "
					+ files_y[i]);

			convertFileDataToArray(i, root);

		}
addMapToDividendDataForEachTicker();
		DataUtil.loadStringData(EarningsTest.REPORTS_ROOT
				+ "NASDAQ_PROFILES_I.txt", DESCRIPTIONS);
		DataUtil.loadStringData(EarningsTest.REPORTS_ROOT
				+ "NYSE_PROFILES_I.txt", DESCRIPTIONS);
		calculateWordStatistics(DESCRIPTIONS, WORD_STATS);
	}

	private void addMapToDividendDataForEachTicker() {
	for(String s: dbSet){
		DIVIDEND_HISTORY.put(s, new TreeMap<Float,Float>());
	}
		
	}

	private void calculateWordStatistics(TreeMap<String, String> descriptions2,
			TreeMap<String, Integer> wordStats) {
		for (String line : descriptions2.values()) {
			String reformatted = Database.simplifyText(line);
			String[] words = reformatted.split(" ");
			for (String word : words) {
				if (wordStats.containsKey(word)) {
					wordStats.put(word, wordStats.get(word) + 1);
				} else {
					wordStats.put(word, 1);
				}
			}
		}
	}

	public static String simplifyText(String line) {
		String reformatted = line.toLowerCase().replaceAll(",", "")
				.replaceAll("[^A-Za-z]", " ");
		while (reformatted.contains("  ")) {
			reformatted = reformatted.replaceAll("  ", " ");
		}
		return reformatted;
	}

	// for each file i convert stored data to numeric data
	private void convertFileDataToArray(int i, String root) {
		// for each time stage file construct array 85 fundamental data
		// points for each symbol
		float[][] data = new float[dbSet.size()][];
		// for each time stage file construct array 7 technical data points
		// for each symbol
		float[][][] pdata = new float[dbSet.size()][][];
		float[] weeksPrices = new float[dbSet.size()];
		TreeMap<String, String> textData = new TreeMap<String, String>();
		Float days = Float.parseFloat(files_q[i].replaceAll("\\.txt", "")
				.split("_")[1]);
		if (!checkForObjectRestore(i, days, root)) {
			DataUtil.loadStringData(root + "q" + File.separator + files_q[i],
					textData);
			DataUtil.loadStringData(root + "y" + File.separator + files_y[i],
					textData);
			int nullcount = 0;
			// ASSUMES A 1 TO 1 EXISTENCE OF NAS AND NY FILES - TRUE SO FAR
			for (int j = 0; j < dbSet.size(); j++) {
				String ticker = dbSet.get(j);
				String textdata = textData.get(ticker);

				if (textdata == null) {
					totalNull++;
					losses.add(ticker);
					nullcount++;
				}

				final int[] sizes = { 2, 36, 44, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
						1, 1, 1 };
				// fundamental data points
				final float[] values = new float[labels.length];
				// technical data points: last ten days (dayNumber open high low
				// close volume adjClose)
				// adding some measure of volatility in price and volume
				//
				final float[][] dailyData = new float[10][TECHNICAL.length];

				if (textdata != null) {

					// still need validation aspect
					float[] rawData = DataUtil.validSmallDataSet(textdata,
							sizes);
					if (rawData.length != 175)
						continue;
					dataRead++;
					// puts fundamental data points from rawData into values
					fillFundamentalData(values, rawData);
					// puts technical data from rawData into dailyData
					// System.out.println(dbSet[j]);
					fillTechnicalData(dailyData, rawData);
					// sets price values directly to float[][] prices
					fillPriceData(j, days, weeksPrices, dailyData);
				} else {
					dataNotRead++;
					Arrays.fill(values, Float.NaN);
					for (int z = 0; z < dailyData.length; z++)
						Arrays.fill(dailyData[z], Float.NaN);
				}
				// assign actual values to database
				data[j] = values;
				pdata[j] = dailyData;
			} // System.out.println(nullcount + " null  ");
			System.out.println(i + " a file pair was loaded, elapsed time: "
					+ (System.currentTimeMillis() - time) / 1000 + " seconds");
			// add data to fundamental series
			DB_ARRAY.put(days, data);
			// add data to technical series
			DB_PRICES.put(days, pdata);
			// add weeks prices to prices - actual prices of stocks for that
			PRICES.put(days, weeksPrices);
			// complete value of market each week
			float marketSum = sum(weeksPrices);
			MARKET.put(days, marketSum);
			// VALID_COUNT.put(days, valid(weeksPrices));

			// float[][] data = new float[dbSet.length][];
			DataUtil.memorizeObject(data, root + File.separator + "OBJECTS"
					+ File.separator + "DATA_" + days);
			// float[][][] pdata = new float[dbSet.length][10][7];
			DataUtil.memorizeObject(pdata, root + File.separator + "OBJECTS"
					+ File.separator + "TECHNICAL_" + days);
			// float[] weeksPrices = new float[dbSet.length];
			DataUtil.memorizeObject(weeksPrices, root + File.separator
					+ "OBJECTS" + File.separator + "PRICES_" + days);
			// float marketSum = sum(weeksPrices);
			DataUtil.memorizeObject(marketSum, root + File.separator
					+ "OBJECTS" + File.separator + "SUM_" + days);

		}
		Thread.yield();
	}

	private Integer valid(float[] weeksPrices) {
		int v = 0;
		for (float f : weeksPrices) {
			if (f == f)
				v++;
		}
		return v;
	}

	private boolean checkForObjectRestore(int i, float days, String root) {

		// float[][] data = new float[dbSet.length][];

		float[][] data = DataUtil.restore2D(root + File.separator + "OBJECTS"
				+ File.separator + "DATA_" + days);
		if (data == null)
			return false;
		// float[][][] pdata = new float[dbSet.length][10][7];
		float[][][] pdata = DataUtil.restore3D(root + File.separator
				+ "OBJECTS" + File.separator + "TECHNICAL_" + days);
		if (pdata == null)
			return false;
		// float[] weeksPrices = new float[dbSet.length];
		float[] weeksPrices = DataUtil.restore1D(root + File.separator
				+ "OBJECTS" + File.separator + "PRICES_" + days);
		if (weeksPrices == null)
			return false;
		// float marketSum = sum(weeksPrices);
		Float marketSum = DataUtil.restoreFloat(root + File.separator
				+ "OBJECTS" + File.separator + "SUM_" + days);
		if (marketSum == null)
			return false;

		// add data to fundamental series
		DB_ARRAY.put(days, data);
		// add data to technical series
		DB_PRICES.put(days, pdata);
		// add weeks prices to prices - actual prices of stocks for that
		PRICES.put(days, weeksPrices);
		// complete value of market each week
		MARKET.put(days, marketSum);

		return true;
	}

	private Float sum(float[] weeksPrices) {
		float sum = 0;
		for (float f : weeksPrices) {
			if (f == f) {
				sum += f;
			}
		}
		return sum;
	}

	TreeSet<Integer> diffs = new TreeSet<Integer>();

	private void fillPriceData(int j, float days, float[] weeksPrices,
			float[][] dailyData) {
		// days if float rep of this weeks date of collection - dailyData[0][0]
		// is most recent dayNumber representation of historic prices data
		// dailyData[0][6] is adjClose of most recent day
		if (days - dailyData[0][0] < 4 && days - dailyData[0][0] >= 0) {
			weeksPrices[j] = dailyData[0][6];
			// dateset.add((int) (days - dailyData[0][0]));//
			// /////////////////////////////////////////
		} else {
			weeksPrices[j] = Float.NaN;
		}

	}

	// TreeSet<Float> dateset = new TreeSet<Float>();
	// FrequencyMap dateset = new FrequencyMap(FrequencyMap.INTEGER);

	private void fillTechnicalData(float[][] dailyData, float[] rawData) {

		// construct technical data
		// 82 - 171

		// 82 = month
		// 83 = day
		// 84 = year
		// 85-90 = 6 daily data points
		// repeats 10 times
		// System.out.println("   --------------              ---------------------    -----");
		for (int x = 0; x < 10; x++) {
			final int month = (int) rawData[82 + 9 * x] + 1;
			final int day = (int) rawData[83 + 9 * x];
			final int year = (int) rawData[84 + 9 * x];
			String mnth = "";
			if (month < 10)
				mnth += "0";
			mnth += month;
			String dy = "";
			if (day < 10)
				dy += "0";
			dy += day;
			try {
				final Date date1 = new SimpleDateFormat("MM/dd/yy").parse(mnth
						+ "/" + dy + "/" + year);
				final long moneyTime = date1.getTime();
				float dayTime = moneyTime / 1000.0f;
				dayTime /= 3600.0f;
				dayTime /= 24.0f;
				// dateset.add(dayTime);////////////////////////////////////////////////////////////
				final float open = rawData[85 + 9 * x];
				final float high = rawData[86 + 9 * x];
				final float low = rawData[87 + 9 * x];
				final float close = rawData[88 + 9 * x];
				final float vol = rawData[89 + 9 * x];
				final float adjClose = rawData[90 + 9 * x];
				final float[] dlda = { dayTime, open, high, low, close, vol,
						adjClose };
				// System.out.println(Arrays.toString(dlda));
				dailyData[x] = dlda;
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

	}

	private void fillFundamentalData(float[] values, float[] rawData) {
		for (int k = 0; k < 82; k++) {
			values[k] = rawData[k];
		}
		values[82] = rawData[172];
		values[83] = rawData[173];
		values[84] = rawData[174];

		// values[85] = values[40] / values[41];
		// pet/pef assumed lower is better:
		// under 1 especially
		// values[86] = values[69] / values[70];
		// 50day/200day assumed higher is
		// good indicates - recent price
		// increase
		// values[87] = values[72] / values[71];
		// 10day/300day volume higher
		// assumed good - recent
		// increase
		// in volume

	}

	// cycle through DB_ARRAY check each weeks data and construct int->(tickers)
	// that meet criteria
	//

	// use DB_ARRAY to construct bundles

	// use price (TreeMap<Float, float[]>) to calculate bundles
	public static void computeLimitChanges() {
		for (Entry<Float, float[][]> ent : DB_ARRAY.entrySet()) {
			final float date = ent.getKey();
			final float[][] data = ent.getValue();
			ArrayList<Integer> bundle = new ArrayList<Integer>();
			for (int i = 0; i < data.length; i++) {
				// include this symbol unless its values don't meet the
				// requirements
				boolean isIn = true;

				for (Entry<Integer, Point2D.Float> lims : LIMITS.entrySet()) {
					int id = lims.getKey();// the id of the financial data
											// parameter
					float lowlimit = lims.getValue().x;// lower limit
					float highlimit = lims.getValue().y;// upper limit
					// if data is invalid , or outside the limits exclude i
					// for each id restriction limit bundle to valid data (not
					// nan) and withing range
					if (false) { // DataControlls.INVERT[id].isSelected()) {
						if (data[i][id] != data[i][id]
								|| (data[i][id] > lowlimit && data[i][id] < highlimit)) {
							isIn = false;
							// doesn't meet criteria nan or out of range
							// therefore
							// dont store it
							// in fact no need to continue for this i
							break;
						}
					} else {
						if (data[i][id] != data[i][id]
								|| data[i][id] < lowlimit
								|| data[i][id] > highlimit) {
							isIn = false;
							// doesn't meet criteria nan or out of range
							// therefore
							// dont store it
							// in fact no need to continue for this i
							break;
						}
					}
				}
				// if all data points are satisfied add to bundle
				if (isIn)
					bundle.add(i);

			}

			BUNDLES.put(date, bundle);
		}
		// .....................................
		// out of loop we should have bundles for each date which meet the
		// requirements
		// compute the value of the bundles using prices
		float lastDate = Float.NaN;
		float lastsum = Float.NaN;
		ArrayList<Integer> lastSet = null;
		for (Entry<Float, ArrayList<Integer>> entr : BUNDLES.entrySet()) {
			float date = entr.getKey();
			ArrayList<Integer> valids = entr.getValue();
			// System.out.println(date+"-------------------------->"+valids);//////////////////////////////////
			final float[] prcs = PRICES.get(date);
			float sum = 0;
			for (int include : valids) {
				if (prcs[include] == prcs[include])
					sum += prcs[include];
			}
			if (lastsum == lastsum) {
				float change = (sum - lastsum) / lastsum;
				BUNDLES_CHANGES.put(lastDate, change);
				BUNDLES_SIZES.put(lastDate, valids.size());
				SET_CHANGES.put(lastDate, compareSets(lastSet, valids));
				// System.out.println("constructing BUNDLES_CHANGES  --->"+BUNDLES_CHANGES);
			}
			lastSet = valids;
			lastDate = date;
			lastsum = sum;
		}

		// return BUNDLES_CHANGES;
	}

	private static Point compareSets(ArrayList<Integer> lastSet,
			ArrayList<Integer> valids) {
		int sell = 0;
		int buy = 0;

		for (Integer I : lastSet) {
			if (!valids.contains(I))
				sell++;
		}
		for (Integer I : valids) {
			if (!lastSet.contains(I))
				buy++;
		}

		return new Point(buy, sell);
	}

	static String[] concat(String[] A, String[] B) {
		int aLen = A.length;
		int bLen = B.length;
		String[] C = new String[aLen + bLen];
		System.arraycopy(A, 0, C, 0, aLen);
		System.arraycopy(B, 0, C, aLen, bLen);
		return C;
	}

	public void testValues(int id) {
		ArrayList<Float> stats = new ArrayList<Float>();
		long time = System.currentTimeMillis();
		for (Entry<Float, float[][]> ent : DB_ARRAY.entrySet()) {
			float[][] dats = ent.getValue();
			for (float[] d : dats) {
				stats.add(d[id]);
			}
		}

		StatInfo show = new StatInfo(stats, id);
		// //show.setTitle(labels[id]);
		System.out.println(show);
	}
	// ABCO^8_+2.65
	// 0.29_0.32_1.30_1.51_9.00_9.00_9.00_7.00_0.26_0.30_1.26_1.16_0.32_0.34_1.35_1.70_0.31_0.31_1.23_1.30_0.24_0.29_0.30_0.29_0.31_0.31_0.28_0.33_0.07_0.02_-0.02_0.04_29.20%_6.90%_-6.70%_13.80%
	// 1.95B_1.87B_90.23_36.45_2.34_4.32_6.85_4.14_29.24_4.92%_10.81%_3.85%_8.85%_450.84M_12.98_N/A_210.80M_63.81M_22.16M_0.61_N/A_74.44M_2.10_0.00_N/A_0.93_8.01_81.83M_38.80M_-0.12_8.84%_52.88_51.05_275,079_106,357_35.50M_35.28M_5.25%_96.80%_2.01M_8.40_5.90%_1.63M_N/A
	// Jul_5_2013_55.78_55.78_54.12_55.04_89900_55.04@Jul_3_2013_54.66_55.18_54.27_54.84_57500_54.84@Jul_2_2013_54.69_55.53_54.23_55.06_97100_55.06@Jul_1_2013_55.17_55.27_54.68_54.79_115600_54.79@Jun_28_2013_54.41_55.31_53.95_54.65_167900_54.65@Jun_27_2013_54.47_54.71_53.93_54.65_96500_54.65@Jun_26_2013_54.01_54.44_53.44_54.13_120000_54.13@Jun_25_2013_54.18_54.18_53.27_53.67_105200_53.67@Jun_24_2013_53.33_54.13_52.45_53.69_139400_53.69@Jun_21_2013_53.26_54.62_53.25_53.61_244400_53.61
	// 0.0 1.0 3

}
