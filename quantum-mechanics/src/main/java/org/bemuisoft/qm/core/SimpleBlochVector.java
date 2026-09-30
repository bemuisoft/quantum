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

import java.util.List;

import org.bemuisoft.math.base.SpatialVector;
import org.bemuisoft.math.complex.ComplexNumber;
import org.bemuisoft.math.complex.Matrix;
import org.bemuisoft.quantum.api.Base;

/**
 * A simple implementation of a Bloch vector.
 * <p>
 * It can be constructed from a (reduced) density matrix,
 * but it cannot be modified by public methods.
 * Of course, this limitation can be removed by a subclass.
 * <p>
 * There are different types of Bloch vectors, but
 * an instance of this class can represent any of them:
 * <ol>
 * <li>A pure  Bloch vector has length 1 and can be used as
 * an alternative way to describe the pure state of a qubit.</li>
 * <li>A conditional Bloch vector is a potentially pure
 * Bloch vector in superposition with one or more
 * other conditional Bloch vectors.
 * Potentially pure in this context means that it has a
 * probability of becoming pure after wave function collapse.
 * Its weighted length is equal to this probability.</li>
 * <li>A mixed Bloch vector is the weighted sum of a number of
 * conditional Bloch vectors or components.
 * Weighted sum in this context means that conditional Bloch vectors
 * are summed with their weighted lengths.</li>
 * <li>A Bloch vector component can be any of the above.</li>
 * </ol>
 * 
 * @author Benno Muilwijk
 */
public class SimpleBlochVector extends SpatialVector implements Base {

	private String label;
	private double r;

	/**
	 * Default constructor.
	 * Creates a simple Bloch vector without label
	 * and zero length.
	 */
	protected SimpleBlochVector() {
		this("");
	}

	/**
	 * Constructs a simple Bloch vector
	 * with identifying label and zero length.
	 * 
	 * @param label	the label
	 */
	protected SimpleBlochVector(String label) {
		this.label = label;
	}

	/**
	 * Constructs a simple Bloch vector without label
	 * from a reduced density matrix.
	 * 
	 * @param rdm	the reduced density matrix
	 */
	public SimpleBlochVector(Matrix rdm) {
		this();
		set(rdm);
	}

	/**
	 * Creates a simple Bloch vector without label
	 * from the intersection of specified rows and
	 * columns of a density matrix.
	 * 
	 * @param rho	the density matrix
	 * @param i		row and column index 0
	 * @param j		row and column index 1
	 * @see			#set(Matrix, int, int)
	 * @see			Matrix#subMatrix(int, int)
	 */
	public SimpleBlochVector(Matrix rho, int i, int j) {
		this();
		set(rho, i, j);
	}

	/**
	 * Creates a simple Bloch vector
	 * with identifying label
	 * from a reduced density matrix.
	 * 
	 * @param label	the label
	 * @param rdm	the reduced density matrix
	 */
	public SimpleBlochVector(String label, Matrix rdm) {
		this(label);
		set(rdm);
	}

	/**
	 * Creates a simple Bloch vector
	 * with identifying label
	 * from the intersection of specified rows and
	 * columns of a density matrix.
	 * 
	 * @param label	the label
	 * @param rho	the density matrix
	 * @param i		row and column index 0
	 * @param j		row and column index 1
	 * @see			#set(Matrix, int, int)
	 * @see			Matrix#subMatrix(int, int)
	 */
	public SimpleBlochVector(String label, Matrix rho, int i, int j) {
		this(label);
		set(rho, i, j);
	}

	/**
	 * Returns the identifying label of this vector.
	 * 
	 * @return	the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Returns the length, or magnitude, of this vector.
	 * 
	 * @return	the length
	 */
	public double length() {
		if (Double.isNaN(r)) {
			r = round(Math.sqrt(x*x + y*y + z*z), 14);
		}
		return r;
	}

	/**
	 * Returns the polar angle of this vector.
	 * 
	 * @return	the polar angle (theta).
	 */
	public double getTheta() {
		if (length() == 0.0)
			return 0.0;
		return Math.acos(round(z, 14)/length());	// round z to the same precision as length()
	}

	/**
	 * Returns the phase, or azimuthal angle, of this vector.
	 * 
	 * @return	the phase (phi)
	 */
	public double getPhase() {
		if (x == 0.0 && y == 0.0)
			return 0.0;
		return Math.atan2(y, x);
	}

	/**
	 * Resets this vector to zero length.
	 * 
	 * @return	this vector
	 */
	protected SimpleBlochVector reset() {
		x = y = z = r = 0.0;
		return this;
	}

	/**
	 * Derives this vector from the specified
	 * reduced density matrix.
	 * 
	 * @param rdm	the reduced density matrix
	 * @return		this vector after update
	 */
	protected SimpleBlochVector set(Matrix rdm) {
		check(rdm.rows() == 2 && rdm.columns() == 2, "Reduced density matrix must be 2x2");
		set(rdm, 0, 1);
		return this;
	}

