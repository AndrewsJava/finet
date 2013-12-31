package harlequinmettle.financialsnet;
 

import harlequinmettle.financialsnet.interfaces.DBLabels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ProfileViewer implements ActionListener {

	static JFrame frame;
	static JTabbedPane tabbedPane;
	// static JPanel share = new JPanel();
	static JSplitPane share;

	static JTextArea jta;
	static JScrollPane textScroll;

	static JPanel buttons;
	static JScrollPane buttonScroll;

	static {

		intitializeUI();

	}

	public void configureUI() {
		ArrayList<Integer> previousBundle = null;
		ArrayList<Integer> nextBundle = null;
		boolean first = true;
		Float[] keys = Database.BUNDLES.keySet().toArray(
				new Float[Database.BUNDLES.size()]);
		int maxBundleSize = 0;
		int i = 0;
		for (ArrayList<Integer> bundle : Database.BUNDLES.values()) {
			JPanel bundles = new JPanel();
			bundles.setLayout(new GridLayout(0, 1));
			if (i > 0)
				previousBundle = Database.BUNDLES.get(keys[i - 1]);
			if (i + 1 < keys.length - 1)
				nextBundle = Database.BUNDLES.get(keys[i + 1]);

			if (first) {
				first = false;
			}
			bundles.add(new JButton("" + bundle.size()));
			for (int id : bundle) {
				JButton tickerButton = new JButton(Database.dbSet.get(id) + " "
						+ id);
				tickerButton.addActionListener(this);

				bundles.add(tickerButton);
				if (previousBundle == null || !previousBundle.contains(id))
					tickerButton.setBackground(Color.green);
				if (!nextBundle.contains(id))
					tickerButton.setBackground(Color.red);
			}
			if (bundle.size() > maxBundleSize)
				maxBundleSize = bundle.size();
			// JScrollPane scroller = new JScrollPane(bundles);
			buttons.add(bundles);
			i++;
		}

		buttons.setSize(100 * keys.length, 20 * maxBundleSize);
		// frame.repaint();
		frame.setVisible(true);
	}

	public ProfileViewer() {
		configureUI();
	}

	public static void intitializeUI() {

		frame = new JFrame();
		tabbedPane = new JTabbedPane();
		// static JPanel share = new JPanel();
		share = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		jta = new JTextArea("some text for text area");
		textScroll = new JScrollPane();

		buttons = new JPanel();
		buttonScroll = new JScrollPane();

		frame.setSize(900, 680);
		frame.getContentPane().add(tabbedPane);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		tabbedPane.add("Bundles", share);
		// tabbedPane.add(canvasViewer);
		// jta.setSize(400, 100);
		// textScroll.setSize(900, 200);

		// JScrollPane scrollPanel = new JScrollPane();
		// scrollPanel.setPreferredSize();

		jta.setFont(DataControlls.mFont);
		jta.setLineWrap(true);
		// textScroll.add(jta);
		textScroll.setViewportView(jta);
		textScroll.setPreferredSize(new Dimension(900, 200));
		textScroll.getVerticalScrollBar().setUnitIncrement(32);
		// share.add(textScroll );
		share.add(textScroll, JSplitPane.BOTTOM);
		share.setDividerLocation(450);

		buttons.setLayout(new GridLayout(1, 0));
		// buttons.setSize(900,600);
		buttonScroll.setViewportView(buttons);
		buttonScroll.setPreferredSize(new Dimension(900, 600));
		// buttonScroll.add(buttons);
		buttonScroll.getVerticalScrollBar().setUnitIncrement(32);
		// share.add(buttonScroll );
		share.add(buttonScroll, JSplitPane.TOP);
		// canvasViewer.add(companyOverview);

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String ticker = arg0.getActionCommand().split(" ")[0];
		int id = Integer.parseInt(arg0.getActionCommand().split(" ")[1]);
		StringBuilder profile = new StringBuilder(ticker + "  ---->"
				+ Database.DESCRIPTIONS.get(ticker).replaceAll("_", " "));
		appendProfile(profile);
		jta.setText(profile.toString());

		JScrollPane canvasScroll = new JScrollPane();

		canvasScroll.getVerticalScrollBar().setUnitIncrement(32);

		ProfileCanvas companyOverview = new ProfileCanvas(id);
		 canvasScroll.setViewportView(companyOverview);

		tabbedPane.add(ticker, canvasScroll);
		// companyOverview.repaint();
	}

	private void appendProfile(StringBuilder profile) {

		for (int i = 0; i < DBLabels.labels.length; i++) {
			int textSize = DBLabels.labels[i].length();
		}

	}

}
