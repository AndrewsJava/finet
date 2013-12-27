package harlequinmettle.financialsnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

@SuppressWarnings({ "serial", "hiding" })
public class FrequencyMap {
	public TreeMap<String, Integer> TMS = new TreeMap<String, Integer>();
	public TreeMap<Integer, Integer> TMI = new TreeMap<Integer, Integer>();
	public TreeMap<Integer, ArrayList<Float>> TMIData = new TreeMap<Integer, ArrayList<Float>>();
	public int _Type;
	public static final int STRING = 565656565;
	public static final int INTEGER = 717171717;

	// public ArrayList<Float> allData = new ArrayList<Float>();

	public FrequencyMap(int s) {
		_Type = s;
	}

	public void addToData(List<Float> list, int i) {
		if (TMIData.containsKey(i)) {
			TMIData.get(i).addAll(list);
		} else {
			TMIData.put(i, new ArrayList<Float>(list));
		}
	}

	public void add(String s) {
		if (TMS.containsKey(s)) {
			TMS.put(s, TMS.get(s) + 1);
		} else {
			TMS.put(s, 1);
		}
	}

	public void add(int i) {
		if (TMI.containsKey(i)) {
			TMI.put(i, TMI.get(i) + 1);
		} else {
			TMI.put(i, 1);

		}

	}

	public String toString() {
		String rep = "";
		switch (_Type) {
		case STRING:
			for (Entry<String, Integer> ent : TMS.entrySet()) {
				rep += ent.getKey() + " -occurs>" + ent.getValue() + "\n";
			}
			break;
		case INTEGER:

			for (Entry<Integer, Integer> ent : TMI.entrySet()) {
			 
					rep += ent.getKey() + " -occurs>" + ent.getValue()  + "\n";
				 
			}
			for (Entry<Integer, ArrayList<Float>> ent : TMIData.entrySet()) {
				ArrayList<Float> alf = ent.getValue();
				if (alf == null)
					rep += ent.getKey() + " -occurs>" + ent.getValue()
							+ " DQ: null" + "\n";
				else
					rep += ent.getKey() + " -dataQuality>" +	 new StatInfo(alf, false).dataQuality
							+ "\n";
			}
			break;
		default:
			break;
		}

		return rep;

	}

	public int size() {
		int size = 0;
		switch (_Type) {
		case STRING:
			size = TMS.size();
			break;
		case INTEGER:
			size = TMI.size();
			break;
		default:
			break;
		}
		return size;
	}

}
