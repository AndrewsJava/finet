package harlequinmettle.financialsnet;

import harlequinmettle.financialsnet.interfaces.DBLabels;

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FilterPanel extends JPanel{
	JCheckBox include = new JCheckBox("apply filter");
	String[] options = {"between"	, "less than", "greater than"};
	JComboBox<String> op = new JComboBox<String>(DBLabels.labels);
	JComboBox<String> choices = new JComboBox<String>(DBLabels.labels);
	JTextField low = new JTextField();
	JTextField high = new JTextField();
	
public FilterPanel(){
//horizontal layout
	this.setLayout(new GridLayout(1, 0));
	add(include); 
	add(choices);
	//TODO: add options
	//add(op);
	add(low);
	add(high);
	
}
public int getId(){
	return choices.getSelectedIndex();
}
public float getLow(){
	 float lowVal  = Float.MIN_VALUE;
	 try{
	 lowVal =	Float.parseFloat(low.getText());
	 if(lowVal == lowVal)
	 return lowVal;
	 }catch(Exception e){
		 
	 }
		 return lowVal;
	} 
public float getHigh(){
	 float highVal =  Float.MAX_VALUE;
	 
	 try{
		 highVal =	Float.parseFloat(high.getText());
	 
	 if(highVal == highVal)
	 return highVal;
	 }catch(Exception e){
		 
	 }
		 return highVal ;
	}
	}
