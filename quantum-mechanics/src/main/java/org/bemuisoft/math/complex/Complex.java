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
 * This class provides a mutable implementation of a complex value.
 * Therefore, it is <b>not safe</b> to have it shared by different objects
 * which require full control over the complex number part values.
 * But it works more efficient in complex calculations, because
 * no new objects need to be instantiated for intermediate results.
 * 
 * @author Benno Muilwijk
 */
public class Complex extends Number {

	/** Real part. */
	protected double re;
	/** Imaginary part. */
	protected double im;

	/**
	 * Constructs a complex value with default initial value zero.
	 */
	public Complex() {
	}

	/**
	 * Constructs a complex value with the specified initial value {@code x+yi}.
	 * 
	 * @param realPart		initial x
	 * @param imaginaryPart	initial y
	 */
	public Complex(double realPart, double imaginaryPart) {
		this.re = realPart;
		this.im = imaginaryPart;
	}

	/**
	 * Constructs a complex value with the specified initial value.
	 * 
	 * @param z	initial complex value
	 */
	public Complex(Complex z) {
		this(z.re, z.im);
	}

	/**
	 * Constructs a complex value with the specified initial value.
	 * 
	 * @param x	initial value
	 */
	public Complex(Number x) {
		if (x instanceof Complex) {
			Complex z = (Complex) x;
			this.re = z.re;
			this.im = z.im;
		} else {
			this.re = x.doubleValue();
			this.im = 0.0;
		}
	}

	/**
	 * Returns the real part of this complex value.
	 * 
	 * @return	the real part
	 */
	public double realPart() {
		return re;
	}

	/**
	 * Returns the imaginary part of this complex value.
	 * 
	 * @return	the imaginary part
	 */
	public double imaginaryPart() {
		return im;
	}

	/**
	 * Returns the square of the absolute value
	 * of this complex value.
	 * 
	 * @return	the absolute value squared
	 * @see		#absValue()
	 */
	public double abs2() {
		return re*re + im*im;
	}

	/**
	 * Returns the absolute value of this complex value.
	 * <p>
	 * This value is also referred to as norm or magnitude.
	 * 
	 * @return	the absolute value
	 */
	public double absValue() {
		return Math.hypot(re, im);
	}

	/**
	 * Returns the magnitude of this complex value.
	 * <p>
	 * It is the same as the norm or absolute value, but
	 * has been added for the convenience of physicists.
	 * 
	 * @return	the magnitude
	 */
	public double magnitude() {
		return Math.hypot(re, im);
	}

	/**
	 * Returns the phase of this complex value.
	 * 
	 * @return	the phase
	 */
	public double phase() {
		double x = re;
		double y = im;
		if (Math.abs(x) < 1e-15) x = 0;
		if (Math.abs(y) < 1e-15) y = 0;
		return Math.atan2(y, x);
	}

	/**
	 * Adds a complex value to this complex value.
	 * <p>
	 * This works like the Java += operator,
	 * so the original value is overwritten.
	 * 
	 * @param z	the complex value to add
	 * @return	this complex value after update
	 */
	public Complex add(Complex z) {
		this.re += z.re;
		this.im += z.im;
		return this;
	}

	/**
	 * Subtracts a complex value from this complex value.
	 * <p>
	 * This works like the Java -= operator,
	 * so the original value is overwritten.
	 * 
	 * @param z	the complex value to subtract
	 * @return	this complex value after update
	 */
	public Complex subtract(Complex z) {
		this.re -= z.re;
		this.im -= z.im;
		return this;
	}

	/**
	 * Multiplies this complex value by the given value.
	 * <p>
	 * This works like the Java *= operator,
	 * so the original value is overwritten.
	 * 
	 * @param z	the complex value to multiply by
	 * @return	this complex value after update
	 */
	public Complex multiply(Complex z) {
		final double re = this.re;
		final double im = this.im;
		this.re = re*z.re - im*z.im;
		this.im = re*z.im + im*z.re;
		return this;
	}

	/**
	 * Divides this complex value by the given value.
	 * <p>
	 * This works like the Java /= operator,
	 * so the original value is overwritten.
	 * 
	 * @param z	the complex value to divide by
	 * @return	this complex value after update
	 */
	public Complex divide(Complex z) {
		final double re = this.re;
		final double im = this.im;
		double norm2 = z.abs2();
		this.re = (re*z.re + im*z.im) / norm2;
		this.im = (im*z.re - re*z.im) / norm2;
		return this;
	}

