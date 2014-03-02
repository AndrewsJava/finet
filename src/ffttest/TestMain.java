package ffttest;

import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class TestMain {
	public static final int TWO_POWER = (int) Math.pow(2, 16);

	public static void main(String[] args) {
		double[] fs = { 0 * Math.PI / 180, 1 * Math.PI / 180,
				2 * Math.PI / 180, 3 * Math.PI / 180, 4 * Math.PI / 180,
				5 * Math.PI / 180, 6 * Math.PI / 180, 7 * Math.PI / 180, };
		System.out.println(Arrays.toString(fs));
		// frequency,magnitude,phase
		double[] timeData = generateWave(0.0001,//
				// fs[0], 150, 0,//
				fs[1], 150, 0,//
				fs[2], 150, 0,//
				fs[3], 150, 0,//
				fs[4], 150, 0,//
				fs[5], 150, 0,//
				fs[6], 150, 0,//
				fs[7], 150, 0//
		);

		WaveDisplay time = new WaveDisplay(timeData);

		FastFourierTransformer transformer = new FastFourierTransformer(
				DftNormalization.STANDARD);
		try {
			Complex[] complx = transformer.transform(timeData,
					TransformType.FORWARD);
			double[] frequencyData = new double[complx.length];

			System.out.print("--->" );
			for (int i = 0; i < complx.length/2; i++) {
				double rr = (complx[i].getReal());
				double ri = (complx[i].getImaginary());

				frequencyData[i] = Math.sqrt((rr * rr) + (ri * ri));
				if (isLocalMax(frequencyData, i - 1)) {
				//	System.out.println("----->" + i);
					System.out.print(", "
							+ (2 * Math.PI * i / (TWO_POWER * 1f)));
				}
				// int freqCoeff = (int) (TWO_POWER * frequencyData[i] / i);

			}

			WaveDisplay frequency = new WaveDisplay(frequencyData, 0.02, 300);
			// System.out.println(Arrays.toString(frequencyData));
		} catch (IllegalArgumentException e) {
			System.out.println(e);
		}

	}

	private static boolean isLocalMax(double[] frequencyData, int i) {

		if (i - 1 < 0 || i + 1 >= frequencyData.length)
			return false;

		double left = frequencyData[i - 1];
		double mid = frequencyData[i];
		double right = frequencyData[i + 1];

		if (right < mid && mid > left)
			return true;

		return false;
	}

	private static double[] generateWave(Double rate, double... freqMagPhase) {
		double[] data = new double[TWO_POWER];

		for (int fm = 0; fm < freqMagPhase.length; fm += 3) {
			double frequency = freqMagPhase[fm];
			double magnitude = freqMagPhase[fm + 1];
			double phase = freqMagPhase[fm + 2];
			for (int t = 0; t < TWO_POWER; t++) {
				data[t] = data[t] + magnitude * Math.sin(frequency * t + phase)
						+ rate * t;
			}
		}

		return data;
	}

}
