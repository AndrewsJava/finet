package ffttest;

import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FFT {
	private int power2Size = 2;
	double[] dataToTransform;
	double[] transformedData;
	TreeMap<Double, Long> frequencies = new TreeMap<Double, Long>();

	public FFT(double[] timeData) {
		while (power2Size < timeData.length) {
			power2Size <<= 1;
		}
		System.out.println("arraysize:  " + timeData.length);
		System.out.println("pow2 size: " + power2Size);

		this.dataToTransform = new double[power2Size];
		createZeroFilledArrayForFFT(timeData);
		transformedData = computeTransform();
		if (transformedData == null) {
			System.out.println("TRANSFORM FAILURE ");
			return;
		}
	}
	static double[] toDoubleArray(float[] arr) {
		  if (arr == null) return null;
		  int n = arr.length;
		  double[] ret = new double[n];
		  for (int i = 0; i < n; i++) {
		    ret[i] = (double)arr[i];
		  }
		  return ret;
		}
	public void showFrequencyGraph() {

		HorizontalGraph showWave = new HorizontalGraph(transformedData);
		showWave.setOffset(300);
		JFrame display = new JFrame();
		JScrollPane jsp = HorizontalGraph.makeJScrollPane(showWave);
		//display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		display.add(jsp);
		display.setVisible(true);
		display.setExtendedState(JFrame.MAXIMIZED_BOTH);

	}

	private void createZeroFilledArrayForFFT(double[] timeData) { 
		System.arraycopy(timeData, 0, dataToTransform, 0, timeData.length); 
	}

	private double[] computeTransform() {
		double[] frequencyData = null;
		FastFourierTransformer transformer = new FastFourierTransformer(
				DftNormalization.STANDARD);
		try {
			Complex[] complx = transformer.transform(dataToTransform,
					TransformType.FORWARD);
			frequencyData = new double[complx.length];

			System.out.print("--->");
			for (int i = 0; i < complx.length / 2; i++) {
				double rr = (complx[i].getReal());
				double ri = (complx[i].getImaginary());

				frequencyData[i] = Math.sqrt((rr * rr) + (ri * ri));
				if (isLocalMax(frequencyData, i - 1)) {
					// System.out.println("----->" + i);
					long frequency = (long) (2 * Math.PI * i / (power2Size * 1f));
					double magnitude = frequencyData[i];
					frequencies.put(magnitude, frequency);
					// System.out.print(", " + (2 * Math.PI * i / (power2Size *
					// 1f)));
				}
				// int freqCoeff = (int) (TWO_POWER * frequencyData[i] / i);

			}

			WaveDisplay frequency = new WaveDisplay(frequencyData, 0.02, 300);
			// System.out.println(Arrays.toString(frequencyData));
		} catch (IllegalArgumentException e) {
			System.out.println(e);
		}
		return frequencyData;
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

	public static void main(String arg[]) {
//		for (int i = 0; i < 20; i++) {
//			int random = (int) (100 * Math.random());
//			double[] f = new double[random];
//			new FFT(f);
//		}
	}
}
