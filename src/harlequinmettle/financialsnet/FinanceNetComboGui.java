package harlequinmettle.financialsnet;
 

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class FinanceNetComboGui extends JPanel {

	// in general two panes
	final JSplitPane share = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

	public final JTabbedPane tabbedPane = new JTabbedPane();
	public final LinkedHashMap<String, JScrollPane> PANES = new LinkedHashMap<String, JScrollPane>();

	public final JTextArea textLower = new JTextArea("\n\n\n\n\n\n\n\n\n\n");
	public final ConcurrentLinkedDeque<String> TEXT_HISTORY = new ConcurrentLinkedDeque<String>();
	public final ConcurrentLinkedDeque<String> TEXT_FUTURE = new ConcurrentLinkedDeque<String>();
	public Thread saveHistory;
	final JScrollPane textScroll = JComponentFactory.makeTextScroll(textLower);
	final JPanel southPanel = new JPanel(); 
	private JPanel controllsOption;
	public static boolean revising = false;
	public static long saveInterval = 200;

	public FinanceNetComboGui() {
		this.setLayout(new GridLayout(1, 0));
		this.setUpGUI();
		textLower.addKeyListener(revisionMonitor);
		defineStartSaveHistory();
	}

	// would like to add to all subcomponents to for focus independent undo/redo
	// capabilities
	public KeyListener revisionMonitor = new KeyListener() {

		@Override
		public void keyPressed(KeyEvent e) {
			if ((e.getKeyCode() == KeyEvent.VK_Z)
					&& ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
				undo();
			} else if ((e.getKeyCode() == KeyEvent.VK_Y)
					&& ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
				redo();
			} else {
				revising = false;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}

	};

	public void redo() {
		revising = true;
		if (!TEXT_FUTURE.isEmpty()) {
			String restoreText = TEXT_FUTURE.pop();
			TEXT_HISTORY.push(restoreText);
			textLower.setText(restoreText);
			// gui.TEXT_HISTORY.tailMap(fromKey)
		} 
	}

	public void undo() {

	 revising = true;
		if (!TEXT_HISTORY.isEmpty()) {
			String lastText = TEXT_HISTORY.pop();
			TEXT_FUTURE.push(lastText);
			textLower.setText(lastText);
			// gui.TEXT_HISTORY.tailMap(fromKey)
		} 

	}

	private void defineStartSaveHistory() {
		saveHistory = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					while (revising) {
						sleep(10);
					}
					String text = textLower.getText();
					if (!TEXT_HISTORY.contains(text)) {
						TEXT_HISTORY.push(textLower.getText());
						System.out.println(textLower.getText().replaceAll("\\s"," "));
					}

					sleep(saveInterval);
				}
			}

			// private long getInverseTime() {
			// return Long.MAX_VALUE-System.currentTimeMillis();
			// }

			private void sleep(long time) {
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
				}
			}

		});
		saveHistory.start();
	}

	public void addControlPanel(JPanel controlls) {
		controllsOption = controlls;
		southPanel.add(controllsOption, BorderLayout.PAGE_START);
		// this.repaint();
	}

	private void setUpGUI() {
		// add split pane to application frame
		this.add(share);
		southPanel.setPreferredSize(new Dimension(500,500));
		southPanel.setLayout(new BorderLayout());
		southPanel.add(textScroll, BorderLayout.CENTER);
		if (controllsOption != null)
			southPanel.add(controllsOption, BorderLayout.PAGE_START);
		// add scroll text to bottom pane
		share.add(southPanel, JSplitPane.BOTTOM);
		share.setDividerLocation(180);
		// add tabbed pane to upper pane
		share.add(tabbedPane, JSplitPane.TOP);
		updateTabs(); 

	}

	// control tabs by adding/removing jsrollpanes from arraylist
	public void updateTabs() {
		tabbedPane.removeAll();
		for (Entry<String, JScrollPane> ent : PANES.entrySet()) {
			tabbedPane.add(ent.getKey(), ent.getValue());
		}
	}
 
}
