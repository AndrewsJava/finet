package harlequinmettle.financialsnet;

import harlequinmettle.financialsnet.interfaces.DBLabels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
 

public class DataControlls extends JFrame implements ActionListener,
		ChangeListener {
	public static final int FIELD_COUNT = DBLabels.labels.length;
	// DUPLICATE FOR SUPLEMENTALS DB_SUP - DBLabels.suplementals.length
	public static final JCheckBox[] INCLUDES = new JCheckBox[FIELD_COUNT];
	public static final JCheckBox[] INVERT = new JCheckBox[FIELD_COUNT];
	public static final JButton[] OPEN_STATS = new JButton[FIELD_COUNT];
	public static final JTextField[] LOWS = new JTextField[FIELD_COUNT];
	public static final JTextField[] HIGHS = new JTextField[FIELD_COUNT];
	public static final JButton[] OPEN_RESULTS = new JButton[FIELD_COUNT];
	public static final String PANEL_BUTTON = "show bundles";
	public static Database DB;
	public static ArrayList<StatInfo> statistics = new ArrayList<StatInfo>();
	ProfileViewer pv;
	public static Font mFont = new Font(Font.SANS_SERIF, Font.PLAIN, 20);

	public DataControlls() {
		doSetup();
		new SystemMemoryUsage();
		DB = new Database();
		for (int i = 0; i < FIELD_COUNT; i++) {
			statistics.add(generateStatistics(i));
			LOWS[i].setText("" + statistics.get(i).min);
			HIGHS[i].setText("" + statistics.get(i).max);
		}
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

	public static StatInfo generateBundleStatistics(int id, float emn, float emx) {

		ArrayList<Float> stats = new ArrayList<Float>();
		if (Database.BUNDLES.size() < Database.DB_ARRAY.size())
			return new StatInfo(stats, id, StatInfo.DONT_SHOW);
		// TreeMap<Float, ArrayList<Integer>> BUNDLES

		for (Entry<Float, float[][]> ent : Database.DB_ARRAY.entrySet()) {
			float[][] dats = ent.getValue();
			for (int bun : Database.BUNDLES.get(ent.getKey())) {
				stats.add(dats[bun][id]);
			}
		}
		return new StatInfo(stats, id, StatInfo.DONT_SHOW, emn, emx);
	}

	public static void setMarketComparisonStatistics(StatInfo setTo, int iD,
			float emin, float emax) {

		ArrayList<Float> over = new ArrayList<Float>();
		ArrayList<Float> under = new ArrayList<Float>();

		for (Entry<Float, float[][]> ent : Database.DB_ARRAY.entrySet()) {
			// change in market forward 1wk
			if (!Database.MARKETCHANGE.containsKey(ent.getKey()))
				continue;
			float marketChange = Database.MARKETCHANGE.get(ent.getKey());

			System.out.println("MARKET WAS " + marketChange);
			float[][] dats = ent.getValue();
			float[] individualsChanges = Database.UNFORESEEN.get(ent.getKey());
			if (individualsChanges != null)
				for (int i = 0; i < dats.length; i++) {
					if (individualsChanges[i] != individualsChanges[i])
						continue;
					if (individualsChanges[i] > marketChange)
						over.add(dats[i][iD]);
					else if (individualsChanges[i] < marketChange)
						under.add(dats[i][iD]);
				}
		}
		System.out.println("sizes:  -------->" + over.size() + "   "
				+ under.size());
		// overs - left = false -> right
		setTo.beatMarket = new StatInfo(over, iD, StatInfo.DONT_SHOW, emin,
				emax, false);

		// unders- left = true
		setTo.marketBeat = new StatInfo(under, iD, StatInfo.DONT_SHOW, emin,
				emax, true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		DataControlls dc = new DataControlls();
	}

	public void doSetup() {
		this.setSize(990, 700);

		this.setLayout(new BorderLayout());
		// NORTH panel
		JPanel contain2 = new JPanel();
		JCheckBox nyse = new JCheckBox("nyse", true);
		JCheckBox nasdaq = new JCheckBox("nasdaq", true);
		nyse.addChangeListener(this);
		nasdaq.addChangeListener(this);// doesn't do anything yet
		JButton load = new JButton(PANEL_BUTTON);
		contain2.add(nyse);
		contain2.add(nasdaq);
		contain2.add(load);
		load.addActionListener(this);
		// CENTER scrolling panel
		JPanel container = new JPanel();
		// rows,columns - unbound rows
		container.setLayout(new GridLayout(0, 1));

		for (int i = 0; i < FIELD_COUNT; i++) {

			JPanel subcontainer = new JPanel();
			INVERT[i] = new JCheckBox();
			subcontainer.add(INVERT[i]);
			INCLUDES[i] = new JCheckBox();
			subcontainer.add(INCLUDES[i]);
			OPEN_STATS[i] = new JButton(DBLabels.labels[i]);
			OPEN_STATS[i].setPreferredSize(new Dimension(240, 50));
			OPEN_STATS[i].addActionListener(this);
			subcontainer.add(OPEN_STATS[i]);
			LOWS[i] = new JTextField("MIN " + i * 1000);
			HIGHS[i] = new JTextField("MAX " + i * 1000);
			LOWS[i].setPreferredSize(new Dimension(240, 50));
			HIGHS[i].setPreferredSize(new Dimension(240, 50));
			LOWS[i].setFont(mFont);
			HIGHS[i].setFont(mFont);
			subcontainer.add(LOWS[i]);
			subcontainer.add(HIGHS[i]);

			container.add(subcontainer);
			OPEN_STATS[i].repaint();
			LOWS[i].repaint();
			HIGHS[i].repaint();
		}
		JScrollPane jsp = new JScrollPane(container);
		jsp.getVerticalScrollBar().setUnitIncrement(32);
		// jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(contain2, BorderLayout.NORTH);
		this.add(jsp, BorderLayout.CENTER);

		this.setVisible(true);
	}

	class loadDB implements Runnable {

		@Override
		public void run() {

			DB = new Database();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String buttonTitle = e.getActionCommand();
		JButton button = (JButton) e.getSource();
		int id = e.getID();// event type
		DataControlls.applyLimtsToDatabase();
		if (buttonTitle.equals(PANEL_BUTTON)) {
			if (pv == null) {
				pv = new ProfileViewer();
			} else {
				pv.intitializeUI();
				pv.configureUI();

			}
		}
		for (int i = 0; i < DBLabels.labels.length; i++) {
			if (OPEN_STATS[i] == button) {
				INCLUDES[i].setSelected(true);
				statistics.get(i).addMarketGraph(Database.MARKET);
				statistics.get(i).addPercentagesGraph(Database.MARKETCHANGE);
				statistics.get(i).showIt();
			}
		}

	}

	public static void applyLimtsToDatabase() {
		Database.LIMITS.clear();
		for (int i = 0; i < DataControlls.FIELD_COUNT; i++) {
			if (DataControlls.INCLUDES[i].isSelected()) {
				float lowlimit = Float.parseFloat((DataControlls.LOWS[i]
						.getText()));
				float highlimit = Float.parseFloat((DataControlls.HIGHS[i]
						.getText()));
				Database.LIMITS.put(i, new Point2D.Float(lowlimit, highlimit));
			}
		}
		System.out.println("LIMITS ARE SET ------>");
		for (Entry<Integer, Point2D.Float> entr : Database.LIMITS.entrySet()) {
			System.out.println(entr.getKey() + "------>" + entr.getValue());
		}

		Database.computeLimitChanges();
		for (int i = 0; i < statistics.size(); i++) {
			if (statistics.get(i).isVisible()) {
				statistics.get(i).addBundleSizes(Database.BUNDLES_SIZES);
				statistics.get(i).addBundlesBuySell(Database.SET_CHANGES);
				statistics.get(i).addPercentagesGraph(Database.BUNDLES_CHANGES);

			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

}
