package harlequinmettle.financialsnet;
 

import harlequinmettle.financialsnet.interfaces.DBLabels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JPanel;

public class ProfileCanvas extends JPanel {
	public Color bg = new Color(100, 180, 220);
	public static final int BUFFER = 3;
	public static final int PART = 100;
	public static final int W = 900;
	public static final int H = DBLabels.labels.length * PART
			+ DBLabels.labels.length * BUFFER + 4 * PART;

	public Rectangle2D.Float[] borders = new Rectangle2D.Float[DBLabels.labels.length + 1];
	public ArrayList<Rectangle2D.Float[]> histos = new ArrayList<Rectangle2D.Float[]>();
	public ArrayList<Rectangle2D.Float> volume = new ArrayList<Rectangle2D.Float>();
	public ArrayList<Point2D.Float> minsMax = new ArrayList<Point2D.Float>();

	Point2D.Float volRange = new Point2D.Float();
	Point2D.Float priceRange = new Point2D.Float();// /////SET IN METHODS SHOW
													// IN PAINT

	public ArrayList<GeneralPath> indicies = new ArrayList<GeneralPath>();
	private int barwidth = (W - 40) / StatInfo.nbars;
	public static final int nColors = 20;
	public static Color[] scalesR = new Color[nColors];
	public static Color[] scalesB = new Color[nColors];
	public int[] comp = new int[DBLabels.labels.length];
	private GeneralPath pricePath;
	static {
		for (int i = 0; i < nColors; i++) {
			scalesR[i] = new Color(10 + 10 * i, 20, 20);// shades of red
			scalesB[i] = new Color(20, 20, 10 + 10 * i);// shades of
		}
	}

	public ProfileCanvas(int id) {
		setPreferredSize(new Dimension(W, H));
		ArrayList<float[]> coData = new ArrayList<float[]>();
		TreeMap<Float, float[]> technicals = new TreeMap<Float, float[]>();

		for (int i = 0; i < DBLabels.labels.length; i++) {
			Rectangle2D.Float border = new Rectangle2D.Float(BUFFER, BUFFER + i
					* BUFFER + i * PART, W - 40, PART);
			borders[i] = border;

		}
		int number = DBLabels.labels.length;
		Rectangle2D.Float border = new Rectangle2D.Float(BUFFER, BUFFER
				+ number * BUFFER + number * PART, W - 40, 4 * PART);
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
		for (float[][][] tech : Database.DB_PRICES.values()) {
			for (int i = 0; i < tech[id].length; i++) {

				technicals.put(tech[id][i][0], tech[id][i]);
			}

		}
		pricePath = createPricePath(technicals);
		volume = createVolumeBars(technicals);
		for (int i = 0; i < DBLabels.labels.length; i++) {
			indicies.add(makePathFromData(coData, i));
		}
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
		float graphInterval = (W - 40) / (pts.size() - 1);
		int i = 0;
		for (float f : pts) {
			float xpt = 20 + graphInterval * i;

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
		float graphicsScale = ((float) 4 * PART) / max;
		float barwidth = (W - 40) / tradeVol.size();
		int i = 0;
		for (float f : tradeVol) {
			float top = (H - 30 - graphicsScale * f);
			float left = 20 + (barwidth) * i;
			float width = (barwidth);
			float height = (graphicsScale * f);
			vol.add(new Rectangle2D.Float(left, top, width, height));
			i++;
		}

		return vol;
	}

	private float max(ArrayList<Float> data) {
		float max = Float.NEGATIVE_INFINITY;
		for (float f : data) {
			if(f!=f)continue;
			if (f > max)
				max = f;
		}
		if(max==Float.NEGATIVE_INFINITY)
			max = -1e-7f;
		return max;
	}

	private float min(ArrayList<Float> data) {
		float min = Float.POSITIVE_INFINITY;
		for (float f : data) {
			if(f!=f)continue;
			if (f < min)
				min = f;
		}
		if(min==Float.POSITIVE_INFINITY)
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
		// float minimumPt = StatInfo.findMinimum(highlow);
		// float maximumPt = StatInfo.findMaximum(highlow);

		float minimumPt = new BigDecimal(min(adjCloses)).round(
				new MathContext(3)).floatValue();
		float maximumPt = new BigDecimal(max(adjCloses)).round(
				new MathContext(3)).floatValue();
		priceRange = new Point2D.Float(minimumPt, maximumPt);
		// System.out.println("range: "+minimumPt+"   ---   "+maximumPt);
		float range = maximumPt - minimumPt;
		float localScale = (4 * PART) / range;
		float graphInterval = (W - 40) / (technicals.size() - 1);
		int i = 0;
		for (float f : adjCloses) {
			float xpt = 20 + graphInterval * i;

			float ypt = H - (localScale * (f - minimumPt));
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
			int left = BUFFER + ((int) barwidth) * i;
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
		g.drawString("" + volRange.x, W - 100, H-40);
		g.drawString("" + volRange.y, W - 100, H-4*PART+30);
		
		g.setColor(Color.magenta);
		g.draw(pricePath);

		g.drawString("" + priceRange.x, W - 100, H-55);
		g.drawString("" + priceRange.y, W - 100, H-4*PART+15);
 
		g.setColor(Color.blue);
		for (GeneralPath indi : indicies) {
			g.draw(indi);
		}
	}
}
