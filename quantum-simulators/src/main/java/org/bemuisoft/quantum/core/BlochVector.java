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
package org.bemuisoft.quantum.core;

import org.bemuisoft.math.base.SpatialVector;
import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.Base;

/**
 * This class implements a (pure or mixed) Bloch vector
 * with various single part operations, mostly rotations.
 * <p>
 * It is optimized for minimal memory, at the cost of some processing overhead.
 * <p>
 * The magnitude (aka norm or length) is equal to
 * the probability that the qubit state will
 * collapse into the direction of this vector
 * when all other qubits are measured.
 * 
 * @author Benno Muilwijk
 */
public class BlochVector extends SpatialVector implements Base {

	/**
	 * Constructs a zero vector.
	 */
	public BlochVector() {
		this(0.0, 0.0, 0.0);
	}

	/**
	 * Constructs a vector with Cartesian coordinates x, y and z.
	 * 
	 * @param x	- length in x-direction
	 * @param y	- length in y-direction
	 * @param z	- length in z-direction
	 */
	public BlochVector(double x, double y, double z) {
		super(x, y, z);
	}

	/**
	 * Constructs a new vector from the given vector.
	 * 
	 * @param v	- vector to copy
	 */
	public BlochVector(SpatialVector v) {
		super();
		set(v);
	}

	/**
	 * Returns this vector's magnitude (aka norm or length).
	 * 
	 * @return this vector's azimuthal angle phi in radians
	 */
	public double getMagnitude() {
		return Math.sqrt(x*x + y*y + z*z);
	}

	/**
	 * Returns this vector's relative phase (phi),
	 * which is the azimuthal angle phi in the Bloch ball.
	 * This is the angle relative to the positive X axis
	 * in the XOY plane.
	 * 
	 * @return this vector's phase phi in radians
	 */
	public double getPhase() {
		double x = getX();
		double y = getY();
		if (Math.abs(x) < 1e-15) x = 0.0;
		if (Math.abs(y) < 1e-15) y = 0.0;
		return Math.atan2(y, x);
	}

	/**
	 * Returns this vector's azimuthal angle phi in the Bloch ball.
	 * This is the angle relative to the positive X axis
	 * in the XOY plane.
	 * 
	 * @return this vector's azimuthal angle phi in radians
	 */
	public double getPhi() {
		return getPhase();
	}

	/**
	 * Returns this vector's polar angle theta in the Bloch ball.
	 * This is the angle relative to the positive Z axis,
	 * which is defined to be in the range 0 through pi,
	 * so sin(theta) >= 0.
	 * 
	 * @return this vector's polar angle theta in radians
	 */
	public double getTheta() {
		return Math.acos(getZ() / getMagnitude());
	}

	/**
	 * Sets this vector's x, y and z coordinates
	 * from the given vector.
	 * 
	 * @param v	- vector to copy
	 * @return this vector after update
	 */
	public BlochVector set(SpatialVector v) {
		setXYZ(v.getX(), v.getY(), v.getZ());
		return this;
	}

	/**
	 * Sets this vector's x, y and z coordinates
	 * in the Bloch ball.
	 * 
	 * @param x	- length in x-direction
	 * @param y	- length in y-direction
	 * @param z	- length in z-direction
	 * @return this vector after update
	 */
	public BlochVector setXYZ(double x, double y, double z) {
		super.x = roundCos(x);
		super.y = roundCos(y);
		super.z = roundCos(z);
		return this;
	}

	/**
	 * Sets this vector's x and y coordinates
	 * in the Bloch ball.
	 * <p>
	 * This private method is only used for rotations,
	 * so that the norm of this vector does not change.
	 * 
	 * @param x	- length in x-direction
	 * @param y	- length in y-direction
	 * @return this vector after update
	 */
	private BlochVector setXY(double x, double y) {
		super.x = roundCos(x);
		super.y = roundCos(y);
		return this;
	}

