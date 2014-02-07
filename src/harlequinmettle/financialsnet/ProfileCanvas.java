package harlequinmettle.financialsnet;

import harlequinmettle.financialsnet.interfaces.DBLabels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JPanel;

public class ProfileCanvas extends JPanel {
	SimpleDateFormat showDate = new SimpleDateFormat("YYYY-MMM-dd");
	public Color bg = new Color(100, 180, 220);
	public static final int BUFFER = 3;
	public static final float HORIZONTAL_MARGIN_TOTAL = 70.0F;
	public static final int PART = 150;
	public static int W = 1400;
	public static int H = DBLabels.labels.length * PART
			+ DBLabels.labels.length * BUFFER + 4 * PART;
	public static final int FONT_SIZE = 30;
	public static final Font BIG_FONT = new Font("Sans-Serif", Font.BOLD,
			FONT_SIZE);
	public static final Color TEXT_COLOR = new Color(120, 150, 220, 100);
	public Rectangle2D.Float[] borders = new Rectangle2D.Float[DBLabels.labels.length + 1];
	public ArrayList<Rectangle2D.Float[]> histos = new ArrayList<Rectangle2D.Float[]>();
	public ArrayList<Rectangle2D.Float> volume = new ArrayList<Rectangle2D.Float>();
	public ArrayList<Point2D.Float> minsMax = new ArrayList<Point2D.Float>();

	ArrayList<String> lines = new ArrayList<String>();
	TreeMap<Float, float[]> technicals = new TreeMap<Float, float[]>();
	ArrayList<float[]> coData = new ArrayList<float[]>();

	Point2D.Float volRange = new Point2D.Float();
	Point2D.Float priceRange = new Point2D.Float();// /////SET IN METHODS SHOW
													// IN PAINT

	public ArrayList<GeneralPath> indicies = new ArrayList<GeneralPath>();
	private double barwidth = (W - HORIZONTAL_MARGIN_TOTAL) / StatInfo.nbars;
	public static final int nColors = 20;
	public static Color[] scalesR = new Color[nColors];
	public static Color[] scalesB = new Color[nColors];
	public int[] comp = new int[DBLabels.labels.length];
	private GeneralPath pricePath= new GeneralPath();;
	static {
		for (int i = 0; i < nColors; i++) {
			scalesR[i] = new Color(10 + 10 * i, 20, 20);// shades of red
			scalesB[i] = new Color(20, 20, 10 + 10 * i);// shades of
		}
	}
	int tickerID;
	double date = 0;
	double day = 0;
	double generalInterval = 0;
	int x, y;
	final MouseAdapter dateDisplayer = new MouseAdapter() {

		public void mouseClicked(MouseEvent e) {
			x = e.getX();
			y = e.getY();
			// int index = (int) ((W - 2 * BUFFER) / (x - 2 * BUFFER) /
			// generalInterval* technicals.size()) ;
			int index = (int) ((x - 2 * BUFFER) / generalInterval);
			day = (Float) technicals.keySet().toArray()[index] + 1;
			repaint();
		}
	};
	private int earnDateX;
	private int collectionDateX;

	String dateInfo = "";

