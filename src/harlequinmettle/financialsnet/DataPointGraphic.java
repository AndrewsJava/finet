package harlequinmettle.financialsnet;

import harlequinmettle.financialsnet.interfaces.DBLabels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class DataPointGraphic {

	public static final int PIXELS_TOP = 30;
	public static final int PIXELS_BORDER = 3;
	public static final int PIXELS_HEIGHT = 150;
	public static final int PERCENT_CHANGE_COMPARISON_RANGE_ABS = 20;

	public static Color FAINT_RED_BLUE = new Color(200, 30, 130, 80);
	public static Color FAINT_GREEN_BLUE = new Color(30, 200, 130, 80);


	public static Color FAINT_RED = new Color(200, 30, 30, 150);
	public static Color FAINT_GREEN = new Color(30, 200, 30, 150);
	

	public static Color FAINT_WHITE = new Color(220, 220, 220, 150);
	public static Color FAINT_BLACK = new Color(20, 20, 20, 150);

	public static Color COLOR_HISTOGRAM_BAR_THIS = new Color(200, 200, 200, 150);
	public static Color COLOR_HISTOGRAM_BAR = new Color(100, 100, 250, 150);
	public static Color COLOR_HISTOGRAM_BAR_VOL = new Color(55, 95, 230, 100);
	private static final TreeMap<Integer, Color> COLOR_MAP;
	static {
		COLOR_MAP = new TreeMap<Integer, Color>();
		for (int i = 0; i < PERCENT_CHANGE_COMPARISON_RANGE_ABS; i++) {
			COLOR_MAP.put((-i), new Color(i * 5 + 150, 40 - i, 60 - i, 180));
			COLOR_MAP.put(i, new Color(40 + i, i * 5 + 150, 60 + i, 180));
		}
	}
	private static final Comparator<Line2D.Float> cusotomComparator = new Comparator<Line2D.Float>() {
		@Override
		public int compare(Line2D.Float s1, Line2D.Float s2) {
			return (int) (((s1.x1+s1.y1) - (s2.x1+s2.y2)) * 10000);
		}
	};
	private final TreeMap<Line2D.Float, Color> PERCENT_CHANGE_COMPARISON_LINES = new TreeMap<Line2D.Float, Color>(
			cusotomComparator);
	// previously histos
	private ArrayList<Rectangle2D.Float> bars;

	private Rectangle2D.Float border;

	private Point2D.Float minMaxLine = new Point2D.Float(0, 0);
	private Point2D.Float minMaxBars = new Point2D.Float(0, 0);
	private Point2D.Float minMaxHisto = new Point2D.Float(0, 0);

	private GeneralPath timePath;
	private final TreeMap<Float, Point2D.Float> timePathPoints = new TreeMap<Float, Point2D.Float>();
	private static final BasicStroke STROKE = new BasicStroke(4f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	private String category = "";
	private int categoryId;

	private String ticker = "";
	private int id;
	private int verticalSizeInt = 1;
	private int reorderRanking;
	// sum rank*heightFactor for each object created;
	public static int graphicRank = 0;
	private float barwidth = (eWidth - 2 * PIXELS_BORDER) / StatInfo.nbars;
	private boolean general = true;
	TreeMap<Float, float[]> timeSeriesCompayData = new TreeMap<Float, float[]>();
	float top, left;
	private float graphInterval;
	public static float rectWidth;
	public static float eWidth;

	// TreeMap<Float, float[]> technicals;

	public DataPointGraphic(String category, String ticker) {
		init(category, ticker);
	}

	public DataPointGraphic(String category, String ticker, int heightFactor) {
		this.verticalSizeInt = heightFactor;
		general = false;
		init(category, ticker);

	}

	private void init(String category2, String ticker2) {
		this.category = category2;
		this.ticker = ticker2;
		PERCENT_CHANGE_COMPARISON_LINES.clear();
		timeSeriesCompayData.clear();
		eWidth = ProfileCanvas.W - 40;
		id = Database.dbSet.indexOf(ticker);
		List list = Arrays.asList(DBLabels.priorityLabeling);
		this.reorderRanking = list.indexOf(category2);

		list = Arrays.asList(DBLabels.labels);
		this.categoryId = list.indexOf(category2);

		top = PIXELS_BORDER + graphicRank * PIXELS_BORDER + graphicRank
				* PIXELS_HEIGHT;

		left = PIXELS_BORDER;
		border = new Rectangle2D.Float(PIXELS_BORDER, PIXELS_BORDER
				+ graphicRank * PIXELS_BORDER + graphicRank * PIXELS_HEIGHT,
				eWidth - 2 * PIXELS_BORDER, PIXELS_HEIGHT * verticalSizeInt);

		if (general) {
			graphicRank += verticalSizeInt;
			setUpGeneralGraphData();
		} else {
			graphicRank += verticalSizeInt;
			setUpTechnicalsGraphData();
		}

		// System.out.println(this);
	}

	@Override
	public String toString() {

		return "\n reorderRanking: " + reorderRanking + //
				"\n ticker: " + ticker + //
				"\n category: " + category + //
				"\n min max line: " + minMaxLine + //
				"\n verticalSizeInt: " + verticalSizeInt + //
				"\n graphciRank: " + graphicRank + //
				"\n top: " + top;
	}

	private void setMinMaxHistogramHighlight() {

		ArrayList<Float> variations = new ArrayList<Float>();
		for (float[] timeSeries : timeSeriesCompayData.values()) {
			variations.add(timeSeries[categoryId]);
		}
		float min = min(variations);
		float max = max(variations);
		if ((int) (min * 1e7) == -1)
			min = Float.NaN;
		if ((int) (max * 1e7) == -1)
			max = Float.NaN;
		if (min == min)
			minMaxHisto.x = Database.statistics.get(categoryId)
					.locationInHistogram(min);
		else
			minMaxHisto.x = -10;

		if (max == max)
			minMaxHisto.y = Database.statistics.get(categoryId)
					.locationInHistogram(max);
		else
			minMaxHisto.y = -10;

	}

	private void setUpTechnicalsGraphData() {

		TreeMap<Float, float[]> technicals = Database.TECHNICAL_PRICE_DATA
				.get(Database.dbSet.get(id));
		TreeMap<Float, Point2D.Float> marketToStockPairing = pairPriceWithMarketByDate(
				technicals, 6);
		TreeMap<Float, Float> stockPrices = makeListFromPricePair(
				marketToStockPairing, 0);
		bars = createVolumeBars(technicals);

		timePath = makePathFromData(stockPrices);
		// depends on timePathPoints
		generatePercentComparisonLines(marketToStockPairing);
	}

	private void generatePercentComparisonLines(
			TreeMap<Float, Point2D.Float> marketToStockPairing) {
		Point2D.Float dailyComparisonInitial = marketToStockPairing
				.firstEntry().getValue();
		boolean first = true;
		for (Entry<Float, Point2D.Float> ent : marketToStockPairing.entrySet()) {
			if (first) {
				first = false;
				continue;
			}
			Point2D.Float dailyComparisonFinal = ent.getValue();

			int individualDelta = (int) (calculateDifferenceIndividual(
					dailyComparisonInitial, dailyComparisonFinal) * 1000);

			int marketDelta = (int) (calculateDifferenceMarket(
					dailyComparisonInitial, dailyComparisonFinal) * 1000);

			 int individualToMarketDelta = (int) ((individualDelta -
			 marketDelta));

			// System.out.println(
			// "percent change comparison:   "+individualToMarketDelta);

			Point2D.Float startPoint = timePathPoints.get(ent.getKey());

			float individualX = startPoint.x;
			float individualY = startPoint.y;

			float changeX = individualX;
			float changeY = individualY - marketDelta * 10;

			Line2D.Float comparisonLineMarket = new Line2D.Float(individualX,
					individualY, changeX, changeY);

			float changeYindividual = individualY - individualDelta * 10;

			Line2D.Float comparisonLineIndividual = new Line2D.Float(
					individualX, individualY, changeX, changeYindividual);
			
			float changeYMarketToIndividual = individualY - individualToMarketDelta * 10;

			Line2D.Float comparisonLineIndividualToMarket = new Line2D.Float(
					individualX, individualY, changeX, changeYMarketToIndividual);
			// if (individualToMarketDelta >
			// PERCENT_CHANGE_COMPARISON_RANGE_ABS)
			// individualToMarketDelta = PERCENT_CHANGE_COMPARISON_RANGE_ABS;
			// if (individualToMarketDelta <
			// -PERCENT_CHANGE_COMPARISON_RANGE_ABS)
			// individualToMarketDelta = -PERCENT_CHANGE_COMPARISON_RANGE_ABS;
			// Color relativeColor = COLOR_MAP.get(individualToMarketDelta);
			if (marketDelta < 0)
				PERCENT_CHANGE_COMPARISON_LINES.put(comparisonLineMarket,
						FAINT_RED_BLUE);
			else
				PERCENT_CHANGE_COMPARISON_LINES.put(comparisonLineMarket,
						FAINT_GREEN_BLUE);
			if (individualDelta < 0)
				PERCENT_CHANGE_COMPARISON_LINES.put(comparisonLineIndividual,
						FAINT_RED);
			else
				PERCENT_CHANGE_COMPARISON_LINES.put(comparisonLineIndividual,
						FAINT_GREEN);

			if (individualToMarketDelta > 0)
				PERCENT_CHANGE_COMPARISON_LINES.put(comparisonLineIndividualToMarket,
						FAINT_WHITE);
			else
				PERCENT_CHANGE_COMPARISON_LINES.put(comparisonLineIndividualToMarket,
						FAINT_BLACK);
			dailyComparisonInitial = dailyComparisonFinal;

		}
	}

	private float calculateDifferenceIndividual(
			Point2D.Float dailyComparisonInitial,
			Point2D.Float dailyComparisonFinal) {

		float marketInitial = dailyComparisonInitial.y;
		float marketFinal = dailyComparisonFinal.y;
		float marketChange = (marketFinal - marketInitial) / marketInitial;

		float individualInitial = dailyComparisonInitial.x;
		float individualFinal = dailyComparisonFinal.x;
		float individualChange = (individualFinal - individualInitial)
				/ individualFinal;
		return individualChange;
	}

	private float calculateDifferenceMarket(
			Point2D.Float dailyComparisonInitial,
			Point2D.Float dailyComparisonFinal) {

		float marketInitial = dailyComparisonInitial.y;
		float marketFinal = dailyComparisonFinal.y;
		float marketChange = (marketFinal - marketInitial) / marketInitial;

		float individualInitial = dailyComparisonInitial.x;
		float individualFinal = dailyComparisonFinal.x;
		float individualChange = (individualFinal - individualInitial)
				/ individualFinal;
		return marketChange;
	}

	private void setUpGeneralGraphData() {
		bars = setUpBars(Database.statistics.get(categoryId).histogram);

		for (Entry<Float, float[][]> allData : Database.DB_ARRAY.entrySet()) {
			// get all company data (all 87 pts) for each collected set
			timeSeriesCompayData.put(allData.getKey(), allData.getValue()[id]);//

		}

		timePath = makePathFromData(makeListFromPointInArray(
				timeSeriesCompayData, categoryId));

		setMinMaxHistogramHighlight();
	}

	public ArrayList<Rectangle2D.Float> setUpBars(int[] histogram) {
		int max = maxBar(histogram);
		ArrayList<Rectangle2D.Float> histoBars = new ArrayList<Rectangle2D.Float>();
		float graphicsScale = ((float) PIXELS_HEIGHT * verticalSizeInt) / max;
		for (int i = 0; i < StatInfo.nbars; i++) {

			float htop = top
					+ (PIXELS_HEIGHT * verticalSizeInt * 1f - (graphicsScale * 1f * histogram[i]));
			float left = PIXELS_BORDER + (barwidth) * i;
			float width = (int) (barwidth);
			float height = (int) (graphicsScale * histogram[i]);
			histoBars.add(new Rectangle2D.Float(left, htop, width, height));
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

	private float roundTo(float numberToRound, int placesToRoundTo) {
		return new BigDecimal(numberToRound).round(
				new MathContext(placesToRoundTo)).floatValue();
	}

	private TreeMap<Float, Float> makeListFromPointInArray(
			TreeMap<Float, float[]> coData, int id) {

		TreeMap<Float, Float> pts = new TreeMap<Float, Float>();
		for (Entry<Float, float[]> data : coData.entrySet()) {

			pts.put(data.getKey(), data.getValue()[id]);
		}
		return pts;
	}

	// coordinate individual with total market
	private TreeMap<Float, Point2D.Float> pairPriceWithMarketByDate(
			TreeMap<Float, float[]> technicals, int idPt) {
		TreeMap<Float, Point2D.Float> pricePoints = new TreeMap<Float, Point2D.Float>();
		for (Entry<Float, float[]> ent : technicals.entrySet()) {
			Point2D.Float priceMatch = new Point2D.Float(ent.getValue()[idPt],
					Database.SUM_MARKET_PRICE_DATA.get(ent.getKey())[idPt]);
			pricePoints.put(ent.getKey(), priceMatch);
		}
		return pricePoints;
	}

	// coordinate one point with date number
	private TreeMap<Float, Float> mapDateNumberToValue(
			TreeMap<Float, float[]> technicals, int idPt) {
		TreeMap<Float, Float> pricePoints = new TreeMap<Float, Float>();
		for (Entry<Float, float[]> ent : technicals.entrySet()) {
			pricePoints.put(ent.getKey(), ent.getValue()[idPt]);
		}
		return pricePoints;
	}

	// coordinate individual with total market
	private TreeMap<Float, Float> makeListFromPricePair(
			TreeMap<Float, Point2D.Float> priceByDate, int id) {

		TreeMap<Float, Float> pts = new TreeMap<Float, Float>();
		for (Entry<Float, Point2D.Float> prices : priceByDate.entrySet()) {
			switch (id) {
			case 0:
				pts.put(prices.getKey(), prices.getValue().x);
				break;
			case 1:
				pts.put(prices.getKey(), prices.getValue().y);
				break;
			}
		}
		return pts;
	}

	private GeneralPath makePathFromData(TreeMap<Float, Float> pts) {
		GeneralPath trend = new GeneralPath();
		timePathPoints.clear();
		// /////////////////////
		float minimumPt = roundTo(min(new ArrayList<Float>(pts.values())), 3);
		float maximumPt = roundTo(max(new ArrayList<Float>(pts.values())), 3);
		minMaxLine = new Point2D.Float(minimumPt, maximumPt);
		// System.out.println(category + " range: " + minimumPt + "   ---   "
		// + maximumPt);
		float range = maximumPt - minimumPt;
		float vertScaling = (PIXELS_HEIGHT * verticalSizeInt) / range;

		graphInterval = (eWidth - PIXELS_BORDER * 2) / (pts.size());
		int i = 0;
		for (Entry<Float, Float> e : pts.entrySet()) {
			float f = e.getValue();
			float date = e.getKey();
			float xpt = 2 * PIXELS_BORDER + graphInterval * i;

			float ypt = top + PIXELS_HEIGHT * verticalSizeInt
					- (vertScaling * (f - minimumPt));
			if (!general)
				timePathPoints.put(date, new Point2D.Float(xpt, ypt));
			if (i == 0) {
				trend.moveTo(xpt, ypt);
			} else {
				trend.lineTo(xpt, ypt);
			}
			i++;
		}

		// /////////////////////
		return trend;
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

	private ArrayList<java.awt.geom.Rectangle2D.Float> createVolumeBars(
			TreeMap<Float, float[]> technicals) {
		ArrayList<java.awt.geom.Rectangle2D.Float> vol = new ArrayList<java.awt.geom.Rectangle2D.Float>();
		ArrayList<Float> tradeVol = new ArrayList<Float>();
		for (Entry<Float, float[]> entr : technicals.entrySet()) {
			float[] trades = entr.getValue();

			tradeVol.add(trades[5]);

		}

		float max = max(tradeVol);

		float minimumPt = roundTo(min(tradeVol), 3);
		float maximumPt = roundTo(max(tradeVol), 3);
		minMaxBars = new Point2D.Float(minimumPt, maximumPt);

		float graphicsScale = (PIXELS_HEIGHT * verticalSizeInt) / max;
		rectWidth = (eWidth - PIXELS_BORDER * 2f) / tradeVol.size() * 1f;
		int i = 0;
		for (float f : tradeVol) {
			// float top = (H - 30 - graphicsScale * f);
			// float top = (4*PART+BUFFER - 30 - graphicsScale * f);

			float btop = top + PIXELS_HEIGHT * graphicRank - graphicsScale * f;
			float left = PIXELS_BORDER + (rectWidth) * i;
			float width = (rectWidth);
			float height = (graphicsScale * f);
			vol.add(new Rectangle2D.Float(left, btop, width, height));
			i++;
		}

		return vol;
	}

	public void rescale() {
		// PERCENT_CHANGE_COMPARISON_LINES.clear();
		init(category, ticker);
	}

	public void drawMe(Graphics2D g) {
		if (!general)
			drawPercentComparisonLines(g);
		int J = 0;
		for (Rectangle2D.Float bar : bars) {
			if (general) {
				if (minMaxHisto.x == J || minMaxHisto.y == J)
					g.setColor(COLOR_HISTOGRAM_BAR_THIS);
				else
					g.setColor(COLOR_HISTOGRAM_BAR);
			} else {
				g.setColor(COLOR_HISTOGRAM_BAR_VOL);
			}
			g.draw(bar);
			g.fill(bar);
			J++;
		}
		g.setColor(new Color(20, 20, 10 + 10 * 19));
		g.draw(border);

		g.setColor(Color.white);
		g.drawString(category, left + PIXELS_BORDER, top + 15);

		g.drawString("" + minMaxLine.x, eWidth - 100, top + PIXELS_HEIGHT
				- PIXELS_BORDER);
		g.drawString("" + minMaxLine.y, eWidth - 100, top + 13);

		if (general)
			g.setColor(Color.blue);
		else
			g.setColor(Color.magenta);
		g.draw(timePath);
		drawHighlightWindows(g);

	}

	private void drawPercentComparisonLines(Graphics2D g) {
		// System.out.println(
		// PERCENT_CHANGE_COMPARISON_LINES.size()+"      < --------------------- size");
		for (Entry<Line2D.Float, Color> comparison : PERCENT_CHANGE_COMPARISON_LINES
				.entrySet()) {
			Stroke original = g.getStroke();
			// System.out.println(comparison.getValue().getRed()+"  "+comparison.getValue().getGreen()+"  "+comparison.getValue().getBlue());
			// System.out.println( "x1: "+comparison.getKey().x1);
			// System.out.println( "y1: "+comparison.getKey().y1);
			// System.out.println( "x2: "+comparison.getKey().x2);
			// System.out.println( "y2: "+comparison.getKey().y2);
			g.setStroke(STROKE);
			g.setColor(comparison.getValue());
			g.draw(comparison.getKey());
			Ellipse2D.Float elipse = new Ellipse2D.Float(
					comparison.getKey().x1 - 3, comparison.getKey().y1 - 3, 6,
					6);
			g.draw(elipse);
			g.setStroke(original);
		}
	}

	private void drawHighlightWindows(Graphics2D g) {

		// g.setColor(new Color(200, 200, 200, 200));
		// g.fillRect(W - 110, H - 70, 90, 40);
		// g.fillRect(W - 110, H - 4 * PART - 0, 90, 40);
		// g.setColor(new Color(205, 00, 205));
		// g.drawString("" + priceRange.x, W - 100, H - 55);
		// g.drawString("" + priceRange.y, W - 100, H - 4 * PART + 15);
		//
		// g.setColor(new Color(205, 100, 100));
		// g.drawString("" + volRange.x, W - 100, H - 40);
		// g.drawString("" + volRange.y, W - 100, H - 4 * PART + 30);
	}
}