	/**
	 * Sets this vector's x and z coordinates
	 * in the Bloch ball.
	 * <p>
	 * This private method is only used for rotations,
	 * so that the norm of this vector does not change.
	 * 
	 * @param x	- length in x-direction
	 * @param z	- length in z-direction
	 * @return this vector after update
	 */
	private BlochVector setXZ(double x, double z) {
		super.x = roundCos(x);
		super.z = roundCos(z);
		return this;
	}

	/**
	 * Sets this vector's y and z coordinates
	 * in the Bloch ball.
	 * <p>
	 * This private method is only used for rotations,
	 * so that the norm of this vector does not change.
	 * 
	 * @param y	- length in y-direction
	 * @param z	- length in z-direction
	 * @return this vector after update
	 */
	private BlochVector setYZ(double y, double z) {
		super.y = roundCos(y);
		super.z = roundCos(z);
		return this;
	}

	/**
	 * Applies the Hadamard gate to this vector,
	 * which essentially swaps x and z.
	 * <p>
	 * This is equivalent to Ry(pi/2) o Z<br/>
	 * or in code: {@code z().ry(Math.PI/2)}
	 * 
	 * @return this vector after update
	 */
	public BlochVector h() {
		setXZ(z, x);
		y = -y;
		return this;
	}

	/**
	 * Applies the Pauli X gate to this vector,
	 * which essentially negates z and y.
	 * It is also known as a bit flip,
	 * because X(a, b) gives (b, a).
	 * 
	 * @return this vector after update
	 */
	public BlochVector x() {
		setYZ(-y, -z);
		return this;
	}

	/**
	 * Applies the Pauli Y gate to this vector,
	 * which essentially negates z and x.
	 * <p>
	 * This is equivalent to Ry(pi)<br/>
	 * or in code: {@code ry(Math.PI)}
	 * 
	 * @return this vector after update
	 */
	public BlochVector y() {
		setXZ(-x, -z);
		return this;
	}

	/**
	 * Applies the Pauli Z gate to this vector,
	 * which essentially negates x and y.
	 * 
	 * @return this vector after update
	 */
	public BlochVector z() {
		setXY(-x, -y);
		return this;
	}

	/**
	 * Applies the S gate to this vector,
	 * which is essentially a phase shift of pi/2.
	 * <p>
	 * This is equivalent to Rz(pi/2)<br/>
	 * or in code: {@code rz(Math.PI/2)}
	 * 
	 * @return this vector after update
	 */
	public BlochVector s() {
		setXY(-y, x);
		return this;
	}

	/**
	 * Applies the S dagger gate to this vector,
	 * which is essentially a phase shift of -pi/2.
	 * <p>
	 * This is equivalent to Rz(-pi/2)<br/>
	 * or in code: {@code rz(-Math.PI/2)}
	 * 
	 * @return this vector after update
	 */
	public BlochVector sdg() {
		setXY(y, -x);
		return this;
	}

	/**
	 * Applies the T gate to this vector,
	 * which is essentially a phase shift of pi/4.
	 * <p>
	 * This is equivalent to Rz(pi/4)<br/>
	 * or in code: {@code rz(Math.PI/4)}
	 * 
	 * @return this vector after update
	 */
	public BlochVector t() {
		// cos(a + pi/4) = cos(a)cos(pi/4) - sin(a)sin(pi/4)
		// sin(a + pi/4) = sin(a)cos(pi/4) + cos(a)sin(pi/4)
		setXY((x - y) * SQRT_HALF, (x + y) * SQRT_HALF);
		return this;
	}

	/**
	 * Applies the T dagger gate to this vector,
	 * which is essentially a phase shift of -pi/4.
	 * <p>
	 * This is equivalent to Rz(-pi/4)<br/>
	 * or in code: {@code rz(-Math.PI/4)}
	 * 
	 * @return this vector after update
	 */
	public BlochVector tdg() {
		// cos(a - pi/4) = sin(a)sin(pi/4) + cos(a)cos(pi/4)
		// sin(a - pi/4) = sin(a)cos(pi/4) - cos(a)sin(pi/4)
		setXY((y + x) * SQRT_HALF, (y - x) * SQRT_HALF);
		return this;
	}