	public ProfileCanvas(int id) {
		technicals = Database.TECHNICAL_PRICE_DATA.get(Database.dbSet.get(id));
		addMouseListener(dateDisplayer);
		tickerID = id;
		setTextDescriptionInArray();
		setPreferredSize(new Dimension(W, H));

		for (int i = 0; i < DBLabels.labels.length; i++) {
			Rectangle2D.Float border = new Rectangle2D.Float(BUFFER, BUFFER + i
					* BUFFER + i * PART, W - HORIZONTAL_MARGIN_TOTAL, PART);
			borders[i] = border;
		}
		int number = DBLabels.labels.length;
		Rectangle2D.Float border = new Rectangle2D.Float(BUFFER, BUFFER
				+ number * BUFFER + number * PART, W - HORIZONTAL_MARGIN_TOTAL,
				4 * PART);
		borders[number] = border;

		for (float[][] allData : Database.DB_ARRAY.values()) {
			// get all company data (all 87 pts) for each collected set
			coData.add(allData[id]);//
		}

		int offset = 0;
		for (StatInfo stat : Database.statistics) {
			// compare these companies values to statistics
			Rectangle2D.Float[] histog = setUpBars(stat.histogram, offset);
			histos.add(histog);
			float datapt = coData.get(0)[offset];
			if (datapt == datapt)
				comp[offset] = stat.locationInHistogram(datapt);
			else
				comp[offset] = -10;
			offset++;
			// System.out.println(Arrays.toString(histog));
		}

		// [2500stocks][10days][7indicator]
		// for each weeks data go throug all 10 days for this stock add all
		// technicals
		// "day(float)",//0
		// "open",//1
		// "high",//2
		// "low",//3
		// "close",//4
		// "volume",//5
		// "adjClose",//6
		// for (float[][][] tech : Database.DB_PRICES.values()) {
		// for (int i = 0; i < tech[id].length; i++) {
		// // map date factor to days price/vol data
		// technicals.put(tech[id][i][0], tech[id][i]);
		// }
		//
		// }
		pricePath = createPricePath(technicals);
		volume = createVolumeBars(technicals);
		for (int i = 0; i < DBLabels.labels.length; i++) {
			indicies.add(makePathFromData(coData, i));
		}

	}

	public void setDateInfo(String di) {
		dateInfo = di;
		setDateLines(dateInfo);
	}

	public ProfileCanvas(String dateInfo, int id, int width, int height) {
		technicals = Database.TECHNICAL_PRICE_DATA.get(Database.dbSet.get(id));
		addMouseListener(dateDisplayer);
		tickerID = id;
		this.dateInfo = dateInfo;
		setTextDescriptionInArray();

		for (float[][] allData : Database.DB_ARRAY.values()) {
			// get all company data (all 87 pts) for each collected set
			coData.add(allData[id]);//

		}

		// for (float[][][] tech : Database.DB_PRICES.values()) {
		// for (int i = 0; i < tech[id].length; i++) {
		//
		// technicals.put(tech[id][i][0], tech[id][i]);
		// }
		//
		// }

		rescaleCanvas(new Dimension(width, height));
	}

	private void setDateLines(String dateInfo) {
		String[] dates = dateInfo.split(" ");
		if (dates.length < 2) {
			System.out.println("\n\nARRAY IS SHORT : " + dateInfo + "  -->  "
					+ Arrays.toString(dates));
			return;
		}
		System.out.println("\n data : " + dateInfo + "  -->  "
				+ Arrays.toString(dates));
		try {
			long earningsReportDate = EarningsTest.singleton.dateFormatForFile
					.parse(dates[1]).getTime() / 1000 / 3600 / 24;
			int collectionDate = (int) Double.parseDouble(dates[0]);
			int index = 0;
			for (Entry<Float, float[]> ent : technicals.entrySet()) {
				int datePt = (int) (float) ent.getKey();

				if (earningsReportDate == datePt) {
					earnDateX = convertIndexToScreenPoint(index);
				}
				if (collectionDate == datePt) {
					collectionDateX = convertIndexToScreenPoint(index);
				}
				index++;
			}
			System.out.println("\n\nearnings report date : "
					+ earningsReportDate);
			System.out.println("collection date      : " + collectionDate);
		} catch (ParseException e) {
			System.out
					.println("DATE PARSE ERROR   : " + Arrays.toString(dates));

		}
	}

	private int convertIndexToScreenPoint(int index) {
		int screenPoint = (int) (index * generalInterval + 2 * BUFFER);
		System.out.println("\n\nindex in map: " + index);
		System.out.println("converted to screen : " + screenPoint);
		return screenPoint;
	}

