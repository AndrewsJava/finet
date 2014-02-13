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
	public static int W = 1400;
	public static int H = DBLabels.labels.length
			* DataPointGraphic.PIXELS_HEIGHT + DBLabels.labels.length
			// fix this 4 for technical graph constant
			* DataPointGraphic.PIXELS_BORDER + 4
			* DataPointGraphic.PIXELS_HEIGHT;
	public static final int FONT_SIZE = 30;
	public static final Font BIG_FONT = new Font("Sans-Serif", Font.BOLD,
			FONT_SIZE);
	public static final Color TEXT_COLOR = new Color(120, 150, 220, 100);

	ArrayList<String> lines = new ArrayList<String>();
	TreeMap<Float, float[]> technicals;

	private int earnDateX;
	private int collectionDateX;

	String dateInfo = "";
	private final ArrayList<DataPointGraphic> charts = new ArrayList<DataPointGraphic>();

	int tickerID;
	double date = 0;
	double day = 0;
	double generalInterval = 0;
	int x, y;
	final MouseAdapter dateDisplayer = new MouseAdapter() {

		public void mouseClicked(MouseEvent e) {
			x = e.getX();
			y = e.getY();
			int index = (int) ((x - DataPointGraphic.PIXELS_BORDER) / DataPointGraphic.rectWidth);
			day = (Float) technicals.keySet().toArray()[index];
			repaint();
		}
	};

//	ProfileCanvas pc = new ProfileCanvas(reformatTitle(buttonData),
//			tickerLocation, jf.getWidth(), jf.getHeight());

	public ProfileCanvas(String dateInfo, int id, int width, int height) {
		this.dateInfo = dateInfo;
		init(id);
		rescaleCanvas(new Dimension(width, height));
	}

	public ProfileCanvas(int id) {
		init(id);
		// [2500stocks][10days][7indicator]
		// for each weeks data go through all 10 days for this stock add all
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

	}

	private void init(int id) {
		String ticker = Database.dbSet.get(id);
		technicals = Database.TECHNICAL_PRICE_DATA.get(ticker);
		addMouseListener(dateDisplayer);
		tickerID = id;
		setTextDescriptionInArray();
		
		DataPointGraphic prices = new DataPointGraphic("", ticker, 4);
		charts.add(prices);
		for (String s : DBLabels.priorityLabeling) {
			if (s.equals(""))
				continue;

			charts.add(new DataPointGraphic(s, ticker));
		}

		rescaleCanvas(new Dimension(W, H));
	}
	public void rescaleCanvas(Dimension dim) {
		W = dim.width;
		rescaleAllDataPointGraphics();
		 setPreferredSize(new Dimension(W, H));
		setDateLines(dateInfo);
	}

	public void setDateInfo(String di) {
		dateInfo = di;
		setDateLines(dateInfo);
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

		}
	}

	private int convertIndexToScreenPoint(int index) {
		int screenPoint = (int) (index * generalInterval + DataPointGraphic.PIXELS_BORDER);
		return screenPoint;
	}

	private void rescaleAllDataPointGraphics() {
		for (DataPointGraphic gr : charts) {
			gr.rescale();
		}
	}

	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, W, H);
		for (DataPointGraphic dg : charts) {
			dg.drawMe(g);
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
