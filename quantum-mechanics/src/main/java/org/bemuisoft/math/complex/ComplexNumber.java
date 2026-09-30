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

/**
 * This class provides a non-mutable implementation of a complex number.
 * Therefore, it can safely be shared by different objects
 * which require full control over the complex number part values.
 * 
 * @author Benno Muilwijk
 */
public final class ComplexNumber extends Complex {

	/**
	 * Constructs a complex number with the specified value {@code x+yi}.
	 * 
	 * @param realPart		x
	 * @param imaginaryPart	y
	 */
	public ComplexNumber(double realPart, double imaginaryPart) {
		super(realPart, imaginaryPart);
	}

	/**
	 * Constructs a complex number with the specified complex value.
	 * 
	 * @param z	the complex value
	 */
	public ComplexNumber(Complex z) {
		super(z);
	}

	/**
	 * Constructs a complex number with the specified value.
	 * 
	 * @param x	the value
	 */
	public ComplexNumber(Number x) {
		super(x);
	}

	/**
	 * Returns the sum of this complex number and a complex value.
	 * 
	 * @param z	the complex value to add
	 * @return	complex number this + z
	 * @see		#sum(Complex, Complex)
	 */
	@Override
	public ComplexNumber add(Complex z) {
		return sum(this, z);
	}

	/**
	 * Returns the difference between this complex number and a complex value.
	 * 
	 * @param z	the complex value to subtract
	 * @return	complex number this - z
	 * @see		#difference(Complex, Complex)
	 */
	@Override
	public ComplexNumber subtract(Complex z) {
		return difference(this, z);
	}

	/**
	 * Returns the product of this complex number and a complex value.
	 * 
	 * @param z	the complex value to multiply by
	 * @return	complex number this * z
	 * @see		#product(Complex, Complex)
	 */
	@Override
	public ComplexNumber multiply(Complex z) {
		return product(this, z);
	}

	/**
	 * Returns the quotient of this complex number and a complex value.
	 * 
	 * @param z	the complex value to divide by
	 * @return	complex number this / z
	 * @see		#quotient(Complex, Complex)
	 */
	@Override
	public ComplexNumber divide(Complex z) {
		return quotient(this, z);
	}

	/**
	 * Returns a complex number with the normalized value
	 * of this complex number.
	 * <p>
	 * If the norm is equal or close to zero,
	 * complex number one is returned.
	 * Otherwise the value of this complex number
	 * is divided by its own norm.
	 * 
	 * @return	complex number one or (this / absValue())
	 */
	@Override
	public ComplexNumber normalize() {
		double norm = absValue();
		if (norm == 1.0) {
			return this;
		}
		Complex n = new Complex(this).normalize();
		return new ComplexNumber(n);
	}

	@Override
	public ComplexNumber conjugate() {
		if (im == 0.0) {
			return this;
		}
		return new ComplexNumber(re, -im);
	}

	//////////////////
	// Static methods
	//////////////////

	/**
	 * Returns the sum of two complex values.
	 * 
	 * @param a	complex value a
	 * @param b	complex value b
	 * @return	complex number a + b
	 */
	public static ComplexNumber sum(Complex a, Complex b) {
		return new ComplexNumber(a.copy().add(b));
	}

	/**
	 * Returns the difference between two complex values.
	 * 
	 * @param a	complex value a
	 * @param b	complex value b
	 * @return	complex number a - b
	 */
	public static ComplexNumber difference(Complex a, Complex b) {
		return new ComplexNumber(a.copy().subtract(b));
	}

	/**
	 * Returns the product of two complex values.
	 * 
	 * @param a	complex value a
	 * @param b	complex value b
	 * @return	complex number a * b
	 */
	public static ComplexNumber product(Complex a, Complex b) {
		return new ComplexNumber(a.copy().multiply(b));
	}

	/**
	 * Returns the quotient of two complex values.
	 * 
	 * @param a	complex value a
	 * @param b	complex value b
	 * @return	complex number a / b
	 */
	public static ComplexNumber quotient(Complex a, Complex b) {
		return new ComplexNumber(a.copy().divide(b));
	}

}