	/**
	 * Normalizes this complex value.
	 * <p>
	 * If the norm is equal or close to zero,
	 * this complex value is set to one.
	 * Otherwise it is divided by its own norm.
	 * 
	 * @return	this complex value after update
	 */
	public Complex normalize() {
		double norm = absValue();
		if (norm < 1e-8) {
			re = 1.0;
			im = 0.0;
		} else {
			re /= norm;
			im /= norm;
		}
		return this;
	}

	/**
	 * Returns a copy of this complex value.
	 * 
	 * @return	a copy
	 */
	public final Complex copy() {
		return new Complex(re, im);
	}

	/**
	 * Returns the complex conjugate of this complex value.
	 * <p>
	 * This complex value is <b>not</b> modified by this operation.
	 * 
	 * @return	the complex conjugate value
	 */
	public Complex conjugate() {
		return new Complex(re, -im);
	}

	/**
	 * Answers whether this complex value is (almost) equal
	 * to the given complex value.
	 * <p>
	 * This is considered to be the case if the absolute value
	 * of the difference between this and the specified value
	 * is less than 1e-8 (0.00000001).
	 * 
	 * @param z	the complex value to compare to
	 * @return	{@code true} if this complex value is close to z, {@code false} otherwise
	 * @see		#isCloseTo(Complex, double)
	 */
	public boolean isCloseTo(Complex z) {
		return isCloseTo(z, 1e-8);
	}

	/**
	 * Answers whether this complex value is (almost) equal
	 * to the given complex value.
	 * <p>
	 * This is considered to be the case if the absolute value
	 * of the difference between this and the specified value
	 * is less than the specified margin.
	 * 
	 * @param z			the complex value to compare to
	 * @param margin	the margin
	 * @return			{@code true} if this complex value is close to z, {@code false} otherwise
	 * @see				#isCloseTo(Complex)
	 */
	public boolean isCloseTo(Complex z, double margin) {
		return (Math.hypot(re - z.re, im - z.im) < margin);
	}

	/**
	 * Returns this complex value as {@code String}.
	 * 
	 * @return	String representation of this complex value
	 */
	public String asString() {
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

	@Override
	public String toString() {
		double r = absValue();
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [r=").append(r);
		if (r != 0.0) {
			sb.append(", φ=").append(phase()/Math.PI).append('π');
			sb.append(", z=").append(this.asString());
		}
		sb.append("]");
		return sb.toString();
	}

	//////////////////
	// Number methods
	//////////////////

	/**
	 * Returns the real part of this complex value as a {@code double}.
	 *
	 * @return	the real part as {@code double}.
	 */
	@Override
	public double doubleValue() {
		return realPart();
	}

	/**
	 * Returns the real part of this complex value as a {@code float}.
	 *
	 * @return	the real part cast to {@code float}.
	 */
	@Override
	public float floatValue() {
		return (float) doubleValue();
	}

	/**
	 * Returns the real part of this complex value as a {@code long}.
	 *
	 * @return	the real part cast to {@code long}.
	 */
	@Override
	public long longValue() {
		return (long) doubleValue();
	}

	/**
	 * Returns the real part of this complex value as an {@code int}.
	 *
	 * @return	the real part cast to {@code int}.
	 */
	@Override
	public int intValue() {
		return (int) doubleValue();
	}

	//////////////////
	// Static methods
	//////////////////

	/**
	 * Returns the sum of two complex values.
	 * 
	 * @param a	complex value a
	 * @param b	complex value b
	 * @return	complex value a + b
	 */
	public static Complex sum(Complex a, Complex b) {
		return a.copy().add(b);
	}

	/**
	 * Returns the difference between two complex values.
	 * 
	 * @param a	complex value a
	 * @param b	complex value b
	 * @return	complex value a - b
	 */
	public static Complex difference(Complex a, Complex b) {
		return a.copy().subtract(b);
	}

	/**
	 * Returns the product of two complex values.
	 * 
	 * @param a	complex value a
	 * @param b	complex value b
	 * @return	complex value a * b
	 */
	public static Complex product(Complex a, Complex b) {
		return a.copy().multiply(b);
	}

	/**
	 * Returns the quotient of two complex values.
	 * 
	 * @param a	complex value a
	 * @param b	complex value b
	 * @return	complex value a / b
	 */
	public static Complex quotient(Complex a, Complex b) {
		return a.copy().divide(b);
	}

}
