package ffttest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

class PopUp extends JPopupMenu implements ActionListener,
ItemListener  {
    JMenuItem anItem;
    public PopUp(){
        anItem = new JMenuItem("Click Me!");
        add(anItem);
       anItem.addActionListener(this);;
    }
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
	System.out.println(arg0.getActionCommand());
	}

    
}
