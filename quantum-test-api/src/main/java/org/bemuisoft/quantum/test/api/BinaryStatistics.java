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
 * different binary values have been measured.
 * <p>
 * Measurement values are expected to be in the range [0, 2^n - 1],
 * where n is the number of binary digits.
 * 
 * @author Benno Muilwijk
 */
public class BinaryStatistics extends BasicStatistics {

	private String format;

	/**
	 * Main method.
	 * <p>
	 * Serves as an example how this class can be used.
	 * 
	 * @param args	command line arguments (ignored)
	 */
	public static void main(String[] args) {
		BinaryStatistics test = new BinaryStatistics(3);
		test.addMeasurement(0b100);
		test.addMeasurement(0b011);
		test.addMeasurement(0b100);
		test.printResults();
	}

	/**
	 * Default constructor.
	 * <p>
	 * Returns a new instance for values of (max) 5 binary digits,
	 * thus with a valid range of [0-31] or binary [00000-11111].
	 */
	public BinaryStatistics() {
		this(5);
	}

	/**
	 * Constructor for n binary digits.
	 * 
	 * @param n	maximum number of binary digits in the measurement value
	 */
	public BinaryStatistics(int n) {
		super(1<<n);					// 1 << n == 1 * Math.pow(2, n)
		format = "%" + n + "s";
	}

	/**
	 * Formats the given measurement value as
	 * a binary string with leading zeroes.
	 * 
	 * @param m	the measurement value to format
	 * @return	the formatted binary number
	 */
	@Override
	protected String format(int m) {
		return String.format(format, Integer.toBinaryString(m)).replace(' ', '0');
	}

}
