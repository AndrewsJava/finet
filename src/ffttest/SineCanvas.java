package ffttest;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

public class SineCanvas extends JPanel implements MouseWheelListener{
	double[] data;
	private double vscale = 1;
	private double offset = 0;
	private static final int HALF_WIDTH = 300;

	public SineCanvas(double[] data) {
		this.data = data;
		this.addMouseWheelListener(this);
		this.addMouseListener(new PopClickListener());
		setPreferredSize(new Dimension(TestMain.TWO_POWER, HALF_WIDTH * 2));
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
		g.fillRect(0, 0, TestMain.TWO_POWER, HALF_WIDTH * 5);
		g.setColor(Color.white);

		for (int i = 0; i < data.length; i++) {
			g.fillOval(i, (int) (offset+(HALF_WIDTH - vscale * data[i])), 4, 4);
		}
		g.setColor(Color.DARK_GRAY);
		for (int i = 0; i < TestMain.TWO_POWER; i+= 10) {
			g.drawLine(i,0,i,2*HALF_WIDTH);
		}
		g.setColor(Color.GRAY);
		for (int i = 0; i < TestMain.TWO_POWER; i+= 50) {
			g.drawLine(i,0,i,2*HALF_WIDTH);
		}
		g.setColor(new Color(0,100,150));
		for (int i = 0; i < TestMain.TWO_POWER; i+= 500) {
			g.drawLine(i,0,i,2*HALF_WIDTH);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		float notches = e.getWheelRotation();
		final double BY = 0.05;
	if(notches>0){
		vscale*=(1+BY);
	}else{
		vscale*=(1-BY);
	}
		 setScale(vscale);
	 repaint(); 
	}
}
