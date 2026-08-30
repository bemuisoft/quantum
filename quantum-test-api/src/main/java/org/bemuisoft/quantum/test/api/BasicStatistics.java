/*
Copyright 2026 Benno Muilwijk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.bemuisoft.quantum.test.api;

/**
 * A simple tool that keeps track of how many times
 * different values have been measured.
 * <p>
 * Measurement values are expected to be in the range [0, n-1],
 * where n is the maximum number of values.
 * Actual measurement values must be translated to a value
 * in this range, if needed.
 * <p>
 * For example, the ages of a group of people can be divided
 * into a number of ranges.
 * Then each "measurement value" identifies a defined range:<pre>
 * Value	age
 *   0		&lt;18
 *   1		19-29
 *   2		30-39
 *   3		40-49
 *   4		50+
 * </pre>
 * 
 * @author Benno Muilwijk
 */
public class BasicStatistics {

	private int[] measurements;
	private int total;

	/**
	 * Main method.
	 * <p>
	 * Serves as an example how this class can be used.
	 * 
	 * @param args	command line arguments (ignored)
	 */
	public static void main(String[] args) {
		BasicStatistics test = new BasicStatistics(10);
		test.addMeasurement(5);
		test.addMeasurement(4);
		test.addMeasurement(5);
		test.printResults();
	}

	/**
	 * Constructor.
	 * 
	 * @param n	maximum number of values that can be measured
	 */
	public BasicStatistics(int n) {
		measurements = new int[n];
	}

	/**
	 * Increments the count of the given
	 * measurement value.
	 * 
	 * @param m	the measurement value
	 */
	public void addMeasurement(int m) {
		measurements[m] += 1;
		total += 1;
	}

	/**
	 * Returns the total number of measurements.
	 * 
	 * @return	total number of measurements
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * Returns the total count for the specified measurement value.
	 * This is the number of times this value was measured.
	 * 
	 * @param m	the measurement value
	 * @return	the total count for the measurement value
	 */
	public int getTotal(int m) {
		return measurements[m];
	}

	/**
	 * Resets all measurement counts to zero.
	 */
	public void reset() {
		for (int i = 0; i < measurements.length; i++) {
			measurements[i] = 0;
		}
		total = 0;
	}

	/**
	 * Prints the percentages for all possible measurement values
	 * to {@code System.out}.
	 */
	public void printResults() {
		double p;
		for (int m = 0; m < measurements.length; m++) {
			p = (total == 0) ? 0.0 : measurements[m] * 100.0 / total;
			System.out.println(format(m) + '\t' + String.format("%5.1f", p) + '%');
		}
	}

	/**
	 * Returns the given number as a String of
	 * (at least) 4 characters.
	 * If the value is less than 1000,
	 * the number is right-justified.
	 * 
	 * @param m	the number to format
	 * @return	the formatted number
	 */
	protected String format(int m) {
		return String.format("%4s", m);
	}

}
