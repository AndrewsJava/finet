package ffttest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Map.Entry;
import java.util.Arrays;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class HorizontalGraph extends JPanel implements MouseWheelListener {

	double[] data;
	private double vscale = 1;
	private double offset = 0;
	private static final int HALF_WIDTH = 500;
	TreeMap<Double, Long> frequencies = new TreeMap<Double, Long>();
	TreeMap< Long, Double> freq_s = new TreeMap< Long, Double>();
	public static final int FONT_SIZE = 30;
	public static final Font DATA_FONT = new Font("Sans-Serif", Font.ITALIC,
			FONT_SIZE);

	 
	public void setFrequencies(TreeMap<Double, Long> frequencies) {
		this.frequencies = frequencies;
		convertFrequencies();
	}

	private void convertFrequencies() {
		for (Entry<Double, Long> ent : frequencies.entrySet()) {
			if (freq_s.containsKey(ent.getValue())) {
			 freq_s.put(ent.getValue(), freq_s.get(ent.getValue())+ent.getKey());
			}else{

				 freq_s.put(ent.getValue(),ent.getKey());
			}
		}
	}

	public HorizontalGraph(double[] data) {
		this.data = data;
		System.out.println(" ---------------------->>> HORIZONTAL GRAPH CREATED WITH DATA length: "+data.length);
		this.addMouseWheelListener(this);
		this.addMouseListener(new PopClickListener());
		setPreferredSize(new Dimension(data.length, HALF_WIDTH * 2));
		repaint();
	}

	public void setScale(double scale) {
		vscale = scale;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, data.length, HALF_WIDTH * 5);
		g.setColor(Color.white);
		g.setFont(DATA_FONT);
		int j = 0;
		for (Entry< Long,Double> ent : freq_s.entrySet()) {
			 
				j += 3;
				g.drawString(" frequency: " + ent.getKey(), 40, FONT_SIZE * j + 6 * j);
				g.drawString(" mag_sum: " + ent.getValue(), 40, FONT_SIZE
						* (j + 1) + 6 * (j + 1));
		 
		}
		for (int i = 0; i < data.length; i++) {
			g.fillOval(i, (int) (offset + (HALF_WIDTH - vscale * data[i])), 4,
					4);
		}
		g.setColor(Color.DARK_GRAY);
		for (int i = 0; i < data.length; i += 10) {
			g.drawLine(i, 0, i, 2 * HALF_WIDTH);
		}
		g.setColor(Color.GRAY);
		for (int i = 0; i < data.length; i += 50) {
			g.drawLine(i, 0, i, 2 * HALF_WIDTH);
		}
		g.setColor(new Color(0, 100, 150));
		for (int i = 0; i < data.length; i += 500) {
			g.drawLine(i, 0, i, 2 * HALF_WIDTH);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		float notches = e.getWheelRotation();
		final double BY = 0.05;
		if (notches > 0) {
			vscale *= (1 + BY);
		} else {
			vscale *= (1 - BY);
		}
		setScale(vscale);
		repaint();
	}

	public static JScrollPane makeJScrollPane(JComponent jComp) {
		JScrollPane scroller = new JScrollPane(jComp,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		// scroller.setViewportView(jComp);
		// scroller.setPreferredSize(new Dimension(600, 300));
		scroller.getVerticalScrollBar().setUnitIncrement(32);
		scroller.getHorizontalScrollBar().setUnitIncrement(32);
		return scroller;
	}

}
