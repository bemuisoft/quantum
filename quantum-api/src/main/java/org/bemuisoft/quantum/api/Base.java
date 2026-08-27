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

package org.bemuisoft.quantum.api;

import java.util.Locale;

/**
 * A base interface with common constants and default methods.
 * 
 * @author Benno Muilwijk
 */
public interface Base {

	// some useful constants
	/** π */
	public static final double PI = Math.PI;
	/** 2π */
	public static final double TWO_PI = 2.0 * PI;
	/** π/2 */
	public static final double HALF_PI = 0.5 * PI;
	/** π/4 */
	public static final double QUARTER_PI = 0.25 * PI;
	
	/** √(1/2) = 1/√2 */
	public static final double SQRT_HALF = Math.sqrt(0.5);

	/**
	 * Throws an {@code IllegalArgumentException} if and only if
	 * the specified condition is false.
	 * <p>
	 * The condition is usually specified like an {@code if} condition,
	 * for example:<br/>
	 * {@code check(value != null, "value must not be null.");}
	 * 
	 * @param condition	- the required condition to pass 
	 * @param message	- the exception message to use
	 * @throws IllegalArgumentException when {@code condition} is false
	 */
	public default void check(boolean condition, String message) {
		if (!condition) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Throws an {@code IllegalStateException} if and only if
	 * the specified condition is false.
	 * <p>
	 * The condition is usually specified like an {@code if} condition,
	 * for example:<br/>
	 * {@code check(value != null, "value must not be null.");}
	 * 
	 * @param condition	- the required condition to pass
	 * @param message	- the exception message to use
	 * @throws IllegalStateException when {@code condition} is false
	 */
	public default void checkState(boolean condition, String message) {
		if (!condition) {
			throw new IllegalStateException(message);
		}
	}

	/**
	 * Logs the specified message.
	 * 
	 * @param msg	- the message to log
	 */
	public default void log(String msg) {
		System.out.println(msg);
	}

	/**
	 * Answers whether the values a and b differ less than
	 * the specified margin
	 * 
	 * @param a			- value a
	 * @param b			- value b
	 * @param margin	- the margin
	 * @return	true if abs(a - b) &lt; margin, false otherwise
	 */
	public default boolean isClose(double a, double b, double margin) {
		return Math.abs(a - b) < margin;
	}

	/**
	 * Returns a rounded value.
	 * <p>
	 * Default implementation is to return the value
	 * rounded to 15 decimal places.
	 * 
	 * @param value	- the value to round
	 * @return	the rounded value
	 */
	public default double round(double value) {
		return round(value, 15);
	}

	/**
	 * Returns a value rounded to the specified number
	 * of decimal places.
	 * 
	 * @param value	 - the value to round
	 * @param places - the number of decimal places
	 * @return	the rounded value
	 */
	public default double round(double value, int places) {
		double scale = Math.pow(10, places);
		if (value < 0.0) scale = -scale;
		return Math.round(value * scale) / scale;
//		BigDecimal bd = new BigDecimal(Double.toString(value));
//	    bd = bd.setScale(places, RoundingMode.HALF_UP);
//	    return bd.doubleValue();
	}

	/**
	 * Returns 0.0 when the given value is close to 0.0.
	 * Returns 1.0 when the given value is close to or greater than 1.0.
	 * Returns -1.0 when the given value is close to or less than -1.0.
	 * Otherwise the given value is returned.
	 * 
	 * @param v	- the value to round
	 * @return	the rounded value
	 */
	public default double roundCos(double v) {
		// Math.cos(1.05e-8) gives 1.0
		// Math.cos(1.06e-8) gives 0.9999999999999999 == 1.0 - 1e-16
		// Math.cos(1.82e-8) gives 0.9999999999999999 == 1.0 - 1e-16
		// Math.cos(1.83e-8) gives 0.9999999999999998 == 1.0 - 2e-16
		// Math.cos(2.35e-8) gives 0.9999999999999998 == 1.0 - 2e-16
		// Math.cos(2.36e-8) gives 0.9999999999999997 == 1.0 - 3e-16
		// Math.acos(1.0 - 0.55e-16) gives 0.0
		// Math.acos(1.0 - 0.56e-16) gives 1.4901161193847656E-8
		// Math.acos(1.0 - 1.66e-16) gives 1.4901161193847656E-8
		// Math.acos(1.0 - 1.67e-16) gives 2.1073424255447017E-8
		// Math.acos(1.0 - 2.77e-16) gives 2.1073424255447017E-8
		// Math.acos(1.0 - 2.78e-16) gives 2.580956827951785E-8
		double margin = 2e-16;
		if (v >= 1.0 - margin) return 1.0;
		if (v <= -1.0 + margin) return -1.0;
		margin = 2e-8;
		if (v == 0.0 || v > margin || v < -margin) {
			return v;
		}
		return 0.0 * v;
	}

	/**
	 * Returns a value rounded in a "smart" way.
	 * <p>
	 * Default implementation is to repeat specific patterns
	 * in decimals places 9-12 into 13-16.
	 * For example:
	 * <ul>
	 * <li>0.0625000000001234 would be rounded as 0.0625000000000000</li>
	 * <li>0.0833333333331234 would be rounded as 0.0833333333333333</li>
	 * <li>0.0123456666666789 would be rounded as 0.0123456666666667</li>
	 * </ul>
	 * 
	 * @param value	- the value to round
	 * @return	the rounded value
	 */
	public default double roundSmart(double value) {
		double v = Math.round(value);
		if (v == value) {
			return value;
		}
		String s16 = String.format(Locale.US, "%.16f", value);
		String s12 = String.format(Locale.US, "%.12f", value);
		String last4 = s12.substring(s12.length() - 4);
		if (s16.endsWith(last4)) {
			return value;
		}
		if (last4.equals("0000")) {
			return Double.parseDouble(s12);
		}
		if (last4.equals("3333")) {
			return Double.parseDouble(s12) + Math.signum(v) * 1e-12 / 3.0;
		}
		if (last4.equals("6667")) {
			return Double.parseDouble(s12) - Math.signum(v) * 1e-12 / 3.0;
		}
		return value;
	}

	/**
	 * Returns an angle in radians mod 2π.
	 * <p>
	 * Default implementation returns the
	 * angle in the range &lt;-π, +π]
	 * if the input value is in the range
	 * &lt;-3π, +3π].
	 * 
	 * @param rad - the angle in radians
	 * @return		the angle in radians mod 2π
	 */
	public default double mod2Pi(double rad) {
		if (rad > PI) {
			rad -= TWO_PI;
		} else if (rad <= -PI) {
			rad += TWO_PI;
		}
		return rad;
	}

	/**
	 * Returns a String representation of
	 * an angle in radians in units of π.
	 * <p>
	 * Default implementation returns the
	 * fraction with max 5 decimal places.
	 * For example:
	 * <ul>
	 * <li>PI/2 would be returned as "0.5π"</li>
	 * <li>PI/3 would be returned as "0.33333π"</li>
	 * </ul>
	 * 
	 * @param radians - the angle in radians
	 * @return			the String representation of the angle
	 */
	public default String toPi(double radians) {
		return Double.toString(round(radians/PI, 5)) + 'π';
	}

	/**
	 * Returns a random value in the range [-1, +1].
	 * 
	 * @return	a random value
	 */
	public default double randomCos() {
		return Math.random() * 2.0 - 1.0;
	}

}
