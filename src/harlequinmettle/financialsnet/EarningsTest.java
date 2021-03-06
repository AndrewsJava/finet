package harlequinmettle.financialsnet;

import harlequinmettle.financialsnet.interfaces.DBLabels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;

public class EarningsTest {
	static ProgramSettings programSettings;
	static Database db;
	final String qTickerDownloadSite = "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nasdaq&render=download";
	final String yTickerDownloadSite = "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nyse&render=download";
	final Calendar calendar = Calendar.getInstance();
	final TreeMap<String, String> rawMap = new TreeMap<String, String>();
	final TreeMap<String, String> buttonNamingWorkaround = new TreeMap<String, String>();
	final SimpleDateFormat dateFormatForFile = new SimpleDateFormat(
			"yyyy_MM_dd");
	final SimpleDateFormat dateFormatForUrl = new SimpleDateFormat("yyyyMMdd");
	final ArrayList<File> rawHtmlData = new ArrayList<File>();
	final String[] dow = { "DJI" };
	static final String REPORTS_ROOT = "earningsReportsFiles" + File.separator;
	final JFrame application = new JFrame("Financails Net");
	final JTabbedPane gui = new JTabbedPane();
	// final JCheckBox nasdaq = JComponentFactory.doJCheckbox("NASDAQ");
	// final JCheckBox nyse = JComponentFactory.doJCheckbox("NYSE");
	final JRadioButton confirmLoad = new JRadioButton("yes");
	// settings components:
	final JLabel labelOutputFile = JComponentFactory
			.doJLabel("root path to text database: ");
	static final JTextArea PATH_SOURCE = JComponentFactory.doJTextArea();

	final CustomButton buttonBrowseFiles = JComponentFactory
			.doPathBrowseButton(PATH_SOURCE);

	// ///panel for dowloads location (move from)
	final JLabel labelDownloadsLocation = JComponentFactory
			.doJLabel("root path downloads: ");
	static final JTextArea PATH_DOWNLOADS = JComponentFactory.doJTextArea();

	final CustomButton buttonBrowseDownloadsFiles = JComponentFactory
			.doPathBrowseButton(PATH_DOWNLOADS);

	final JCheckBox averageDividend = new JCheckBox("use average of dividends");

	final JCheckBox autoLoad = new JCheckBox("automatically load database");

	final JCheckBox showFFT = new JCheckBox("show fft results technical data",
			false);

	final JLabel daysToDownload = JComponentFactory
			.doJLabel("number of days to request expected reports: ");
	static final JTextArea DAYS_OF_REPORTS = JComponentFactory.doJTextArea();

	static final TreeMap<String, File> MAP_TO_FILES = new TreeMap<String, File>();

	final JLabel wordSearch = JComponentFactory
			.doJLabel("words to search for (space separated) ");
	static final JTextArea wordsForSearch = JComponentFactory.doJTextArea();

	final CustomButton submitSearchButton = JComponentFactory
			.doDescriptionSearchButton(wordsForSearch);
	final FilterPanel filter_one = new FilterPanel();
	final FilterPanel filter_two = new FilterPanel();
	final FilterPanel filter_three = new FilterPanel();
	final FilterPanel[] filters = { filter_one, filter_two, filter_three };

