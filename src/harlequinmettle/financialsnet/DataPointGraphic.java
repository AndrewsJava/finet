package harlequinmettle.financialsnet;

import harlequinmettle.financialsnet.interfaces.DBLabels;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class DataPointGraphic {

	public static final int PIXELS_TOP = 30;
	public static final int PIXELS_BORDER = 3;
	public static final int PIXELS_HEIGHT = 150;
	
	public static Color COLOR_HISTOGRAM_BAR_THIS =  new Color( 200,200,200,150);
	public static Color COLOR_HISTOGRAM_BAR =  new Color( 100,100,250,150 );
	public static Color COLOR_HISTOGRAM_BAR_VOL =  new Color(255, 195, 30,150);
	// previously histos
	private ArrayList<Rectangle2D.Float> bars;
	
	private Rectangle2D.Float border;
	
	private Point2D.Float minMaxLine = new Point2D.Float(0, 0);
	private Point2D.Float minMaxBars = new Point2D.Float(0, 0);
	private Point2D.Float minMaxHisto = new Point2D.Float(0, 0);
	
	private GeneralPath timePath;
	
	private String category = "";
	private int categoryId;
	
	private String ticker = "";
	private int id;
	
	private int verticalSizeInt = 1;
	private int reorderRanking;
	// sum rank*heightFactor for each object created;
	public static int graphicRank = 0;
	private float barwidth = (ProfileCanvas.W - 2 * PIXELS_BORDER)
			/ StatInfo.nbars;
	private boolean general = true;
	ArrayList<float[]> timeSeriesCompayData = new ArrayList<float[]>();
	float top, left;
	public static int rectWidth;

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

		id = Database.dbSet.indexOf(ticker);
		List list = Arrays.asList(DBLabels.priorityLabeling);
		this.reorderRanking = list.indexOf(category2);

		list = Arrays.asList(DBLabels.labels);
		this.categoryId = list.indexOf(category2);

		if (general) {
			graphicRank += verticalSizeInt ;
			setUpGeneralGraphData();
		} else {
			graphicRank += verticalSizeInt * (1);
			setUpTechnicalsGraphData();
		}
		top = PIXELS_BORDER + graphicRank * PIXELS_BORDER + graphicRank
				* PIXELS_HEIGHT;
		left = PIXELS_BORDER;

		border = new Rectangle2D.Float(PIXELS_BORDER, PIXELS_BORDER
				+ graphicRank * PIXELS_BORDER + graphicRank * PIXELS_HEIGHT,
				ProfileCanvas.W - 2 * PIXELS_BORDER, PIXELS_HEIGHT
						* verticalSizeInt);
		System.out.println(this);
	}

	@Override
	public String toString() {
 
		return "\n reorderRanking: "+reorderRanking+//
				"\n category: "+category+//
				"\n verticalSizeInt: "+verticalSizeInt+//
				"\n graphciRank: "+graphicRank+//
				"\n top: "+top;
				}

	private void setMinMaxHistogramHighlight() {

		ArrayList<Float> variations = new ArrayList<Float>();
		for (float[] timeSeries : timeSeriesCompayData) {
			variations.add(timeSeries[categoryId]);
		}
		float min = min(variations);
		float max = max(variations);

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
		ArrayList<Point2D.Float> marketToStockPairing = pairPriceWithMarketByDate(
				technicals, 6);
		ArrayList<Float> stockPrices = makeListFromPricePair(
				marketToStockPairing, 0);
		bars = createVolumeBars(technicals);

		timePath = new GeneralPath();
	}

	private void setUpGeneralGraphData() {
		bars = setUpBars(Database.statistics.get(categoryId).histogram);

		for (float[][] allData : Database.DB_ARRAY.values()) {
			// get all company data (all 87 pts) for each collected set
			timeSeriesCompayData.add(allData[categoryId]);//

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

			int top = PIXELS_BORDER
					+ PIXELS_BORDER
					* graphicRank
					+ PIXELS_HEIGHT
					* graphicRank
					+ (PIXELS_HEIGHT * verticalSizeInt - (int) (graphicsScale * histogram[i]));
			int left = PIXELS_BORDER + (int) (barwidth) * i;
			int width = (int) (barwidth);
			int height = (int) (graphicsScale * histogram[i]);
			histoBars.add(new Rectangle2D.Float(left, top, width, height));
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

	private ArrayList<Float> makeListFromPointInArray(
			ArrayList<float[]> coData, int id) {

		ArrayList<Float> pts = new ArrayList<Float>();
		for (float[] data : coData) {
			pts.add(data[id]);
		}
		return pts;
	}

	// coordinate individual with total market
	private ArrayList<Point2D.Float> pairPriceWithMarketByDate(
			TreeMap<Float, float[]> technicals, int idPt) {
		ArrayList<Point2D.Float> pricePoints = new ArrayList<Point2D.Float>();
		for (Entry<Float, float[]> ent : technicals.entrySet()) {
			Point2D.Float priceMatch = new Point2D.Float(ent.getValue()[idPt],
					Database.SUM_MARKET_PRICE_DATA.get(ent.getKey())[idPt]);
			pricePoints.add(priceMatch);
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

	private GeneralPath makePathFromData(ArrayList<Float> pts) {
		GeneralPath trend = new GeneralPath();

		// /////////////////////
		float minimumPt = roundTo(min(pts), 3);
		float maximumPt = roundTo(max(pts), 3);
		minMaxLine = new Point2D.Float(minimumPt, maximumPt);
		// System.out.println("range: "+minimumPt+"   ---   "+maximumPt);
		float range = maximumPt - minimumPt;
		float localScale = (PIXELS_HEIGHT * verticalSizeInt) / range;
		float graphInterval = (ProfileCanvas.W - PIXELS_BORDER * 2)
				/ (pts.size() - 1);
		int i = 0;
		for (float f : pts) {
			float xpt = 2 * PIXELS_BORDER + graphInterval * i;

			float ypt = PIXELS_BORDER + graphicRank * PIXELS_BORDER
					+ graphicRank * PIXELS_HEIGHT + PIXELS_HEIGHT
					- (localScale * (f - minimumPt));

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
		  rectWidth = (ProfileCanvas.W - PIXELS_BORDER * 2)
				/ tradeVol.size();
		int i = 0;
		for (float f : tradeVol) {
			// float top = (H - 30 - graphicsScale * f);
			// float top = (4*PART+BUFFER - 30 - graphicsScale * f);

			float top = PIXELS_BORDER + PIXELS_BORDER * graphicRank
					+ PIXELS_HEIGHT * graphicRank + PIXELS_HEIGHT * graphicRank
					- graphicsScale * f;
			float left = PIXELS_BORDER + (rectWidth) * i;
			float width = (rectWidth);
			float height = (graphicsScale * f);
			vol.add(new Rectangle2D.Float(left, top, width, height));
			i++;
		}

		return vol;
	}

	public void rescale() {
		graphicRank = 0;
		// init(category, ticker);
	}

	public void drawMe(Graphics2D g) {

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

		g.drawString("" + minMaxLine.x, ProfileCanvas.W - 100, top
				+ PIXELS_HEIGHT - PIXELS_BORDER);
		g.drawString("" + minMaxLine.y, ProfileCanvas.W - 100, top + 13);

		if (general)
			g.setColor(Color.blue);
		else
			g.setColor(Color.magenta);
		g.draw(timePath);
		drawHighlightWindows(g);

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
