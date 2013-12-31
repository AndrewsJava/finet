package harlequinmettle.financialsnet;
 

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MemoryManager {

	private static final String SETTINGS_OBJECT_PATH = ".finacial_net_program_settings";

	public static void saveSettings() {
		memorizeObject(EarningsTest.programSettings, SETTINGS_OBJECT_PATH);
	}

	// /////////// ////////////////////////////
	public static ProgramSettings restoreSettings() {
		ProgramSettings settings = null;
		try {
			FileInputStream filein = new FileInputStream(SETTINGS_OBJECT_PATH);
			ObjectInputStream objin = new ObjectInputStream(filein);
			try {
				settings = (ProgramSettings) objin.readObject();
			} catch (ClassCastException cce) {
				System.out.println("CLASSCASTEXCEPTION");
			}
			objin.close();
		} catch (Exception ioe) {
			System.out.println("NO resume: saver");
		}
		if (settings == null)
			settings = new ProgramSettings();
		return settings;
	} //

	public static void memorizeObject(Object ob, String obFileName) {
		System.out.println("memorizing object ... ");
		File nextFile = new File(obFileName);
		// nextFile.mkdirs();//in case they don't exist
		try {
			nextFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FileOutputStream fileout = new FileOutputStream(obFileName);

			ObjectOutputStream objout = new ObjectOutputStream(fileout);
			objout.writeObject(ob);
			objout.flush();
			objout.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("UNABLE TO SAVE OBJECT TO: " + obFileName);
		}
		System.out.println("done memorizing object to: " + obFileName);
	}

}
