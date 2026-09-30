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
package org.bemuisoft.math.complex;

import org.bemuisoft.quantum.api.Base;

/**
  * A base class with common constants and methods
  * related to complex numbers.
  * 
  * @author Benno Muilwijk
 */
public class ComplexBase implements Base {

	/** Complex number zero. */
	public static final ComplexNumber ZERO = c(0.0);
	/** Complex number one. */
	public static final ComplexNumber ONE = c(1.0);

	/**
	 * Returns a {@link Complex} value (cv) with the same value as {@code x}.
	 * <p>
	 * If {@code x} is a {@code Complex} object, it is returned.
	 * Otherwise a new {@code Complex} object is returned with the same value.
	 * 
	 * @param x		the value
	 * @return		a {@link Complex} object with value {@code x}
	 * @see			Complex
	 */
	public static Complex cv(Number x) {
		// do not force a copy in case x is Complex
		if (x instanceof Complex) {
			return (Complex) x;
		}
		return new Complex(x);
	}

	/**
	 * Returns a copy of a {@link Complex} value (cc) with the same value as {@code x}.
	 * <p>
	 * If {@code x} is a {@code ComplexNumber}, it is returned.
	 * Otherwise a new {@code Complex} object is returned with the same value.
	 * <p>
	 * Note: {@code ComplexNumber} objects are not copied because
	 * they are not modifiable (and can thus be shared safely).
	 * Other non-modifiable numeric types like {@code Double} must be copied anyway,
	 * because they cannot be cast to {@code Complex}.
	 * 
	 * @param x		the value to copy
	 * @return		a {@link Complex} object with value {@code x}
	 * @see			Complex
	 * @see			ComplexNumber
	 */
	public static Complex cc(Number x) {
		// force a copy in case x is Complex, but not a ComplexNumber
		if (x instanceof ComplexNumber) {
			return (Complex) x;
		}
		return new Complex(x);
	}

	/**
	 * Returns a {@link ComplexNumber} with the same value as {@code x}.
	 * <p>
	 * If {@code x} is a {@code ComplexNumber}, it is returned.
	 * Otherwise a new {@code ComplexNumber} is returned with the same value.
	 * 
	 * @param x		the value
	 * @return		a {@link ComplexNumber} with value {@code x}
	 * @see			ComplexNumber
	 */
	public static ComplexNumber c(Number x) {
		if (x instanceof ComplexNumber) {
			return (ComplexNumber) x;
		}
		return new ComplexNumber(x);
	}

	/**
	 * Returns a {@link ComplexNumber} with the real value {@code x}.
	 * <p>
	 * The value {@code x} is used as the real part
	 * and the imaginary part is set to zero.
	 * 
	 * @param x		the real value
	 * @return		a {@link ComplexNumber} with value {@code x}
	 */
	public static ComplexNumber c(double x) {
		return new ComplexNumber(x, 0.0);
	}

	/**
	 * Returns a {@link ComplexNumber} with the complex value {@code x+yi}.
	 * <p>
	 * The value {@code x} is used as the real part
	 * and {@code y} is used as the imaginary part.
	 * 
	 * @param x		the real value
	 * @param y		the imaginary value
	 * @return		a {@link ComplexNumber} with value {@code x+yi}
	 */
	public static ComplexNumber c(double x, double y) {
		return new ComplexNumber(x, y);
	}

	/**
	 * Returns a {@link ComplexNumber} with the value {@code r*e^(i*phase)}.
	 * <p>
	 * The real part is set to {@code r*cos(phase)} and
	 * the imaginary part is set to {@code r*sin(phase)}.
	 * 
	 * @param r		the complex magnitude
	 * @param phase	the complex phase
	 * @return		a {@link ComplexNumber} with value {@code r*e^(i*phase)}
	 */
	public static ComplexNumber e(double r, double phase) {
		return new ComplexNumber(r*Math.cos(phase), r*Math.sin(phase));
	}

	/**
	 * Returns a {@link ComplexNumber} with the value {@code e^(i*phase)}.
	 * <p>
	 * The real part is set to {@code cos(phase)} and
	 * the imaginary part is set to {@code sin(phase)}.
	 * 
	 * @param phase	the complex phase
	 * @return		a {@link ComplexNumber} with value {@code e^(i*phase)}
	 */
	public static ComplexNumber ei(double phase) {
		return new ComplexNumber(Math.cos(phase), Math.sin(phase));
	}

	/**
	 * Returns a {@link ComplexNumber} with the imaginary value {@code yi}.
	 * <p>
	 * The value {@code y} is used as the imaginary part
	 * and the real part is set to zero.
	 * 
	 * @param y		the imaginary value
	 * @return		a {@link ComplexNumber} with value {@code yi}
	 */
	public static ComplexNumber i(double y) {
		return new ComplexNumber(0.0, y);
	}

	/**
	 * Returns the given numbers as an array.
	 * <p>
	 * Such an array can be used as row values
	 * when initializing a {@link Matrix}.
	 * 
	 * @param values	input values
	 * @return			array with input values
	 */
	public static Number[] row(Number... values) {
		return values;
	}

	/**
	 * Returns a {@link ComplexNumber} with both the real
	 * and imaginary part rounded to 15 decimal places.
	 * 
	 * @param z			the complex value to round
	 * @return			the rounded complex value
	 */
	public ComplexNumber round(Complex z) {
		return round(z, 15);
	}

	/**
	 * Returns a {@link ComplexNumber} with both the real and imaginary part
	 * rounded to the specified number of decimal places.
	 * 
	 * @param z			the complex value to round
	 * @param places	the number of decimal places
	 * @return			the rounded complex value
	 */
	public ComplexNumber round(Complex z, int places) {
		return new ComplexNumber(round(z.realPart(), places), round(z.imaginaryPart(), places));
	}

	/**
	 * Returns a complex number as {@code String}.
	 * 
	 * @param z	the complex number
	 * @return	String representation of {@code z}
	 */
	public String asString(ComplexNumber z) {
		final double re = round(z.realPart());
		final double im = round(z.imaginaryPart());
		if (im == 0.0) {
			return Double.toString(re);
		}
		StringBuilder sb = new StringBuilder();
		if (re != 0.0) {
			sb.append(re);
			if (im >= 0.0) {
				sb.append('+');
			}
		}
		sb.append(im).append('i');
		return sb.toString();
	}

	/**
	 * Returns a complex number array as {@code String}.
	 * 
	 * @param row	the complex number array
	 * @return		String representation of the {@code row}
	 */
	public String asString(ComplexNumber[] row) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (int i = 0; i < row.length; i++) {
			if (i != 0) sb.append(", ");
			sb.append(asString(row[i]));
		}
		sb.append(')');
		return sb.toString();
	}

}