	public void rescaleCanvas(Dimension dim) {
		W = dim.width;
		// H = dim.height;
		histos.clear();
		indicies.clear();
		barwidth = (W - HORIZONTAL_MARGIN_TOTAL) / StatInfo.nbars;
		setPreferredSize(new Dimension(W, H));

		for (int i = 0; i < DBLabels.labels.length; i++) {
			Rectangle2D.Float border = new Rectangle2D.Float(BUFFER, BUFFER + i
					* BUFFER + i * PART, W - HORIZONTAL_MARGIN_TOTAL, PART);
			borders[i] = border;

		}
		int number = DBLabels.labels.length;
		Rectangle2D.Float border = new Rectangle2D.Float(BUFFER, BUFFER
				+ number * BUFFER + number * PART, W - HORIZONTAL_MARGIN_TOTAL,
				4 * PART);
		borders[number] = border;

		int offset = 0;
		List<StatInfo> statList = Collections
				.synchronizedList(Database.statistics);
		for (StatInfo stat : statList) {
			// compare these companies values to statistics
			Rectangle2D.Float[] histog = setUpBars(stat.histogram, offset);
			histos.add(histog);
			float datapt = coData.get(0)[offset];
			if (datapt == datapt)
				comp[offset] = stat.locationInHistogram(datapt);
			else
				comp[offset] = -10;
			offset++;
		}
//  originally
//		pricePath = createPricePath(technicals);
//		volume = createVolumeBars(technicals);
//		for (int i = 0; i < DBLabels.labels.length; i++) {
//			indicies.add(makePathFromData(coData, i));
//		}
		ArrayList<Point2D.Float>  marketToStockPairing = pairPriceWithMarketByDate(6);
		ArrayList<Float> stockPrices = makeListFromPricePair(marketToStockPairing,0);
		pricePath = makePathFromData(stockPrices,1,4);
		volume = createVolumeBars(technicals);
		 ArrayList<String> labelList =  new ArrayList<String>(Arrays.asList(DBLabels.labels));
		for (int i = 0; i < DBLabels.priorityLabeling.length; i++) {
			int ranking =  labelList.indexOf(DBLabels.priorityLabeling)-1;
			if(ranking>=0)
			indicies.add(makePathFromData(makeListFromPointInArray(coData,ranking),4+ranking, 1));
		}

		setDateLines(dateInfo);
	}

	private GeneralPath makePathFromData(ArrayList<Float> pts, int rank,
			int segments) {
	//	GeneralPath trend = new GeneralPath();

		// /////////////////////
		float minimumPt = new BigDecimal(min(pts)).round(new MathContext(3))
				.floatValue();
		float maximumPt = new BigDecimal(max(pts)).round(new MathContext(3))
				.floatValue();
		minsMax.add(new Point2D.Float(minimumPt, maximumPt));
		// System.out.println("range: "+minimumPt+"   ---   "+maximumPt);
		float range = maximumPt - minimumPt;
		float localScale = (PART * segments) / range;
		float graphInterval = (W - HORIZONTAL_MARGIN_TOTAL) / (pts.size() - 1);
		int i = 0;
		for (float f : pts) {
			float xpt = 2 * BUFFER + graphInterval * i;

			float ypt = BUFFER + rank * BUFFER + rank * PART + PART
					- (localScale * (f - minimumPt));

			if (i == 0) {
				pricePath.moveTo(xpt, ypt);
			} else {
				pricePath.lineTo(xpt, ypt);
			}
			i++;
		}

		// /////////////////////
		return pricePath;
	}

	private ArrayList<Float> makeListFromPointInArray(
			ArrayList<float[]> coData, int id) {

		ArrayList<Float> pts = new ArrayList<Float>();
		for (float[] data : coData) {
			pts.add(data[id]);
		}
		return pts;
	}

