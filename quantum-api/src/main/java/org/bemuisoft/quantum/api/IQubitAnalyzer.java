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

/**
 * Extends {@link IQubit} with some methods that may be useful
 * for analysis.
 * They are not available on physical qubits or particles,
 * but can be used to analyze the internal state of
 * simulation models.
 * <p>
 * Only {@code getX()}, {@code getY()} and {@code getZ()}
 * are required.
 * For all other methods default implementations are provided,
 * which may throw an {@code UnsupportedOperationException}
 * if not overridden by the implementing class.
 * <p>
 * The pure state psi of a single qubit is usually described as
 * <pre>|psi> = (a, b)</pre>
 * where a and b are two complex numbers, which can be expressed as<pre>
 * a = |a| * e^(i*alpha)
 * b = |b| * e^(i*beta) 
 * </pre>
 * This state can also be described using two angles theta and phi in a Bloch sphere, with<pre>
 * a = cos(theta/2) 
 * b = sin(theta/2) * e^(i*phi) 
 *   = sin(theta/2) * (cos(phi) + i*sin(phi))
 * </pre>
 * where phi = beta - alpha, which is the relative phase.
 * The phase of a (alpha) can be considered as the global or overall phase,
 * which is irrelevant as it cannot be measured.
 * <p>
 * The state can also be viewed as a point on a Bloch sphere or Bloch vector with:<pre>
 *  z = cos(theta)
 *  y = sin(theta)sin(phase)
 *  x = sin(theta)cos(phase)
 * </pre>where theta is defined to be in the range 0 through pi.
 * Some special states:<pre>
 * |0> is the state (1, 0) which has theta = 0	and x = 0, y = 0, z = +1
 * |1> is the state (0, 1) which has theta = pi	and x = 0, y = 0, z = -1
 * |+> is the state (sqrt(1/2),  sqrt(1/2)) which has theta = pi/2 and phase = 0	and x = +1, y = 0, z = 0
 * |-> is the state (sqrt(1/2), -sqrt(1/2)) which has theta = pi/2 and phase = pi	and x = -1, y = 0, z = 0
 * </pre>
 * The probability of measuring +1 along the Z axis, i.e. |0>, is cos²(theta/2) = (1+cos(theta))/2 = (1+z)/2<br/>
 * The probability of measuring -1 along the Z axis, i.e. |1>, is sin²(theta/2) = (1-cos(theta))/2 = (1-z)/2<br/>
 * <p>
 * When a qubit is entangled with another qubit, its state is not pure, but mixed.
 * This means it is a superposition of two possible (conditional) Bloch vectors,
 * one that will become the true state if the other qubit becomes |0> after measurement, and.
 * one that will become the true state if the other qubit becomes |1> after measurement.
 * Until such a measurement is done, a mixed Bloch vector can be assigned,
 * which is the weighted sum of the conditional Bloch vectors.
 * For example, if the probability that the other qubit is measured as |0> is 75%
 * (and thus 25% for |1>), the weighted sum is 0.75 times one + 0.25 times the other
 * conditional Bloch vector.
 * <p>
 * The methods {@code getX(i)}, {@code getY(i)} and {@code getZ(i)} may be used to
 * return the weighted x, y, and z values of the individual conditional Bloch vectors,
 * but implementors are not required to do so.
 * 
 * @author Benno Muilwijk
 * @see IQubit
 */
public interface IQubitAnalyzer extends IQubit {

	/**
	 * Returns the quantum state of the total system
	 * that includes this qubit, if supported.
	 * 
	 * @return	the system's quantum state, or null
	 * @see IQuantumState
	 */
	public default IQuantumState getSystemState() {
		return null;
	}

	/**
	 * Returns the label of this qubit.
	 * 
	 * @return	the label
	 */
	public default String getLabel() {
		return "";
	}

	/**
	 * Returns the x value of this qubit's (pure or mixed) Bloch vector.
	 * This is the sum of weighted the x values of all conditional Bloch vectors.
	 * 
	 * @return	the Bloch vector's x value
	 */
	public double getX();

	/**
	 * Returns the y value of this qubit's (pure or mixed) Bloch vector.
	 * This is the sum of weighted the y values of all conditional Bloch vectors.
	 * 
	 * @return	the Bloch vector's y value
	 */
	public double getY();

	/**
	 * Returns the z value of this qubit's (pure or mixed) Bloch vector.
	 * This is the sum of weighted the z values of all conditional Bloch vectors.
	 * 
	 * @return	the Bloch vector's z value
	 */
	public double getZ();

	/**
	 * Returns the magnitude of this qubit's (pure or mixed) Bloch vector.
	 * <p>
	 * A magnitude of 1 indicates that this qubit has a
	 * pure state.
	 * A magnitude of less than 1 indicates that this qubit
	 * has a mixed state.
	 * 
	 * @return	the Bloch vector's magnitude
	 */
	public default double getMagnitude() {
		final double x = getX();
		final double y = getY();
		final double z = getZ();
		return Math.sqrt(x*x + y*y + z*z);
	}

	/**
	 * Returns this qubit's relative phase,
	 * which is the azimuthal angle phi in the Bloch sphere.
	 * This is the angle relative to the X axis
	 * in the XOY plane.
	 * <p>
	 * In case this qubit's magnitude equals zero,
	 * i.e. when this qubit is maximally entangled,
	 * the azimuthal angle is not defined and anything may be returned.
	 * 
	 * @return	this qubit's phase phi in radians
	 */
	public default double getPhase() {
		return Math.atan2(getY(), getX());
	}

