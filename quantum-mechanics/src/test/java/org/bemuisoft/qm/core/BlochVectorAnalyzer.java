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
package org.bemuisoft.qm.core;

import static org.bemuisoft.math.complex.ComplexBase.ZERO;
import static org.bemuisoft.math.complex.ComplexBase.c;

import org.bemuisoft.math.complex.ComplexNumber;
import org.bemuisoft.math.complex.Matrix;

/**
 * Helper class for extended analysis of Bloch vectors,
 * used by several test classes.
 * 
 * @author Benno Muilwijk
 */
public class BlochVectorAnalyzer extends SimpleBlochVector {

	public BlochVectorAnalyzer() {
		super();
	}

	public BlochVectorAnalyzer(String label) {
		super(label);
	}

	public BlochVectorAnalyzer(SimpleBlochVector v) {
		super(v.getLabel());
		add(v);
	}

	public BlochVectorAnalyzer(String label, SimpleBlochVector v) {
		super(label);
		add(v);
	}

	public BlochVectorAnalyzer(String label, Matrix rdm) {
		super(label);
		set(rdm);
	}

	public BlochVectorAnalyzer(String label, double x, double y, double z) {
		super(label);
		set(x, y, z);
	}

	@Override
	public BlochVectorAnalyzer add(SimpleBlochVector v) {
		super.add(v);
		return this;
	}

	public BlochVectorAnalyzer subtract(SimpleBlochVector v) {
		super.set(getX() - v.getX(), getY() - v.getY(), getZ() - v.getZ());
		return this;
	}

	public BlochVectorAnalyzer scale(double factor) {
		// divides length by the given factor
		// so that scaling by its own length gives a unit vector
		super.set(getX()/factor, getY()/factor, getZ()/factor);
		return this;
	}

	public BlochVectorAnalyzer rx(double radians) {
		if (radians != 0.0) {
			final double cos = Math.cos(radians);
			final double sin = Math.sin(radians);
			final double x = getX();
			final double y = getY();
			final double z = getZ();
			// set new values for y and z
			set(x, roundCos(y*cos - z*sin), roundCos(z*cos + y*sin));
		}
		return this;
	}

	public BlochVectorAnalyzer ry(double radians) {
		if (radians != 0.0) {
			final double cos = Math.cos(radians);
			final double sin = Math.sin(radians);
			final double x = getX();
			final double y = getY();
			final double z = getZ();
			// set new values for x and z
			set(roundCos(x*cos + z*sin), y, roundCos(z*cos - x*sin));
		}
		return this;
	}

	public BlochVectorAnalyzer rz(double radians) {
		if (radians != 0.0) {
			final double cos = Math.cos(radians);
			final double sin = Math.sin(radians);
			final double x = getX();
			final double y = getY();
			final double z = getZ();
			// set new values for x and y
			set(round(x*cos - y*sin), round(y*cos + x*sin), z);
		}
		return this;
	}

	public Matrix densityMatrix() {
		Matrix rho = Matrix.create(2, 2);
		final double x = getX();
		final double y = getY();
		final double z = getZ();
		rho.init(0, 0, (1.0 + z) / 2.0);
		rho.init(1, 1, (1.0 - z) / 2.0);
		rho.init(1, 0, c(x/2.0,  y/2.0));
		rho.init(0, 1, c(x/2.0, -y/2.0));
		return rho;
	}

	public double eigenvalue(int i) {
		switch (i) {
			case 0:
				return eigenvalue0();
			case 1:
				return 1.0 - eigenvalue0();
			default:
				throw new IllegalArgumentException("Invalid index for eigenvalue");
		}
	}

	private double eigenvalue0() {
		// determinant det =
		// (1+z)*(1-z)/4 - (x+yi)*(x-yi)/4 =
		// (1 - z²)/4 - (x² + y²)/4 =
		// (1 - r²)/4
		// 1/4 - det = r²/4
		// ev = 0.5 + sqrt(1/4 - det) = 0.5 + r/2 = (1+r)/2
		return (1.0 + length()) / 2.0;
	}

	public ComplexNumber[] eigenvector(int i) {
		switch (i) {
			case 0:
				return eigenvector(length());
			case 1:
				return eigenvector(-length());
			default:
				throw new IllegalArgumentException("Invalid index for eigenvector");
		}
	}

	private ComplexNumber[] eigenvector(double r) {
		ComplexNumber[] v = new ComplexNumber[2];
		if (Math.abs(r) < 1e-8) {
			v[0] = ZERO;
			v[1] = ZERO;
			return v;
		}
		final double x = getX();
		final double y = getY();
		final double z = getZ();
		if (r + z != 0.0) {
			final double norm = r * Math.sqrt(2.0 * (1.0 + z/r));		// signed norm, so that v[0] is never negative
			v[0] = c((r + z) / norm);
			v[1] = c(x / norm, y / norm);
		} else {
			final double norm = r * Math.sqrt(2.0 * (1.0 - z/r));		// signed norm, so that v[1] is never negative
			v[0] = c(x / norm, -y / norm);								// v[0] should always be 0 in this case (r + z == 0)
			v[1] = c((r - z) / norm);
		}
		return v;
	}

	public BlochVectorAnalyzer set(double eigenvalue, ComplexNumber[] eigenvector) {
		check(eigenvector.length == 2, "eigenvector must have length 2");
		double r = eigenvalue * 2.0 - 1.0;
		double v0 = eigenvector[0].abs2();
		double v1 = eigenvector[1].abs2();
		double cosTheta = (v0 - v1) / (v0 + v1);
		double sinTheta = Math.sqrt(1 - cosTheta*cosTheta);
		double phi = eigenvector[1].phase() - eigenvector[0].phase();
		set(r*sinTheta*Math.cos(phi), r*sinTheta*Math.sin(phi), r*cosTheta);
		return this;
	}

	public static BlochVectorAnalyzer sum(String label, SimpleBlochVector... comps) {
		BlochVectorAnalyzer sum = new BlochVectorAnalyzer(label);
		for (SimpleBlochVector comp : comps) {
			sum.add(comp);
		}
		return sum;
	}

	public static BlochVectorAnalyzer crossProduct(String label, SimpleBlochVector a, SimpleBlochVector b) {
		BlochVectorAnalyzer v = new BlochVectorAnalyzer(label);
		final double a1 = a.getX();
		final double a2 = a.getY();
		final double a3 = a.getZ();
		final double b1 = b.getX();
		final double b2 = b.getY();
		final double b3 = b.getZ();
		v.set(a2*b3 - a3*b2, a3*b1 - a1*b3, a1*b2 - a2*b1);
		return v;
	}

}
