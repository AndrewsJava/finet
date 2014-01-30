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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.BorderFactory;
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

		File dir = new File(EarningsTest.ROOT);
		File[] files = dir.listFiles();
		Arrays.sort(files);
		for (int i = files.length - 1; i >= 0; i--) {
			File f = files[i];
			CustomButton eb = JComponentFactory.makeHtmlLoadButton(f.getName());

			EarningsTest.MAP_TO_FILES.put(f.getName(), f);

			eb.setHorizontalAlignment(SwingConstants.LEFT);
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

	public static CustomButton makeHtmlLoadButton(final String buttonTitle) {
		final CustomButton a = new CustomButton(buttonTitle);

		a.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!Database.loaded)
					return;
				final JFrame jf = new JFrame(buttonTitle);
				jf.setSize(1300,650);
				final ArrayList<ProfileCanvas> theCanvasesToRescale = new ArrayList<ProfileCanvas>();
				jf.addComponentListener(JComponentFactory.doWindowRescaleListener(theCanvasesToRescale));
				jf.setVisible(true);
				jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JTabbedPane compareTickers = new JTabbedPane();
				jf.add(compareTickers);
				ArrayList<String> tickers = parseFileForTickers(EarningsTest.MAP_TO_FILES
						.get(a.getText()));

//				for (Entry<String, File> ent : EarningsTest.MAP_TO_FILES
//						.entrySet())
//					System.out.println(ent.getKey() + "  --->" + ent.getValue());
				for (String s : tickers) {
					int tickerLocation = Database.dbSet.indexOf(s);
					if (tickerLocation > 0){
						ProfileCanvas pc = new ProfileCanvas(buttonTitle,
								tickerLocation, jf.getWidth(),jf.getHeight());
//						ProfileCanvas pc = new ProfileCanvas(
//								tickerLocation);
						theCanvasesToRescale.add(pc);
						compareTickers.add(s, JComponentFactory
								.makeJScrollPane(pc));
					}
				}
			}
		}

		);

		return a;
	}
 

	protected static ComponentAdapter doWindowRescaleListener(
			final ArrayList<ProfileCanvas> theCanvasesToRescale) {
		return new ComponentAdapter() {

		 
			@Override
			public void componentResized(ComponentEvent arg0) {
				for(ProfileCanvas pc : theCanvasesToRescale)
				pc.rescaleCanvas(arg0.getComponent().getBounds().getSize());   
				
			}
 
			
		};
	}

	private static ArrayList<String> parseFileForTickers(File htmlFile) {
		ArrayList<String> tickers = new ArrayList<String>();
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
					System.out.print(ticker + "    ");
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
public static void addReportsTab(){

	JScrollPanelledPane filesTab = JComponentFactory
			.doHtmlTickerFilesTab();

	EarningsTest.singleton.gui.add("Earnings Reports", filesTab);
}
	public static void startLoadingDataBase() {
		Thread loadDB = new Thread(new Runnable() {

			@Override
			public void run() {
				EarningsTest.db = new Database(
						EarningsTest.PATH_SOURCE.getText());
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
				for(Entry<Integer,ArrayList<String>> ent: orderResults.entrySet()){
//	wordStat.append("\n\n"+ent.getKey()+"\n"+ent.getValue());
					if(ent.getKey()>1600)
						for(String s: ent.getValue())
					wordStat.append("\""+s+"\" , ");
				}
			}
		}

		);

		return a;
	}

}