	/**
	 * Derives this vector from the intersection of
	 * specified rows and columns of a density matrix.
	 * <p>
	 * This intersection forms a 2x2 submatrix, which can
	 * be considered as a reduced density matrix component.
	 * <p>
	 * The reduced density matrix of one qubit is a partial
	 * trace of the density matrix, which is essentially
	 * the sum of a number of such components.
	 * <p>
	 * The Bloch vector for a qubit can be derived
	 * directly from its reduced density matrix (rdm).
	 * Or alternatively, a Bloch vector component
	 * can be derived from each rdm component,
	 * in which the qubit's Bloch vector is the sum
	 * of these Bloch vector components.
	 * <p>
	 * A Bloch vector component derived this way
	 * from a pure density matrix is also known as
	 * a conditional Bloch vector.
	 * In that case, the length of the Bloch vector
	 * component is equal to the probability that
	 * the state of the related qubit "collapses"
	 * into a pure state that corresponds to the
	 * normalized conditional Bloch vector,
	 * when other qubits are measured.
	 * 
	 * @param rho	the density matrix
	 * @param i		row and column index 0
	 * @param j		row and column index 1
	 * @return		this vector after update
	 * @see			Matrix#subMatrix(int, int)
	 */
	protected SimpleBlochVector set(Matrix rho, int i, int j) {
		ComplexNumber rho00 = rho.get(i, i);
		ComplexNumber rho01 = rho.get(i, j);
		ComplexNumber rho10 = rho.get(j, i);
		ComplexNumber rho11 = rho.get(j, j);
		check(rho00.imaginaryPart() == 0.0, "Imaginary part of ρ00 must be zero");
		check(rho11.imaginaryPart() == 0.0, "Imaginary part of ρ11 must be zero");
		check(rho01.imaginaryPart() + rho10.imaginaryPart() == 0.0, "Imaginary part of ρ01 + ρ10 must be zero");
		check(rho01.realPart() - rho10.realPart() == 0.0, "Real part of ρ01 - ρ10 must be zero");
		x = round(rho10.realPart() + rho01.realPart());				// x =  ρ01 + ρ10	= 2Re(ρ10) =  2Re(ρ01) = r*sin(θ)*cos(ϕ)
		y = round(rho10.imaginaryPart() - rho01.imaginaryPart());	// y = (ρ01 - ρ10)i	= 2Im(ρ10) = -2Im(ρ01) = r*sin(θ)*sin(ϕ)
		z = round(rho00.realPart() - rho11.realPart());				// z =  ρ00 - ρ11	= 2ρ00 - 1 =  1 - 2ρ11 = r*cos(θ)
//		r = round(rho00.realPart() + rho11.realPart());				// r =  ρ00 + ρ11	= sqrt(x² + y² + z²)	ONLY for elementary components
		r = Double.NaN;
		return this;
	}

	/**
	 * Sets this vector to the specified Cartesian coordinates.
	 * 
	 * @param x		projected length on the x-axis
	 * @param y		projected length on the y-axis
	 * @param z		projected length on the z-axis
	 * @return		this vector after update
	 */
	protected SimpleBlochVector set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		r = Double.NaN;
		return this;
	}

	/**
	 * Adds the specified vector (component) to this vector.
	 * 
	 * @param v		the vector to add
	 * @return		this vector after update
	 */
	protected SimpleBlochVector add(SimpleBlochVector v) {
		x += v.getX();
		y += v.getY();
		z += v.getZ();
		r = Double.NaN;
		return this;
	}

	/**
	 * Answers whether this vector is (almost) equal to the given vector.
	 * <p>
	 * This is considered to be the case if the difference between this
	 * and the other vector is less than 1e-15
	 * along all three Cartesian axes (x, y and z).
	 * 
	 * @param other	the vector to compare to
	 * @return		{@code true} if this vector is close to the other, {@code false} otherwise
	 */
	public boolean isCloseTo(SimpleBlochVector other) {
		return isCloseTo(other, 1e-15);
	}

	/**
	 * Answers whether this vector is (almost) equal to the given vector.
	 * <p>
	 * This is considered to be the case if the difference between this
	 * and the other vector is less than the specified margin
	 * along all three Cartesian axes (x, y and z).
	 * 
	 * @param other		the vector to compare to
	 * @param margin	the accepted margin or tolerance
	 * @return			{@code true} if this vector is close to the other, {@code false} otherwise
	 */
	public boolean isCloseTo(SimpleBlochVector other, double margin) {
		if (other == null) return false;
		return (this.x == other.x || Math.abs(this.x - other.x) < margin)
			&& (this.y == other.y || Math.abs(this.y - other.y) < margin)
			&& (this.z == other.z || Math.abs(this.z - other.z) < margin);
	}

	/**
	 * Returns a {@link StringBuilder} with description of this vector.
	 * 
	 * @return	a {@link StringBuilder} with description of this vector
	 */
	public StringBuilder toStringBuilder() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(' ').append(label);
		sb.append(" [x=").append(roundCos(getX()));
		sb.append(", y=").append(roundCos(getY()));
		sb.append(", z=").append(roundCos(getZ()));
		sb.append(", r=").append(length());
		if (r != 0.0) {
			sb.append(", θ=").append(round(getTheta()/PI, 5)).append('π');
			sb.append(", ϕ=").append(round(getPhase()/PI, 5)).append('π');
		}
		sb.append("]");
		return sb;
	}

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

	//////////////////
	// Static methods
	//////////////////

	/**
	 * Returns a simple Bloch vector that is the sum of
	 * a list of Bloch vector components.
	 * 
	 * @param comps	the list of Bloch vector components
	 * @return		sum of selected Bloch vector components
	 */
	public static SimpleBlochVector sum(List<SimpleBlochVector> comps) {
		SimpleBlochVector v = new SimpleBlochVector();
		for (SimpleBlochVector comp : comps) {
			v.add(comp);
		}
		return v;
	}

	/**
	 * Returns a simple Bloch vector that is the sum of
	 * selected elements from a list of Bloch vector components.
	 * 
	 * @param comps	the list of Bloch vector components
	 * @param index	selected indices of vector component to add
	 * @return		sum of selected Bloch vector components
	 */
	public static SimpleBlochVector sum(List<SimpleBlochVector> comps, int... index) {
		SimpleBlochVector v = new SimpleBlochVector();
		for (int i : index) {
			v.add(comps.get(i));
		}
		return v;
	}

}
