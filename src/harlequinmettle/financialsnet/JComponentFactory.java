package harlequinmettle.financialsnet;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class JComponentFactory {

	public static final int HTML_FILE = 100001;
	
	public static final int HORIZONTAL = 8000000;
	public static final int VERTICAL = 1111111;

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

	public static JScrollPanelledPane doTab(String filePath,
			final int BUTTON_TYPE) {
		JScrollPanelledPane stepScroll = new JScrollPanelledPane();

		File dir = new File(filePath);
		File[] files = dir.listFiles();
		Arrays.sort(files);
		for (int i = files.length - 1; i >= 0; i--) {
			File f = files[i];
			CustomButton eb = null;
			switch (BUTTON_TYPE) {
			case HTML_FILE:
				eb = JComponentFactory.makeButton(f.getName());
				break;
			default:
				break;
			}

			eb.setHorizontalAlignment(SwingConstants.LEFT);
			stepScroll.addComp(eb);

		}
		return stepScroll;

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

	public static CustomButton makeButton(String buttonTitle) {
		final CustomButton a = new CustomButton(buttonTitle);

		a.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

			}

		});
		return a;
	}

}
