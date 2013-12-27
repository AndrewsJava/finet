package harlequinmettle.financialsnet;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.commons.io.FileUtils;

public class EarningsTest {
	final String qTickerDownloadSite = "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nasdaq&render=download";
	final String yTickerDownloadSite = "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nyse&render=download";
	final Calendar calendar = Calendar.getInstance();
	final TreeMap<String, String> rawMap = new TreeMap<String, String>();
	final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	final ArrayList<File> rawHtmlData = new ArrayList<File>();
	final String[] dow = { "DJI" };
	final String root = "earningsReportsFiles" + File.separator;
	final JFrame application = new JFrame("Financails Net");
	final JTabbedPane gui = new JTabbedPane();

	public static void main(String[] args) throws Exception {
		EarningsTest et = new EarningsTest();
		// saveCurrentTickers();
		// storeUpcommingEarningsPages();
		// parseSavedPagesForTickers();
	}

	public EarningsTest() {

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
		JScrollPanelledPane filesTab = JComponentFactory.doTab(root, JComponentFactory.HTML_FILE);
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

	private   void parseSavedPagesForTickers() {
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
