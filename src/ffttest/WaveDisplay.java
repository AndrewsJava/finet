package ffttest;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;


public class WaveDisplay  {
double [] data ;
SineCanvas showWave;
double scale = 1;
public WaveDisplay(double[] data){
this.data = data;
init(1,0);

}
public WaveDisplay(double[] data, double scale,double offset){
this.data = data;
this.scale = scale;
init(scale,offset);

}
	
public void init(double scale, double offset){
	
	    showWave = new SineCanvas(data);
	showWave.setScale(scale);
	showWave.setOffset(offset); 
	JFrame display = new JFrame();
	JScrollPane jsp = makeJScrollPane(showWave);
	display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	

	display.add(jsp);

	display.setVisible(true);
    display.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