	/**
	 * Returns this qubit's polar angle theta in the Bloch sphere,
	 * which is defined to be in the range 0 through pi,
	 * so sin(theta) >= 0.
	 * <p>
	 * In case this qubit's magnitude equals zero,
	 * i.e. when this qubit is maximally entangled,
	 * the polar angle is not defined and anything may be returned.
	 * 
	 * @return	this qubit's angle theta in radians
	 */
	public default double getTheta() {
		return Math.acos(getZ() / getMagnitude());
	}

	/**
	 * Returns the probability of measuring |0> along the Z axis.
	 * This probability is defined as (1+z)/2.
	 * 
	 * @return	the probability of measuring |0>
	 */
	public default double getProbability0() {
		return (1.0 + getZ()) / 2.0;
	}

	/**
	 * Returns the probability of measuring |1> along the Z axis.
	 * This probability is defined as (1-z)/2.
	 * 
	 * @return	the probability of measuring |1>
	 */
	public default double getProbability1() {
		return (1.0 - getZ()) / 2.0;
	}

	/**
	 * Returns this qubit's purity.
	 * 
	 * @return	this qubit's purity
	 */
	public default double getPurity() {
		final double r = getMagnitude();
		return (1.0 + r*r) / 2.0;
	}

	/**
	 * Answers whether this qubit's state is mixed.
	 * <p>
	 * This is the case when the magnitude of its
	 * Bloch vector is less than 1.
	 * 
	 * @return	{@code true} if the state is mixed,
	 * 			{@code false} otherwise
	 */
	public default boolean isMixed() {
		return (getMagnitude() < 1.0);
	}

	/**
	 * Answers whether this qubit's state is pure.
	 * <p>
	 * This is the case when the magnitude of its
	 * Bloch vector is equal to 1.
	 * 
	 * @return	{@code true} if the state is pure,
	 * 			{@code false} otherwise
	 */
	public default boolean isPure() {
		return (getMagnitude() == 1.0);
	}

	/**
	 * Returns the number of Bloch vector components
	 * for this qubit.
	 * <p>
	 * If this method is overridden and returns a value
	 * greater than zero, the methods
	 * {@code getX(i)}, {@code getY(i)} and {@code getZ(i)}
	 * must also be overridden.
	 * <p>
	 * The meaning of component is implementation dependent,
	 * especially for "toy" models.
	 * A common implementation is for the conditional Bloch
	 * vectors that can be extracted from the state vector.
	 * 
	 * @return	number of Bloch vector components
	 */
	public default int components() {
		return 0;
	}

	/**
	 * Returns the x value of this qubit's Bloch vector
	 * component at the specified index.
	 * <p>
	 * The index must be non-negative and less than
	 * the value returned by {@code components()}.
	 * <p>
	 * The meaning of this value is implementation dependent,
	 * but if the components represent conditional Bloch
	 * vectors, the returned value is the weighted x value.
	 * 
	 * @param i		the Bloch vector component index
	 * @return		the Bloch vector component's x value
	 * @throws		UnsupportedOperationException
	 * 				if this operation is not supported
	 * @see #components()
	 */
	public default double getX(int i) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the y value of this qubit's Bloch vector
	 * component at the specified index.
	 * <p>
	 * The index must be non-negative and less than
	 * the value returned by {@code components()}.
	 * <p>
	 * The meaning of this value is implementation dependent,
	 * but if the components represent conditional Bloch
	 * vectors, the returned value is the weighted y value.
	 * 
	 * @param i		the Bloch vector component index
	 * @return		the Bloch vector component's y value
	 * @throws		UnsupportedOperationException
	 * 				if this operation is not supported
	 * @see #components()
	 */
	public default double getY(int i) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the z value of this qubit's Bloch vector
	 * component at the specified index.
	 * <p>
	 * The index must be non-negative and less than
	 * the value returned by {@code components()}.
	 * <p>
	 * The meaning of this value is implementation dependent,
	 * but if the components represent conditional Bloch
	 * vectors, the returned value is the weighted z value.
	 * 
	 * @param i		the Bloch vector component index
	 * @return		the Bloch vector component's z value
	 * @throws		UnsupportedOperationException
	 * 				if this operation is not supported
	 * @see #components()
	 */
	public default double getZ(int i) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the x value of this qubit's hidden vector,
	 * if defined.
	 * <p>
	 * This value may determine the outcome of a measurement
	 * along the X axis, in combination with the x value of
	 * this qubit's Bloch vector.
	 * 
	 * @return	the hidden vector's x value
	 * @throws	UnsupportedOperationException
	 * 			if this operation is not supported
	 */
	public default double getHiddenX() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the y value of this qubit's hidden vector,
	 * if defined.
	 * <p>
	 * This value may determine the outcome of a measurement
	 * along the Y axis, in combination with the y value of
	 * this qubit's Bloch vector.
	 * 
	 * @return	the hidden vector's y value
	 * @throws	UnsupportedOperationException
	 * 			if this operation is not supported
	 */
	public default double getHiddenY() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the z value of this qubit's hidden vector,
	 * if defined.
	 * <p>
	 * This value may determine the outcome of a measurement
	 * along the Z axis, in combination with the z value of
	 * this qubit's Bloch vector.
	 * 
	 * @return	the hidden vector's z value
	 * @throws	UnsupportedOperationException
	 * 			if this operation is not supported
	 */
	public default double getHiddenZ() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets the z value of this qubit's hidden vector,
	 * if supported.
	 * <p>
	 * This value may be used to get a predictable outcome
	 * of a measurement along the Z axis, in combination
	 * with the z value of this qubit's Bloch vector.
	 * 
	 * @param lambda	the hidden vector's z value
	 * @throws	UnsupportedOperationException
	 * 			if this operation is not supported
	 */
	public default void setHiddenZ(double lambda) {
		throw new UnsupportedOperationException();
	}

}
