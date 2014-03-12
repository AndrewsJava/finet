package harlequinmettle.financialsnet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;

import ffttest.FFT;

public class JComponentFactory {

	public static final int HORIZONTAL = 8000000;
	public static final int VERTICAL = 1111111;
	public static final int GENERAL_LAYOUT = 121212121;

	public static final Border BLACKBORDER = BorderFactory
			.createLineBorder(Color.black);
	public static final Border CUSTOMBORDER = BorderFactory
			.createLineBorder(new Color(100, 220, 245));

	public static JScrollPane makeTextScroll(JTextArea jta) {
		JScrollPane textScroll = new JScrollPane();
		Font mFont = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
		jta.setFont(mFont);
		jta.setLineWrap(false);
		textScroll.setViewportView(jta);
		textScroll.setPreferredSize(new Dimension(600, 300));
		textScroll.getVerticalScrollBar().setUnitIncrement(32);
		return textScroll;
	}

	public static JScrollPane makeJScrollPane(JComponent jComp) {
		JScrollPane scroller = new JScrollPane();
		scroller.setViewportView(jComp);
		scroller.setPreferredSize(new Dimension(600, 300));
		scroller.getVerticalScrollBar().setUnitIncrement(32);
		return scroller;
	}

	public static JCheckBox doJCheckbox(String title) {

		final JCheckBox a = new JCheckBox(title);

		a.setSelected(false);
		a.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				boolean cbStatus = (arg0.getStateChange() == ItemEvent.SELECTED);

			}

		});
		return a;

	}

	public static JScrollPanelledPane doHtmlTickerFilesTab() {
		JScrollPanelledPane stepScroll = new JScrollPanelledPane();

		File dir = new File(EarningsTest.REPORTS_ROOT);
		File[] files = dir.listFiles();
		Arrays.sort(files);
		for (int i = files.length - 1; i >= 0; i--) {
			File f = files[i];
			CustomButton eb = JComponentFactory.makeHtmlLoadButton(f.getName());

			EarningsTest.MAP_TO_FILES.put(f.getName(), f);

			stepScroll.addComp(eb);

		}
		return stepScroll;

	}

	public static JPanel generatePanel(JComponent... comps) {
		JPanel shell = JComponentFactory
				.makePanel(JComponentFactory.HORIZONTAL);
		// if (!Cuker.programSettings.useSystemLookAndFeel)
		shell.setBorder(BLACKBORDER);
		for (JComponent a : comps)
			shell.add(a);

		return shell;
	}

	public static JPanel makePanel(int orientation) {
		JPanel p = new JPanel();

		switch (orientation) {
		case HORIZONTAL:
			p.setLayout(new GridLayout(1, 0));
			break;
		case VERTICAL:
			p.setLayout(new GridLayout(0, 1));
			break;
		default:
			break;
		}
		return p;
	}

	public static CustomButton makeLoadDatabaseButton(String buttonTitle,
			JComponent param) {
		final CustomButton a = new CustomButton(buttonTitle);

		a.addActionListener(JComponentFactory.makeDBLoadButtonListener(
				buttonTitle, (JRadioButton) param));

		return a;
	}

	public static String extractDate(String fullTitle) {
		if (!fullTitle.contains("_URL_"))
			return "incompatible text";
		String dateTitle = fullTitle.replaceAll(".html", "").split("_URL_")[1];
		return dateTitle;
	}

	public static String reformatTitle(String fullTitle) {
		if (!fullTitle.contains("_URL_"))
			return "incompatible text";
		String dateTitle = fullTitle.replaceAll(".html", "").replaceAll(
				"_URL_", " ");
		return dateTitle;
	}

	public static CustomButton makeHtmlLoadButton(final String buttonTitle) {

		final CustomButton a = new CustomButton(reformatTitle(buttonTitle));
		a.setHorizontalAlignment(SwingConstants.LEFT);
		colorButton(a); 
		addButtonChoosePanelBuilderListener(a); 

		renameButton(a);
		return a;
	}

	private static void addButtonChoosePanelBuilderListener(final CustomButton a) {

		a.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!Database.loaded)
					return;
				final JFrame jf = new JFrame(a.getText());
				jf.setSize(1300, 650);
				jf.setVisible(true);
				jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JScrollPanelledPane stepScroll = new JScrollPanelledPane();
				jf.add(stepScroll);
				ArrayList<String> tickers = parseFileForTickers(EarningsTest.MAP_TO_FILES
						.get(redoFileFormat(a.getText())));
				saveTickerCount(a.getText(), tickers.size());
				ArrayList<String> actual = new ArrayList<String>();
				for (String s : tickers) {
					int tickerLocation = Database.dbSet.indexOf(s);
					if (tickerLocation > 0) {
						actual.add(s);
						CustomButton tickerButton = JComponentFactory
								.doIndividualTickerButtonForPanel(s,
										a.getText(), jf);
						// tickerButton.setBackground(new Color(100,140,255));
						tickerButton.setMinimumSize(new Dimension(300, 45));
						stepScroll.addComp((tickerButton));
					}
				}
			}
			// saveUseableTicker(a.getText(), actual);

		});
	}

	private static CustomButton doIndividualTickerButton(final String s,
			final String buttonData) {
		final CustomButton a = new CustomButton(s);
		a.setPreferredSize(new Dimension(60, 20));
		final int tickerLocation = Database.dbSet.indexOf(s);
		double marketCap = Database.DB_ARRAY.lastEntry().getValue()[tickerLocation][38];
		addButtonDetails(a, marketCap, tickerLocation, buttonData);
		a.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFrame jf = new JFrame(a.getText());
				jf.setSize(1300, 650);
				jf.setVisible(true);
				ProfileCanvas pc = new ProfileCanvas(reformatTitle(buttonData),
						tickerLocation, jf.getWidth(), jf.getHeight());

				jf.addComponentListener(JComponentFactory
						.doWindowRescaleListener(pc));
				jf.add(JComponentFactory.makeJScrollPane(pc));
			}

		});
		return a;
	}

	private static CustomButton doIndividualTickerButtonForPanel(
			final String s, final String buttonData, final JFrame closeMe) {
		final CustomButton a = new CustomButton((s));
		a.setPreferredSize(new Dimension(60, 20));
		final int tickerLocation = Database.dbSet.indexOf(s);
		double marketCap = Database.DB_ARRAY.lastEntry().getValue()[tickerLocation][38];
		addButtonDetails(a, marketCap, tickerLocation, buttonData);

		colorButtonByPercentChangeAfterEarningsReport(a, buttonData, a
				.getText().split(" ")[0]);

		a.setHorizontalAlignment(SwingConstants.LEFT);
		a.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				final JFrame jf = new JFrame(a.getText());
				jf.setSize(1300, 650);
				jf.setExtendedState(jf.getExtendedState()
						| JFrame.MAXIMIZED_BOTH);
				jf.setVisible(true);
				// closeMe.dispose();
				FFT fft = new FFT(Database.spawnTimeSeriesForFFT(s));
				fft.showFrequencyGraph();
				ProfileCanvas pc = new ProfileCanvas((buttonData),
						tickerLocation, jf.getWidth(), jf.getHeight());

				jf.addComponentListener(JComponentFactory
						.doWindowRescaleListener(pc));
				jf.add(JComponentFactory.makeJScrollPane(pc));
			}

		});
		return a;
	}

	private static void addButtonDetails(CustomButton a, double marketCap,
			int tLoc, String buttonData) {
		String title = a.getText();
		if (title.length() < 2)
			title += "  ";
		if (title.length() < 3)
			title += " ";
		for (int i = 10; i > title.length(); i--) {
			title += " ";
		}
		String cap = (int) (marketCap / 1000000) + " M";

		for (int i = 8; i > cap.length(); i--) {
			title += " ";
		}
		title += cap;
		String t = Database.DESCRIPTIONS.get(Database.dbSet.get(tLoc))
				.replaceAll("_", " ");
		int rankAverage = (int) (1000 * ProfileCanvas
				.calculateWordRankAverage(t));
		if (rankAverage < 100) {
			title += "     L0";
			if (rankAverage < 10)
				title += "0";
		} else {
			title += "     L";
		}
		title += rankAverage + "T";

		for (int i = 30; i > title.length(); i--) {
			title += " ";
		}
		title += "|";
		TreeMap<Float, float[]> prices = Database.TECHNICAL_PRICE_DATA
				.get(title.split(" ")[0]);
		ArrayList<Float> priceChanges = new ArrayList<Float>();
		float[] initial = prices.firstEntry().getValue();
		boolean first = true;
		for (Entry<Float, float[]> ent : prices.entrySet()) {
			if (first) {
				first = false;
				continue;
			}
			float[] ending = ent.getValue();
			float change = (ending[6] - initial[6]) / initial[6];
			priceChanges.add(change);
		}
		StatInfo stat = new StatInfo(priceChanges, false);
		// float stdv = DataPointGraphic.roundTo(stat.standardDeviation, 3);
		int stdv = (int) (1000 * stat.standardDeviation);
		// colorButtonBasedOnStandardDeviation(a, stdv);
		if (stdv >= 1000)
			stdv = 999;
		title += "   " + stdv;
		if (stdv < 100)
			title += " ";
		if (stdv < 10)
			title += " ";
		title += " StdDev %d   |   ";
		if (!Database.INDIVIDUAL_OVERALL_CHANGES.containsKey(a.getText().split(
				" ")[0]))
			return;
		int overallChange = (int) (float) Database.INDIVIDUAL_OVERALL_CHANGES
				.get(//
				a.getText()//
						.split(" ")//
				[0]);
		// colorButtonBasedOnOverallChange(a,overallChange);
		title += overallChange;
		if (overallChange < 100 && overallChange > -9)
			title += " ";
		if (overallChange < 10 && overallChange > 0)
			title += " ";
		title += "%   |   ";
		a.setText(title);
	}

	private static void colorButtonByPercentChangeAfterEarningsReport(
			CustomButton a, String dateInfo, String ticker) {

		String[] dates = dateInfo.split(" ");
		if (dates.length < 2) {
			System.out.println("\n\nARRAY IS SHORT : " + dateInfo + "  -->  "
					+ Arrays.toString(dates));
			return;
		}

		try {
			long earningsReportDate = EarningsTest.singleton.dateFormatForFile
					.parse(dates[1]).getTime() / 1000 / 3600 / 24;
			// int collectionDate = (int) Double.parseDouble(dates[0]);
			// TODO: COMPARE TO MARKET NOT ABSOLUTE
			int daysBefore = 10;
			int daysAfter = 2;
			float beforeReport = earningsReportDate - daysBefore;
			float afterReport = earningsReportDate + daysAfter;
			float marketFactor = Database.calculateMarketChange(beforeReport,
					afterReport);
			float change = Database.calculatePercentChange(
					Database.dbSet.indexOf(ticker), beforeReport, afterReport);
			System.out.println("market calc: " + marketFactor);
			System.out.println("change calc: " + change);
			change = change - marketFactor;
			int red = (int) (120 + change * 1);
			int green = (int) (140 + change * 1);
			int blue = (int) (140 + change * 3);
			if (red > 254)
				red = 255;
			if (green > 254)
				green = 255;
			if (blue > 254)
				blue = 255;
			if (red < 1)
				red = 1;
			if (green < 1)
				green = 1;
			if (blue < 1)
				blue = 1;
			a.setBackground(new Color(red, green, blue));
			String rename = a.getText() + change + " % v mkt -" + daysBefore
					+ ", +" + daysAfter + " days ";
			a.setText(rename);
		} catch (Exception e) {
		}
	}

	private static void colorButtonBasedOnOverallChange(CustomButton a,
			int overallChange) {
		System.out.println("overall market: " + Database.overallMarketChange);
		System.out.println("median :            "
				+ Database.changesStats.median);

	}

	private static void colorButtonBasedOnStandardDeviation(CustomButton a,
			int stdv) {
		// TODO Auto-generated method stub

	}

	private static void addTabBuildingListener(final CustomButton a) {

		a.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!Database.loaded)
					return;
				final JFrame jf = new JFrame(a.getText());
				jf.setSize(1300, 650);
				final ArrayList<ProfileCanvas> theCanvasesToRescale = new ArrayList<ProfileCanvas>();
				jf.addComponentListener(JComponentFactory
						.doWindowRescaleListener(theCanvasesToRescale));
				jf.setVisible(true);
				jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JTabbedPane compareTickers = new JTabbedPane();
				jf.add(compareTickers);
				ArrayList<String> tickers = parseFileForTickers(EarningsTest.MAP_TO_FILES
						.get(redoFileFormat(a.getText())));
				saveTickerCount(a.getText(), tickers.size());
				ArrayList<String> actual = new ArrayList<String>();
				for (String s : tickers) {
					int tickerLocation = Database.dbSet.indexOf(s);
					if (tickerLocation > 0) {
						actual.add(s);
						ProfileCanvas pc = new ProfileCanvas(a.getText(),
								tickerLocation, jf.getWidth(), jf.getHeight());
						// ProfileCanvas pc = new ProfileCanvas(
						// tickerLocation);
						theCanvasesToRescale.add(pc);
						compareTickers.add(s,
								JComponentFactory.makeJScrollPane(pc));
					}
				}
				saveUseableTicker(a.getText(), actual);
				renameButton(a);
			}

			private void saveUseableTicker(String buttonTitle,
					ArrayList<String> tickers) {
				EarningsTest.singleton.programSettings.tickersPerFileInDatabase
						.put(buttonTitle, tickers.size());
				EarningsTest.singleton.programSettings.tickersActual.put(
						buttonTitle, tickers);
				isSaveMax(tickers.size());
				MemoryManager.saveSettings();
			}

			private void isSaveMax(int size) {
				if (size > EarningsTest.singleton.programSettings.maxSize)
					EarningsTest.singleton.programSettings.maxSize = size;
			}

			private void saveTickerCount(String buttonTitle, int size) {
				EarningsTest.singleton.programSettings.tickersPerFile.put(
						buttonTitle, size);

				MemoryManager.saveSettings();
			}
		}

		);
	}

	private void isSaveMax(int size) {
		if (size > EarningsTest.singleton.programSettings.maxSize)
			EarningsTest.singleton.programSettings.maxSize = size;
	}

	private static void saveTickerCount(String buttonTitle, int size) {
		EarningsTest.singleton.programSettings.tickersPerFile.put(buttonTitle,
				size);

		MemoryManager.saveSettings();
	}

	protected static String redoFileFormat(String text) {
		String[] dates = text.split(" ");
		if (dates.length < 2)
			return "invalid format";
		else
			return dates[0] + "_URL_" + dates[1] + ".html";
	}

	private static void colorButton(CustomButton a) {

		double fileDate = 0;
		double earningsDate = 0;
		if (!a.getText().contains("_"))
			return;
		try {
			fileDate = Double.parseDouble(a.getText().split(" ")[0]);
			earningsDate = EarningsTest.singleton.dateFormatForFile.parse(
					a.getText().split(" ")[1]).getTime()
					/ (24 * 3600 * 1000);
		} catch (Exception e) {
			return;
		}
		int color = 120;
		if (earningsDate < EarningsTest.dayNumber()) {
			a.setBackground(new Color(color, color, color));
		} else {
			color = 150;
			double today = EarningsTest.dayNumber();
			while (today < earningsDate) {

				color += 15;
				if (color > 255)
					color = 255;
				today += 2;
			}
			a.setBackground(new Color(100, 100, color));
		}
	}

	private static void renameButton(CustomButton a) {
		String newName = a.getText();
		if (getCountAll(a.getText()) != null)
			newName += "     (" + getCountAll(a.getText()) + ")";
		if (getCountMatch(a.getText()) != null)
			newName += "     (" + getCountMatch(a.getText()) + ")";

		a.setText(newName);
	}

	private static ArrayList<String> getTickersFound(String text) {
		return EarningsTest.singleton.programSettings.tickersActual.get(text);
	}

	private static Integer getCountMatch(String text) {
		return EarningsTest.singleton.programSettings.tickersPerFileInDatabase
				.get(text);
	}

	private static Integer getCountAll(String buttonTitle) {
		return EarningsTest.singleton.programSettings.tickersPerFile
				.get(buttonTitle);
	}

	protected static ComponentAdapter doWindowRescaleListener(
			final ArrayList<ProfileCanvas> theCanvasesToRescale) {
		return new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent arg0) {
				for (ProfileCanvas pc : theCanvasesToRescale)
					pc.rescaleCanvas(arg0.getComponent().getBounds().getSize());

			}

		};
	}

	protected static ComponentAdapter doWindowRescaleListener(
			final ProfileCanvas pc) {
		return new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent arg0) {

				pc.rescaleCanvas(arg0.getComponent().getBounds().getSize());

			}
		};
	}

	private static ArrayList<String> parseFileForTickers(File htmlFile) {

		ArrayList<String> tickers = new ArrayList<String>();
		if (htmlFile == null)
			return tickers;
		String[] getTickersFrom = { "   " };
		try {
			String fromFile = FileUtils.readFileToString(htmlFile);
			getTickersFrom = fromFile.replaceAll("\\s+", "").split("q\\?s=");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 1; i < getTickersFrom.length; i++) {
			String s = getTickersFrom[i];
			try {
				if (s.indexOf(">") > 0 && s.indexOf("<") > 0) {
					String ticker = s.substring(s.indexOf(">") + 1,
							s.indexOf("<"));
					tickers.add(ticker);
					// System.out.print(ticker + "    ");
				}
			} catch (Exception e) {
				// System.err.println("error: "+s);
			}
		}
		return tickers;
	}

	private static ActionListener makeDBLoadButtonListener(
			final String buttonTitle, final JRadioButton yes) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (yes.isSelected())
					startLoadingDataBase();
				else
					return;
				yes.setSelected(false);
				yes.setEnabled(false);
				addReportsTab();
			}
		};
	}

	public static void addReportsTab() {

		JScrollPanelledPane filesTab = JComponentFactory.doHtmlTickerFilesTab();

		EarningsTest.singleton.gui.add("Earnings Reports", filesTab);
	}

	public static void startLoadingDataBase() {
		Thread loadDB = new Thread(new Runnable() {

			@Override
			public void run() {

				if (!Database.loaded)
					EarningsTest.db = new Database(
							EarningsTest.PATH_SOURCE.getText());

				JComponentFactory.addReportsTab();
			}

		});
		loadDB.start();
	}

	public static JLabel doJLabel(String string) {
		JLabel general = new JLabel(string);
		general.setOpaque(true);
		general.setBackground(new Color(100, 200, 250));
		return general;
	}

	public static JTextArea doJTextArea() {
		JTextArea jta = new JTextArea();

		jta.setBackground(new Color(165, 225, 210));
		return jta;
	}

	public static CustomButton doPathBrowseButton(final JTextArea textToUpdate) {
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final CustomButton a = new CustomButton("browse");
		a.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				// In response to a button click:
				int returnVal = fc.showOpenDialog(a);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					String path = file.getAbsolutePath();

					textToUpdate.append(path);

				}

			}

		});
		return a;
	}

	public static CustomButton doDescriptionSearchButton(
			final JTextArea searchWords) {
		final CustomButton a = new CustomButton("search");
		final ArrayList<String> tickers = new ArrayList<String>();
		a.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame searchResultFrame = new JFrame("Search Results: "+searchWords.getText());
				searchResultFrame.setSize(1300, 650);
				searchResultFrame.setVisible(true);
				searchResultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JScrollPanelledPane tickerScroll = new JScrollPanelledPane();
				searchResultFrame.add(tickerScroll);
				String reformated = format(searchWords.getText().toLowerCase());
				String[] searchParams = reformated
						.split(" ");
				for (Entry<String, String> ent : Database.DESCRIPTIONS
						.entrySet()) {
					String ticker = ent.getKey();
					String description = ent.getValue().toLowerCase();

					for (String searcher : searchParams) {
						if (description.contains(searcher)) {
							CustomButton tickerButton = JComponentFactory
									.doIndividualTickerButtonForPanel(ticker,
											a.getText(),searchResultFrame);
							// tickerButton.setBackground(new Color(100,140,255));
							tickerButton.setMinimumSize(new Dimension(300, 45));
							tickerScroll.addComp((tickerButton)); 
						}
					}
				}

			}

			private String format(String lowerCase) {
			while(lowerCase.contains("  ")){
				lowerCase = lowerCase.replaceAll("  ", " ");
			}
				return lowerCase.trim();
			}

		}); 
		return a;
	}
 
	public static CustomButton doFileMoveButton(final File dls) {
		final String title = dls.getName();
		final CustomButton a = new CustomButton(title);
		a.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String root = EarningsTest.PATH_SOURCE.getText();
				if (title.contains("BIG_nas")) {
					String newLocation = root + File.separator + "lg"
							+ File.separator + "q";

					try {
						FileUtils.moveFileToDirectory(dls,
								new File(newLocation), true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (title.contains("nas")) {

					String newLocation = root + File.separator + "sm"
							+ File.separator + "q";
					try {
						FileUtils.moveFileToDirectory(dls,
								new File(newLocation), true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (title.contains("BIG_ny")) {

					String newLocation = root + File.separator + "lg"
							+ File.separator + "y";
					try {
						FileUtils.moveFileToDirectory(dls,
								new File(newLocation), true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (title.contains("ny")) {
					String newLocation = root + File.separator + "sm"
							+ File.separator + "y";
					try {
						FileUtils.moveFileToDirectory(dls,
								new File(newLocation), true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}

		});
		return a;
	}

	public static CustomButton doConfirmMoveAllFiles() {
		// TODO: ADD VALIDATION BASED ON EXISTING DATE NUMBERS AND FILE SIZE
		final CustomButton a = new CustomButton(
				"MOVE ALL OF THESE FILES TO DATABASE");
		a.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				File[] downloads = new File(EarningsTest.PATH_DOWNLOADS
						.getText()).listFiles();
				Arrays.sort(downloads, NameFileComparator.NAME_COMPARATOR);
				for (File dls : downloads) {
					if (dls.getName().contains("BIG_n")
							|| dls.getName().contains("nas_")
							|| dls.getName().contains("ny_")) {
						final String title = dls.getName();
						String root = EarningsTest.PATH_SOURCE.getText();
						if (title.contains("BIG_nas")) {
							String newLocation = root + File.separator + "lg"
									+ File.separator + "q";

							try {
								FileUtils.moveFileToDirectory(dls, new File(
										newLocation), true);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else if (title.contains("nas")) {

							String newLocation = root + File.separator + "sm"
									+ File.separator + "q";
							try {
								FileUtils.moveFileToDirectory(dls, new File(
										newLocation), true);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if (title.contains("BIG_ny")) {

							String newLocation = root + File.separator + "lg"
									+ File.separator + "y";
							try {
								FileUtils.moveFileToDirectory(dls, new File(
										newLocation), true);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else if (title.contains("ny")) {
							String newLocation = root + File.separator + "sm"
									+ File.separator + "y";
							try {
								FileUtils.moveFileToDirectory(dls, new File(
										newLocation), true);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}

		});
		return a;
	}

	public static CustomButton makeTextExplorerLauchButton(
			final String buttonTitle) {
		final CustomButton a = new CustomButton(buttonTitle);

		a.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!Database.loaded)
					return;
				JFrame jf = new JFrame(buttonTitle);
				jf.setSize(900, 500);
				jf.setVisible(true);
				jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JTextArea wordStat = new JTextArea();
				JScrollPane compareTickers = JComponentFactory
						.makeTextScroll(wordStat);
				jf.add(compareTickers);

				TreeMap<Integer, ArrayList<String>> orderResults = new TreeMap<Integer, ArrayList<String>>();

				for (Entry<String, Integer> ent : Database.WORD_STATS
						.entrySet()) {
					String word = ent.getKey();
					Integer count = ent.getValue();
					if (orderResults.containsKey(count)) {
						orderResults.get(count).add(word);
					} else {
						ArrayList<String> startArray = new ArrayList<String>();
						startArray.add(word);
						orderResults.put(count, startArray);
					}
				}
				for (Entry<Integer, ArrayList<String>> ent : orderResults
						.entrySet()) {
					wordStat.append("\n\n" + ent.getKey() + "\n"
							+ ent.getValue());

				}
				ArrayList<Float> avgs = new ArrayList<Float>();
				ArrayList<Float> totals = new ArrayList<Float>();
				for (String txt : Database.DESCRIPTIONS.values()) {
					txt = txt.replaceAll("_", " ");
					totals.add(calculateWordRankTotal(txt));
					avgs.add(calculateWordRankAverage(txt));
				}
				StatInfo totalStats = new StatInfo(totals);
				StatInfo avgsStats = new StatInfo(avgs);
			}

			private float calculateWordRankTotal(String text) {
				float rank = 0;
				String[] words = Database.simplifyText(text).split(" ");
				for (String word : words) {

					rank += 1.0 / Database.WORD_STATS.get(word);
				}
				return rank;
			}

			private float calculateWordRankAverage(String text) {
				float rank = 0;
				String[] words = Database.simplifyText(text).split(" ");
				for (String word : words) {

					rank += 1.0 / Database.WORD_STATS.get(word);
				}
				return rank / words.length;
			}
		}

		);

		return a;
	}

}