	/**
	 * Applies the SX gate to this vector.
	 * <p>
	 * This is equivalent to Rx(pi/2)<br/>
	 * or in code: {@code rx(Math.PI/2)}
	 * 
	 * @return this vector after update
	 */
	public BlochVector sx() {
		setYZ(-z, y);
		return this;
	}

	/**
	 * Applies the SX dagger gate to this vector.
	 * <p>
	 * This is equivalent to Rx(-pi/2)<br/>
	 * or in code: {@code rx(-Math.PI/2)}
	 * 
	 * @return this vector after update
	 */
	public BlochVector sxdg() {
		setYZ(z, -y);
		return this;
	}

	/**
	 * Rotates this vector about the X axis
	 * by the specified angle.
	 * 
	 * @param radians	- the rotation angle
	 * @return this vector after update
	 */
	public BlochVector rx(double radians) {
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);
		setYZ(y*cos - z*sin, y*sin + z*cos);
		return this;
	}

	/**
	 * Rotates this vector about the Y axis
	 * by the specified angle.
	 * 
	 * @param radians	- the rotation angle
	 * @return this vector after update
	 */
	public BlochVector ry(double radians) {
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);
		setXZ(z*sin + x*cos, z*cos - x*sin);
		return this;
	}

	/**
	 * Rotates this vector about the Z axis
	 * by the specified angle.
	 * 
	 * @param radians	- the rotation angle
	 * @return this vector after update
	 */
	public BlochVector rz(double radians) {
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);
		setXY(x*cos - y*sin, x*sin + y*cos);
		return this;
	}

	/**
	 * Applies a phase shift to this vector.
	 * <p>
	 * This is equivalent to the RZ gate.
	 * 
	 * @param radians	- the rotation angle
	 * @return this vector after update
	 */
	public BlochVector p(double radians) {
		rz(radians);
		return this;
	}

	/**
	 * Applies the universal gate U to this vector.
	 * <p>
	 * This is equivalent to RZ(phi)RY(theta)RZ(lambda).
	 * 
	 * @param theta		- theta
	 * @param phi		- phi
	 * @param lambda	- lambda
	 * @return this vector after update
	 */
	public BlochVector u(double theta, double phi, double lambda) {
		if (lambda != 0.0) rz(lambda);
		if (theta != 0.0) ry(theta);
		if (phi != 0.0) rz(phi);
		return this;
	}

	/**
	 * Rotates this vector about the given axis
	 * by the specified angle.
	 * 
	 * @param axis		- the rotation axis
	 * @param radians	- the rotation angle
	 * @return this vector after update
	 */
	public BlochVector r(Axis axis, double radians) {
		final double axisTheta = axis.getTheta();
		final double axisPhase = axis.getPhi();
		if (axisPhase != 0.0) rz(-axisPhase);
		if (axisTheta != 0.0) ry(-axisTheta);
		u(axisTheta, axisPhase, radians);
		return this;
	}

	/**
	 * Appends a description of this vector
	 * to the given string builsder.
	 * 
	 * @param sb	- the string builder
	 * @return the same string builder
	 */
	protected StringBuilder addDescription(StringBuilder sb) {
		double r = getMagnitude();
		sb.append(" {x=");
		sb.append(getX());
		sb.append(", y=");
		sb.append(getY());
		sb.append(", z=");
		sb.append(getZ());
		sb.append(", r=");
		sb.append(r);
		if (r > 0.0) {
			sb.append(", theta=");
			sb.append(toPi(getTheta()));
			sb.append(", phase=");
			sb.append(toPi(getPhase()));
		}
		sb.append('}');
		return sb;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		addDescription(sb);
		return sb.toString();
	}

}
