package harlequinmettle.financialsnet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

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

public class JComponentFactory  {

	public static final int HORIZONTAL = 8000000;
	public static final int VERTICAL = 1111111;
	public static final int GENERAL_LAYOUT = 121212121;

	public static final Border BLACKBORDER = BorderFactory
			.createLineBorder(Color.black);
	public static final Border CUSTOMBORDER = BorderFactory
	.createLineBorder(new Color(100,220,245));
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

	public static JScrollPanelledPane doHtmlTickerFilesTab(  ) {
		JScrollPanelledPane stepScroll = new JScrollPanelledPane();

		File dir = new File(EarningsTest.ROOT);
		File[] files = dir.listFiles();
		Arrays.sort(files);
		for (int i = files.length - 1; i >= 0; i--) {
			File f = files[i];  
				CustomButton eb = JComponentFactory.makeHtmlLoadButton(f.getName() );

				EarningsTest.MAP_TO_FILES.put(f.getName(), f);

			eb.setHorizontalAlignment(SwingConstants.LEFT);
			stepScroll.addComp(eb);

		}
		return stepScroll;

	}

	public static JPanel generatePanel(JComponent... comps) {
		JPanel shell = JComponentFactory.makePanel(JComponentFactory.HORIZONTAL);
		//if (!Cuker.programSettings.useSystemLookAndFeel)
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

	public static CustomButton makeButton(String buttonTitle, int BUTTON_TYPE, JComponent param) {
		final CustomButton a = new CustomButton(buttonTitle);
		switch (BUTTON_TYPE) {
 
		case CustomButton.START_LOAD_DATABASE_TYPE:
			a.addActionListener(JComponentFactory
					.makeDBLoadButtonListener(buttonTitle,(JRadioButton)param));
			break;
		case CustomButton.REFRESH_GUI_TYPE:
//			a.addActionListener(JComponentFactory
//					.makeRefreshButtonListener( ));
			break;
		case CustomButton.SAVE_SETTINGS_TYPE:
//			a.addActionListener(JComponentFactory
//					.makeSaveButtonListener( ));
			break;
		default:
			break;
		}
		return a;
	}

	public static CustomButton makeHtmlLoadButton(final String buttonTitle ) {
		final CustomButton a = new CustomButton(buttonTitle);
 
			a.addActionListener(
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							JFrame jf = new JFrame(buttonTitle);
							jf.setSize(900, 500);
							jf.setVisible(true);
							jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							JTabbedPane compareTickers = new JTabbedPane();
							jf.add(compareTickers);
							ArrayList<String> tickers = parseFileForTickers(EarningsTest.MAP_TO_FILES.get(a.getText()));
 
							for(Entry<String,File> ent:EarningsTest.MAP_TO_FILES.entrySet() )
							System.out.println(ent.getKey() +"  --->"+ent.getValue()); 
							for(String s: tickers){
								int tickerLocation = Database.dbSet.indexOf(s);
								if(tickerLocation>0)
								compareTickers.add(s,JComponentFactory.makeJScrollPane( new ProfileCanvas(tickerLocation)));
							}
						}
					}
			
			
			);
		 
 
		return a;
	}
 

	private static ArrayList<String> parseFileForTickers(File htmlFile) {
	 ArrayList<String> tickers = new ArrayList<String>();
			String[] getTickersFrom = { "   " };
			try {
				String fromFile = FileUtils.readFileToString(htmlFile);
				getTickersFrom = fromFile.replaceAll("\\s+", "")
						.split("q\\?s=");
				System.out.println("\n" + htmlFile.getName() + "      --->"
						+ getTickersFrom.length + "     :>");
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
						System.out.print(ticker
								+ "    ");
					}
				} catch (Exception e) {
					// System.err.println("error: "+s);
				} 
		}
			return tickers;
	}
	private static ActionListener makeDBLoadButtonListener(
			final String buttonTitle,final JRadioButton yes) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(yes.isSelected())
				startLoadingDataBase();
				JFrame jf = new JFrame(buttonTitle);
				jf.setSize(900, 100);
				jf.setVisible(true);
				jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		};
	}

	protected static void startLoadingDataBase() {
		Thread loadDB = new Thread(new Runnable() {

			@Override
			public void run() {
			 EarningsTest.db = new Database(EarningsTest.PATH_SOURCE.getText());
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

	public static CustomButton doBrowseButton(final JTextArea textToUpdate) {
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

}
