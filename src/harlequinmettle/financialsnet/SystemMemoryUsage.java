package harlequinmettle.financialsnet;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

public class SystemMemoryUsage extends JFrame implements Runnable{
 int inc = 0;
	public SystemMemoryUsage(){
		this.setSize(300,800);
		this.setVisible(true);
		new Thread(this).start();
	}
	
	
	
	
	public void paint(Graphics g){
		float maxMem  = Runtime.getRuntime().maxMemory();
		float useMem  = Runtime.getRuntime().totalMemory();
		float freMem  = Runtime.getRuntime().freeMemory();
		int height = this.getHeight()-90;
		
		float scale = (float)height/maxMem;
		int max = (int) (maxMem*scale);
		 int use = (int) (useMem*scale);
		 int fre = (int) (freMem*scale);
		 
		 
		g.setColor(Color.black);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		g.setColor(Color.red);
		g.fillRect(0, 80, 100, max);
		
		g.setColor(Color.blue);
		g.fillRect(100, 80, 100, use);

		g.setColor(Color.green);
		g.fillRect(200, 80, 100, fre+inc);
		
		g.setColor(Color.white);
		g.drawString(""+(long)maxMem/1000000+" MB",0,70);
		g.drawString(""+(long)useMem/1000000+" MB",100,70);
		g.drawString(""+(long)freMem/1000000+" MB",200,70);
		
	}
	@Override
	public void run() {
		while(true){
		this.repaint();
	try {
		Thread.sleep(200);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	}

}