	//coordinate individual with total market
	private ArrayList<Point2D.Float> pairPriceWithMarketByDate(int idPt) {
		ArrayList<Point2D.Float> pricePoints = new ArrayList<Point2D.Float>();
		for (Entry<Float, float[]> ent : technicals.entrySet()) {
			Point2D.Float priceMatch = new Point2D.Float(ent.getValue()[idPt],
					Database.SUM_MARKET_PRICE_DATA.get(ent.getKey())[idPt]);
			pricePoints.add(priceMatch);
		}
		return pricePoints;
	}

	//coordinate individual with total market
	private ArrayList<Float> makeListFromPricePair(
			ArrayList<Point2D.Float> priceByDate, int id) {

		ArrayList<Float> pts = new ArrayList<Float>();
		for (Point2D.Float prices : priceByDate) {
			switch (id) {
			case 0:
				pts.add(prices.x);
				break;
			case 1:
				pts.add(prices.y);
				break;
			}
		}
		return pts;
	}

	private GeneralPath makePathFromData(ArrayList<float[]> coData, int id) {
		GeneralPath trend = new GeneralPath();
		ArrayList<Float> pts = new ArrayList<Float>();
		for (float[] data : coData) {
			pts.add(data[id]);
		}
		// /////////////////////
		float minimumPt = new BigDecimal(min(pts)).round(new MathContext(3))
				.floatValue();
		float maximumPt = new BigDecimal(max(pts)).round(new MathContext(3))
				.floatValue();
		minsMax.add(new Point2D.Float(minimumPt, maximumPt));
		// System.out.println("range: "+minimumPt+"   ---   "+maximumPt);
		float range = maximumPt - minimumPt;
		float localScale = (PART) / range;
		float graphInterval = (W - HORIZONTAL_MARGIN_TOTAL) / (pts.size() - 1);
		int i = 0;
		for (float f : pts) {
			float xpt = 2 * BUFFER + graphInterval * i;

			float ypt = BUFFER + id * BUFFER + id * PART + PART
					- (localScale * (f - minimumPt));
			// System.out.println("POINTS IN PATH: "+xpt+"   ---   "+ypt);

			if (i == 0) {
				pricePath.moveTo(xpt, ypt);

			} else {
				pricePath.lineTo(xpt, ypt);
			}
			i++;
		}

		// /////////////////////
		return trend;
	}

	private ArrayList<java.awt.geom.Rectangle2D.Float> createVolumeBars(
			TreeMap<Float, float[]> technicals) {
		ArrayList<java.awt.geom.Rectangle2D.Float> vol = new ArrayList<java.awt.geom.Rectangle2D.Float>();
		ArrayList<Float> tradeVol = new ArrayList<Float>();
		for (Entry<Float, float[]> entr : technicals.entrySet()) {
			float[] trades = entr.getValue();

			tradeVol.add(trades[5]);

		}

		float max = max(tradeVol);

		float minimumPt = new BigDecimal(min(tradeVol)).round(
				new MathContext(3)).floatValue();
		float maximumPt = new BigDecimal(max(tradeVol)).round(
				new MathContext(3)).floatValue();
		volRange = new Point2D.Float(minimumPt, maximumPt);
		float graphicsScale = (4 * PART) / max;
		float rectWidth = (W - HORIZONTAL_MARGIN_TOTAL) / tradeVol.size();
		int i = 0;
		for (float f : tradeVol) {
			//float top = (H - 30 - graphicsScale * f);
		//	float top = (4*PART+BUFFER - 30 - graphicsScale * f);
			float top = ((BUFFER + 4 * BUFFER + 4 * PART  ) - 30 - graphicsScale * f);
			float left = 2 * BUFFER + (rectWidth) * i;
			float width = (rectWidth);
			float height = (graphicsScale * f);
			vol.add(new Rectangle2D.Float(left, top, width, height));
			i++;
		}

		return vol;
	}