	Integer[] daysAround = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
			15, 16, 17, 18, 19, 20 };
	JComboBox<Integer> daysChoice = new JComboBox<Integer>(daysAround);
	final JCheckBox useAveraging = new JCheckBox("average price (days)", true);

	static EarningsTest singleton;

	public static void main(String[] args) throws Exception {
		EarningsTest et = new EarningsTest();
	}

	public EarningsTest() {
		// TODO: use/display company descriptions
		// TODO: add dates to price/volume graph
		// TODO: merge idential files / double gae output
		File resources = new File(REPORTS_ROOT);
		if (!resources.exists())
			resources.mkdir();
		singleton = this;
		programSettings = MemoryManager.restoreSettings();
		// JFrame
		application.setVisible(true);
		// application.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		application.setSize(1230, 700);
		application.setLocation(50, 50);

		application.add(gui);
		setUpTabs();
		for (Entry<String, ArrayList<String>> x : EarningsTest.singleton.programSettings.tickersActual
				.entrySet()) {
		}
		// System.out.println(x);
	}

	private void setUpTabs() {
		PATH_SOURCE.setText(programSettings.rootPathToTextDatabase);
		PATH_DOWNLOADS.setText(programSettings.rootPathToDownloads);
		DAYS_OF_REPORTS.setText("" + programSettings.daysOfReportsToDownload);
		gui.removeAll();
		JScrollPanelledPane appTab = setUpControlsTab();
		gui.add("controlls", appTab);
	}

	private JScrollPanelledPane setUpControlsTab() {

		final JScrollPanelledPane stepScroll = new JScrollPanelledPane();
		addBasicControlls(stepScroll);
		addDatabaseLocator(stepScroll);
		addDownloadsLocator(stepScroll);
		addDatabaseLoadPanel(stepScroll);
		addTextStatLauncherPanel(stepScroll);
		addSearchProfilePanel(stepScroll);
		addFilterPanel(stepScroll);
		addPorfolioPanel(stepScroll);
		addStatisticExplorerPanel(stepScroll);
		return stepScroll;
	}

	private void addStatisticExplorerPanel(JScrollPanelledPane stepScroll) {
		JPanel myPanel = JComponentFactory
				.makePanel(JComponentFactory.HORIZONTAL);
		final JLabel statSearcher = JComponentFactory
				.doJLabel("display statistics on field: 	");

		JComboBox<String> fieldsForStats = new JComboBox<String>(
				DBLabels.labels);
		myPanel.add(statSearcher);
		myPanel.add(fieldsForStats);
		addStatisticsDisplayChoiceListener(fieldsForStats);
		stepScroll.addComp(myPanel);
	}

	private void addStatisticsDisplayChoiceListener(
			final JComboBox<String> fieldsForStats) {
		fieldsForStats.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = fieldsForStats.getSelectedIndex();

				if (index == 82 && !averageDividend.isSelected()) {
					Database.dividendStats.setVisible(true);
					System.out.println(" --- DONT use average --->");
					System.out.println(" --- index --->"+index);
					System.out.println(" --- is selected --->"+averageDividend.isSelected());
				} else {
					Database.statistics.get(index).setVisible(true);
					System.out.println(" --- DO use average --->");
					System.out.println(" --- index --->"+index);
					System.out.println(" --- is selected --->"+averageDividend.isSelected());
				}
			}

		});

	}

	private void addPorfolioPanel(JScrollPanelledPane stepScroll) {
		JPanel porfolioHistoryPanel = JComponentFactory
				.makePanel(JComponentFactory.HORIZONTAL);
		CustomButton myPorfolio = JComponentFactory.doPorfolioViewButton();
		CustomButton myHistory = JComponentFactory.doHistoryViewButton();
		porfolioHistoryPanel.add(myPorfolio);
		porfolioHistoryPanel.add(myHistory);
		stepScroll.addComp(porfolioHistoryPanel);
	}

	private void addFilterPanel(JScrollPanelledPane stepScroll) {
		for (FilterPanel f : filters)
			stepScroll.addComp(f);
	}

	private void addSearchProfilePanel(JScrollPanelledPane stepScroll) {
		JPanel textSearch = JComponentFactory
				.makePanel(JComponentFactory.HORIZONTAL);

		textSearch.add(wordSearch);
		textSearch.add(wordsForSearch);
		textSearch.add(submitSearchButton);
		textSearch.add(averageDividend);

		stepScroll.addComp(textSearch);
	}

	private void addTextStatLauncherPanel(JScrollPanelledPane stepScroll) {
		JPanel textExplor = JComponentFactory
				.makePanel(JComponentFactory.HORIZONTAL);

		textExplor.add(JComponentFactory
				.makeTextExplorerLauchButton("Explor Text Stats"));

		textExplor.add(useAveraging);
		textExplor.add(daysChoice);
		stepScroll.addComp(textExplor);
	}

	private void addBasicControlls(JScrollPanelledPane stepScroll) {

		final CustomButton buttonRefresh = new CustomButton("refresh");
		final CustomButton buttonSave = new CustomButton("save");
		final CustomButton buttonMoveFiles = new CustomButton("move files");

		buttonRefresh.addActionListener(makeRefreshButtonListener());
		buttonSave.addActionListener(makeSaveButtonListener());
		buttonMoveFiles.addActionListener(makeMoveFilesButtonListener());

		stepScroll.addComp(JComponentFactory.generatePanel(buttonRefresh,
				buttonSave, buttonMoveFiles));

		final CustomButton buttonGatherNextEarningsReports = new CustomButton(
				"get next earnings set");
		buttonGatherNextEarningsReports
				.addActionListener(makeGatherNextSetButtonListener());

		stepScroll.addComp(JComponentFactory.generatePanel(daysToDownload,
				DAYS_OF_REPORTS, buttonGatherNextEarningsReports));
	}

	private ActionListener makeMoveFilesButtonListener() {

		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame fileMover = new JFrame("Move Files");
				fileMover.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				fileMover.setSize(500, 750);
				fileMover.setVisible(true);

				final JScrollPanelledPane fileListScroller = new JScrollPanelledPane();
				fileMover.add(fileListScroller);

				programSettings.rootPathToTextDatabase = PATH_SOURCE.getText();

				File[] downloads = new File(PATH_DOWNLOADS.getText())
						.listFiles();
				Arrays.sort(downloads, NameFileComparator.NAME_COMPARATOR);
				for (File dls : downloads) {
					if (dls.getName().contains("BIG_n")
							|| dls.getName().contains("nas_")
							|| dls.getName().contains("ny_")) {
						double memory = ((int) (dls.length() / 1000)) / 1000.0;
						fileListScroller.addComp(JComponentFactory.doJLabel(dls
								.getName() + "   " + memory + " MB"));
						// fileListScroller.addComp(JComponentFactory
						// .doFileMoveButton(dls));
					}
				}
				fileListScroller.addComp(JComponentFactory
						.doConfirmMoveAllFiles());
				MemoryManager.saveSettings();

			}

		};
	}

	private void addDatabaseLoadPanel(JScrollPanelledPane stepScroll) {

		JPanel dbLoad = JComponentFactory
				.makePanel(JComponentFactory.HORIZONTAL);
		CustomButton loadDB = JComponentFactory.makeLoadDatabaseButton(
				"Load Database", confirmLoad);
		if (programSettings.autoLoadDatabase) {
			autoLoad.setSelected(true);
			confirmLoad.setSelected(true);
			confirmLoad.setEnabled(false);
			loadDB.setEnabled(false);
			JComponentFactory.startLoadingDataBase();
			// JComponentFactory.addReportsTab();
		} else {
			autoLoad.setSelected(false);
		}
		dbLoad.add(autoLoad);
		dbLoad.add(confirmLoad);
		dbLoad.add(loadDB);
		dbLoad.add(showFFT);
		stepScroll.addComp(dbLoad);
	}

	private void addDownloadsLocator(JScrollPanelledPane stepScroll) {
		final JPanel rootFileInfo = JComponentFactory
				.makePanel(JComponentFactory.HORIZONTAL);
		rootFileInfo.add(labelDownloadsLocation);
		rootFileInfo.add(PATH_DOWNLOADS);
		rootFileInfo.add(buttonBrowseDownloadsFiles);
		stepScroll.addComp(rootFileInfo);
	}

	private void addDatabaseLocator(JScrollPanelledPane stepScroll) {

		final JPanel rootFileInfo = JComponentFactory
				.makePanel(JComponentFactory.HORIZONTAL);
		rootFileInfo.add(labelOutputFile);
		rootFileInfo.add(PATH_SOURCE);
		rootFileInfo.add(buttonBrowseFiles);
		stepScroll.addComp(rootFileInfo);

	}

	private ActionListener makeGatherNextSetButtonListener() {

		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				storeUpcommingEarningsPages();

			}

		};
	}

	private ActionListener makeSaveButtonListener() {

		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				programSettings.rootPathToTextDatabase = PATH_SOURCE.getText();

				programSettings.rootPathToDownloads = PATH_DOWNLOADS.getText();

				programSettings.autoLoadDatabase = autoLoad.isSelected();
				try {
					programSettings.daysOfReportsToDownload = Integer
							.parseInt(DAYS_OF_REPORTS.getText());
				} catch (Exception e) {

				}
				MemoryManager.saveSettings();

			}

		};
	}

	private ActionListener makeRefreshButtonListener() {

		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				setUpTabs();

			}

		};
	}

	public void getDataForSet(String[] tickers) {

		// (" 2 36 44 9 9 9 9 9 9 9 9 9 9 1 1 1");
		for (String tk : tickers) {
			StringBuilder saveAsText = new StringBuilder();
			saveAsText.append(ShortDataCollectorUtil.cnnForecast(tk).trim());
			saveAsText.append(" ");
			saveAsText.append(ShortDataCollectorUtil.analystEstimates(tk));
			saveAsText.append(" ");
			saveAsText.append(ShortDataCollectorUtil.keyBasedData(
					ShortDataCollectorUtil.yahoobase + "/ks?s=" + tk,
					ShortDataCollectorUtil.keykeys));
			saveAsText.append(" ");
			saveAsText.append(ShortDataCollectorUtil
					.limitToTen(ShortDataCollectorUtil.pastPrices(tk)));
			saveAsText.append(" ");
			saveAsText.append(ShortDataCollectorUtil.optionsCount(tk));
			// System.out.println(tk);
			if (!ShortDataCollectorUtil.printQuickCount(saveAsText))
				System.out.println("\n--> " + saveAsText);
			rawMap.put(tk, saveAsText.toString());

		}
	}

	private void parseSavedPagesForTickers() {
		for (File htmlText : rawHtmlData) {
			String[] getTickersFrom = { "   " };
			try {
				String fromFile = FileUtils.readFileToString(htmlText);
				getTickersFrom = fromFile.replaceAll("\\s+", "")
						.split("q\\?s=");
				// System.out.println("\n" + htmlText.getName() + "      --->"
				// + getTickersFrom.length + "     :>");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i = 1; i < getTickersFrom.length; i++) {
				String s = getTickersFrom[i];
				try {
					if (s.indexOf(">") > 0 && s.indexOf("<") > 0) {
						// System.out.print(s.substring(s.indexOf(">") + 1,
						// s.indexOf("<"))
						// + "    ");
					}
				} catch (Exception e) {
					// System.err.println("error: "+s);
				}
			}
		}
	}

	private void storeUpcommingEarningsPages() {

		try {

			calendar.setTime(new Date());

			for (int i = 0; i < programSettings.daysOfReportsToDownload; i++) {
				String date = dateFormatForUrl.format(calendar.getTime());
				String dateFileFormat = dateFormatForFile.format(calendar
						.getTime());
				String earnings = "http://biz.yahoo.com/research/earncal/"
						+ date + ".html"; // /

				try {
					File htmlFile = new File(REPORTS_ROOT + dayString()
							+ "_URL_" + dateFileFormat + ".html");
					FileUtils.copyURLToFile(new URL(earnings), htmlFile);
					System.out.println(new SimpleDateFormat("EEE")
							.format(calendar.getTime()) + "      " + earnings);

					rawHtmlData.add(htmlFile);
				} catch (Exception e) {
					System.out.println("Error: "
							+ new SimpleDateFormat("EEE").format(calendar
									.getTime()) + "      " + earnings);

				}
				calendar.add(java.util.Calendar.DAY_OF_YEAR, 1);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void storeUpcommingEarningsPagesCustom() {

		try {
			long time = System.currentTimeMillis();

			for (int i = 0; i < 14; i++) {
				Date days = new Date(time + 1000 * 3600 * 24 * i);
				String date = dateFormatForUrl.format(days);
				String earnings = "http://biz.yahoo.com/research/earncal/"
						+ date + ".html"; // /

				try {
					File htmlFile = new File(REPORTS_ROOT + dayString()
							+ "_URL_" + date + ".html");
					FileUtils.copyURLToFile(new URL(earnings), htmlFile);
					System.out.println(new SimpleDateFormat("EEE").format(days)
							+ "      " + earnings);

					rawHtmlData.add(htmlFile);
				} catch (Exception e) {
					System.out.println("Error: "
							+ new SimpleDateFormat("EEE").format(days)
							+ "      " + earnings);

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String fileTitle(String add) {
		double time = System.currentTimeMillis();
		// convert to seconds
		time /= 1000.0;
		// convert to hours
		time /= 3600.0;
		// to days
		time /= 24.0;
		// limit to one decimal place
		time = (double) ((int) (time * 10) / 10.0);

		return time + "_" + add + ".txt";
		//
	}

	public static String dayString() {
		double time = System.currentTimeMillis();
		// convert to seconds
		time /= 1000.0;
		// convert to hours
		time /= 3600.0;
		// to days
		time /= 24.0;
		// limit to one decimal place
		time = (double) ((int) (time * 10) / 10.0);

		return "" + time;
		//
	}

	public static double dayNumber() {
		double time = System.currentTimeMillis();
		// convert to seconds
		time /= 1000.0;
		// convert to hours
		time /= 3600.0;
		// to days
		time /= 24.0;
		// limit to one decimal place
		time = (double) ((int) (time * 10) / 10.0);

		return time;
		//
	}

	private void saveCurrentTickers() {

		try {
			FileUtils.copyURLToFile(new URL(qTickerDownloadSite), new File(
					REPORTS_ROOT + fileTitle("NASDAq")));
			FileUtils.copyURLToFile(new URL(yTickerDownloadSite), new File(
					REPORTS_ROOT + fileTitle("NySE")));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
