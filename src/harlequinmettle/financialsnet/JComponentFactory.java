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
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class JComponentFactory {

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

	public static JScrollPanelledPane doTab(String filePath,
			final int BUTTON_TYPE) {
		JScrollPanelledPane stepScroll = new JScrollPanelledPane();

		File dir = new File(filePath);
		File[] files = dir.listFiles();
		Arrays.sort(files);
		for (int i = files.length - 1; i >= 0; i--) {
			File f = files[i];
			CustomButton eb = new CustomButton("button is undefined");
			switch (BUTTON_TYPE) {
			case CustomButton.HTML_FILE_LOAD_BUTTON_TYPE:
				eb = JComponentFactory.makeButton(f.getName(), BUTTON_TYPE);
				break;
			default:
				break;
			}

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

	public static CustomButton makeButton(String buttonTitle, int BUTTON_TYPE) {
		final CustomButton a = new CustomButton(buttonTitle);
		switch (BUTTON_TYPE) {
		case CustomButton.HTML_FILE_LOAD_BUTTON_TYPE:
			a.addActionListener(JComponentFactory
					.makeHtmlButtonListener(buttonTitle));
			break;
		case CustomButton.START_LOAD_DATABASE_TYPE:
			a.addActionListener(JComponentFactory
					.makeDBLoadButtonListener(buttonTitle));
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

	private static ActionListener makeHtmlButtonListener(
			final String buttonTitle) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame jf = new JFrame(buttonTitle);
				jf.setSize(300, 300);
				jf.setVisible(true);
				jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		};
	}

	private static ActionListener makeDBLoadButtonListener(
			final String buttonTitle) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
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
				Database db = new Database();
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