	private float max(ArrayList<Float> data) {
		float max = Float.NEGATIVE_INFINITY;
		for (float f : data) {
			if (f != f)
				continue;
			if (f > max)
				max = f;
		}
		if (max == Float.NEGATIVE_INFINITY)
			max = -1e-7f;
		return max;
	}

	private float min(ArrayList<Float> data) {
		float min = Float.POSITIVE_INFINITY;
		for (float f : data) {
			if (f != f)
				continue;
			if (f < min)
				min = f;
		}
		if (min == Float.POSITIVE_INFINITY)
			min = -1e-7f;
		return min;
	}

	private GeneralPath createPricePath(TreeMap<Float, float[]> technicals) {
		GeneralPath pricePath = new GeneralPath();
		ArrayList<Float> highlow = new ArrayList<Float>();
		ArrayList<Float> adjCloses = new ArrayList<Float>();
		for (Entry<Float, float[]> entr : technicals.entrySet()) {
			float[] trades = entr.getValue();
			highlow.add(trades[2]);
			highlow.add(trades[3]);
			adjCloses.add(trades[6]);
		}
		float minimumPt = new BigDecimal(min(adjCloses)).round(
				new MathContext(3)).floatValue();
		float maximumPt = new BigDecimal(max(adjCloses)).round(
				new MathContext(3)).floatValue();
		priceRange = new Point2D.Float(minimumPt, maximumPt);
		// System.out.println("range: "+minimumPt+"   ---   "+maximumPt);
		float range = maximumPt - minimumPt;
		float localScale = (4 * PART) / range;
		float graphInterval = (W - HORIZONTAL_MARGIN_TOTAL)
				/ (technicals.size() - 1.0f);
		generalInterval = graphInterval;
		// double graphInterval = barwidth;
		int i = 0;
		for (double f : adjCloses) {
			double xpt = 2 * BUFFER + graphInterval * i;

			double ypt = H - (localScale * (f - minimumPt));
			// System.out.println("POINTS IN PATH: "+xpt+"   ---   "+ypt);

			if (i == 0) {
				pricePath.moveTo(xpt, ypt);

			} else {
				pricePath.lineTo(xpt, ypt);
			}
			i++;
		}

		return pricePath;
	}

	public Rectangle2D.Float[] setUpBars(int[] histogram, int offset) {
		int max = maxBar(histogram);
		Rectangle2D.Float[] histoBars = new Rectangle2D.Float[StatInfo.nbars];

		float graphicsScale = ((float) PART) / max;

		for (int i = 0; i < StatInfo.nbars; i++) {
			int top = BUFFER + BUFFER * offset + PART * offset
					+ (PART - (int) (graphicsScale * histogram[i]));
			int left = BUFFER + (int) (barwidth) * i;
			int width = (int) (barwidth);
			int height = (int) (graphicsScale * histogram[i]);
			histoBars[i] = new Rectangle2D.Float(left, top, width, height);

		}
		return histoBars;
	}

