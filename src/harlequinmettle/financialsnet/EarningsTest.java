package harlequinmettle.financialsnet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;

public class EarningsTest {
	static ProgramSettings programSettings;
	final String qTickerDownloadSite = "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nasdaq&render=download";
	final String yTickerDownloadSite = "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nyse&render=download";
	final Calendar calendar = Calendar.getInstance();
	final TreeMap<String, String> rawMap = new TreeMap<String, String>();
	final SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
	final ArrayList<File> rawHtmlData = new ArrayList<File>();
	final String[] dow = { "DJI" };
	final String root = "earningsReportsFiles" + File.separator;
	final JFrame application = new JFrame("Financails Net");
	final JTabbedPane gui = new JTabbedPane();
	final JCheckBox nasdaq = JComponentFactory.doJCheckbox("NASDAQ");
	final JCheckBox nyse = JComponentFactory.doJCheckbox("NYSE");
	// settings components:
	final JLabel labelOutputFile = JComponentFactory
			.doJLabel("root path to text database: ");
	final JTextArea rootPathTextArea = JComponentFactory.doJTextArea();
	final CustomButton buttonBrowseFiles = JComponentFactory
			.doBrowseButton(rootPathTextArea);

	//
	// final JLabel labelStepsFile = JComponentFactory.doJLabel(
	// "Path to calabash canned steps and custom steps : ", new Color(90, 190,
	// 210));
	// final JTextArea textStepsPath = JComonentFactory.doJTextArea(
	// new Color(140, 195, 190));
	// final CustomButton buttonBrowseStepsFiles = Maker
	// .makeFilePathButton(textStepsPath, Maker.STEPS_PATH);

	public static void main(String[] args) throws Exception {
		EarningsTest et = new EarningsTest();
		// et.saveCurrentTickers();
		// storeUpcommingEarningsPages();
		// parseSavedPagesForTickers();
	}

	public EarningsTest() {
		programSettings = MemoryManager.restoreSettings();
		rootPathTextArea.setText(programSettings.rootPathToTextDatabase);
		// JFrame
		application.setVisible(true);
		// application.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		application.setSize(1230, 700);
		application.setLocation(50, 50);

		application.add(gui);
		setUpTabs();
	}

	private void setUpTabs() {
		gui.removeAll();
		JScrollPanelledPane appTab = setUpControlsTab();
		gui.add("controlls", appTab);

		JScrollPanelledPane filesTab = JComponentFactory.doTab(root,
				CustomButton.HTML_FILE_LOAD_BUTTON_TYPE);
		gui.add("one", filesTab);
	}

	private JScrollPanelledPane setUpControlsTab() {

		JScrollPanelledPane stepScroll = new JScrollPanelledPane();

		final CustomButton buttonRefresh = JComponentFactory.makeButton(
				"refresh", CustomButton.REFRESH_GUI_TYPE);
		buttonRefresh.addActionListener(makeRefreshButtonListener());
		final CustomButton buttonSave = JComponentFactory.makeButton("save",
				CustomButton.SAVE_SETTINGS_TYPE);
		buttonSave.addActionListener(makeSaveButtonListener());
		stepScroll.addComp(JComponentFactory.generatePanel(buttonRefresh,
				buttonSave));
		JPanel rootFileInfo = JComponentFactory
				.makePanel(JComponentFactory.HORIZONTAL);
		rootFileInfo.add(labelOutputFile);
		rootFileInfo.add(rootPathTextArea);
		rootFileInfo.add(buttonBrowseFiles);
		stepScroll.addComp(rootFileInfo);

		JPanel dbLoad = JComponentFactory
				.makePanel(JComponentFactory.HORIZONTAL);
		dbLoad.add(nasdaq);
		dbLoad.add(nyse);
		dbLoad.add(JComponentFactory.makeButton("Load Database",
				CustomButton.START_LOAD_DATABASE_TYPE));
		stepScroll.addComp(dbLoad);
		return stepScroll;
	}

	private ActionListener makeSaveButtonListener() {

		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				programSettings.rootPathToTextDatabase = rootPathTextArea
						.getText();

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
				System.out.println("\n" + htmlText.getName() + "      --->"
						+ getTickersFrom.length + "     :>");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i = 1; i < getTickersFrom.length; i++) {
				String s = getTickersFrom[i];
				try {
					if (s.indexOf(">") > 0 && s.indexOf("<") > 0) {
						System.out.print(s.substring(s.indexOf(">") + 1,
								s.indexOf("<"))
								+ "    ");
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

			for (int i = 0; i < 14; i++) {
				String date = sdf.format(calendar.getTime());
				String earnings = "http://biz.yahoo.com/research/earncal/"
						+ date + ".html"; // /

				try {
					File htmlFile = new File(root + dayNumber() + "_URL_"
							+ date + ".html");
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
				String date = sdf.format(days);
				String earnings = "http://biz.yahoo.com/research/earncal/"
						+ date + ".html"; // /

				try {
					File htmlFile = new File(root + dayNumber() + "_URL_"
							+ date + ".html");
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

	public static String dayNumber() {
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

	private void saveCurrentTickers() {

		try {
			FileUtils.copyURLToFile(new URL(qTickerDownloadSite), new File(root
					+ fileTitle("NASDAq")));
			FileUtils.copyURLToFile(new URL(yTickerDownloadSite), new File(root
					+ fileTitle("NySE")));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