	private int maxBar(int[] histogram) {
		int max = 0;

		for (int i : histogram) {
			if (i > max) {

				max = i;
			}
		}
		return max;
	}

	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, W, H);
		int k = 0;
		for (Rectangle2D.Float[] histo : histos) {
			int j = 0;
			for (Rectangle2D.Float bar : histo) {
				if (comp[k] == j)
					g.setColor(Color.white);
				else

					g.setColor(scalesB[11]);
				g.fill(bar);

				j++;
			}
			k++;
		}
		int i = 0;
		for (Rectangle2D.Float rf : borders) {

			g.setColor(scalesB[19]);
			g.draw(rf);

			if (i == DBLabels.labels.length) {

			} else {
				g.setColor(Color.white);
				g.drawString(DBLabels.labels[i], BUFFER + 3, 2 + i * BUFFER + i
						* PART + 20);

				g.drawString("" + minsMax.get(i).x, W - 100, 2 + i * BUFFER + i
						* PART + PART - 5);
				g.drawString("" + minsMax.get(i).y, W - 100, 2 + i * BUFFER + i
						* PART + 13);
			}
			i++;
		}
		g.setColor(Color.orange);
		for (Rectangle2D.Float v : volume) {
			g.draw(v);
		}

		g.setColor(Color.magenta);
		g.draw(pricePath);

		g.setColor(new Color(200, 200, 200, 200));
		g.fillRect(W - 110, H - 70, 90, 40);
		g.fillRect(W - 110, H - 4 * PART - 0, 90, 40);
		g.setColor(new Color(205, 00, 205));
		g.drawString("" + priceRange.x, W - 100, H - 55);
		g.drawString("" + priceRange.y, W - 100, H - 4 * PART + 15);

		g.setColor(new Color(205, 100, 100));
		g.drawString("" + volRange.x, W - 100, H - 40);
		g.drawString("" + volRange.y, W - 100, H - 4 * PART + 30);
		g.setColor(Color.blue);
		for (GeneralPath indi : indicies) {
			g.draw(indi);
		}
		drawTextInBackground(g);
		drawDateLines(g);
	}

	private void drawDateLines(Graphics2D g) {
		g.setColor(Color.green);
		g.drawLine(x, 0, x, H);
		g.setColor(Color.gray);
		g.drawLine(collectionDateX, 0, collectionDateX, H);
		g.setColor(Color.white);
		g.drawLine(earnDateX, 0, earnDateX, H);
	}

	private void drawTextInBackground(Graphics2D g) {
		Font original = g.getFont();
		g.setColor(TEXT_COLOR);
		g.setFont(BIG_FONT);
		String date = showDate.format(new Date((long) day * 24 * 3600 * 1000));
		if (x > 0.8 * W) {
			g.drawString(date, x - 200, y - 15);
		} else {
			g.drawString(date, x, y - 15);
		}
		int ct = 0;
		for (String someWords : lines) {

			g.drawString(someWords, 150, 50 + ct++ * (FONT_SIZE + 6));
		}
		g.setFont(original);
	}

	private void setTextDescriptionInArray() {
		String t = Database.DESCRIPTIONS.get(Database.dbSet.get(tickerID))
				.replaceAll("_", " ");
		double rankTotal = calculateWordRankTotal(t);
		double rankAverage = calculateWordRankAverage(t);
		lines.add(new String(" "));
		lines.add(new String(" "));
		lines.add("word rank sum: " + rankTotal);
		lines.add("-----");
		lines.add("-----");
		lines.add("word rank avg:  " + rankAverage);
		lines.add("-----");
		lines.add("-----");

		String[] words = t.split(" ");
		int ct = 0;
		int wordsPerLine = 6;
		String line = "";
		boolean foundEmployeeCount = false;
		for (String word : words) {
			if (!foundEmployeeCount && ct < 10) {
				if ((word.replaceAll(",", "")).matches("-?\\d+(\\.\\d+)?")) {

					lines.add(new String(line));
					lines.add("-----");
					lines.add(new String(word));
					lines.add("-----");
					line = "";
					foundEmployeeCount = true;
					continue;
				}
			}
			ct++;
			line += word + " ";
			if (ct % wordsPerLine == 0) {
				lines.add(new String(line));
				line = "";
			}
		}
	}

	private double calculateWordRankTotal(String text) {
		double rank = 0;
		String[] words = Database.simplifyText(text).split(" ");
		for (String word : words) {

			rank += 1.0 / Database.WORD_STATS.get(word);
		}
		return new BigDecimal(rank).round(new MathContext(3)).doubleValue();
	}

	public static double calculateWordRankAverage(String text) {
		double rank = 0;
		String[] words = Database.simplifyText(text).split(" ");
		for (String word : words) {

			rank += 1.0 / Database.WORD_STATS.get(word);
		}
		double value = rank / words.length;
		if (value != value || Double.isInfinite(value))
			value = 0;
		return new BigDecimal(value).round(new MathContext(3)).doubleValue();
	}
}
