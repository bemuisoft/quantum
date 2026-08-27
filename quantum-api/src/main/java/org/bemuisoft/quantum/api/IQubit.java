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
 * A qubit (quantum bit) is the basic building block of a
 * quantum computer, like a bit of a digital computer.
 * While classical bits are designed to be either 0 or 1,
 * a qubit can have values in between.
 * However, when measured it always becomes either |0> or |1>.
 * <p>
 * Both classical bits and qubits are controlled by gates.
 * Logical gates for digital bits and quantum gates for qubits.
 * <p>
 * Quantum gates essentially rotate qubits,
 * optionally controlled by the state of one or more other qubits.
 * You can think of an electron's spin, which has a spatial orientation
 * that can be rotated.
 * <p>
 * Note that only the qubit's orientation relative
 * to the measurement direction matters.
 * For example, earth's rotation has no effect on the quantum state
 * of a qubit, as the measurement direction rotates along with it.
 * <p>
 * For practical (computational) reasons, the Bloch sphere is commonly
 * used as the reference frame for a qubit.
 * The Z axis is the measurement direction by definition,
 * so changing the measurement direction means rotating this
 * reference frame (or the qubit in it). 
 * For example, measuring a qubit in the X direction requires
 * a rotation so that this direction becomes aligned with the Z axis
 * before measurement.
 * <p>
 * This interface defines the measurement action, a reset action
 * and many quantum gates.
 * 
 * @author Benno Muilwijk
 */
public interface IQubit {

	/**
	 * Measures this qubit and return the outcome
	 * as either 0 for |0> or 1 for |1>.
	 * 
	 * @return this qubit's measurement value 0 or 1
	 */
	public int measure();
	
	/**
	 * Resets this qubit to the basis state |0>
	 * aka ground state of a phase qubit.
	 * 
	 * @return this qubit
	 */
	public IQubit reset();
	
	//---------------
	// Unitary gates
	//---------------
	
	/**
	 * Applies the Hadamard gate to this qubit,
	 * which essentially swaps x and z.
	 * <p>
	 * This is equivalent to RY(pi/2) o Z<br/>
	 * or in code: {@code z().ry(Math.PI/2)}
	 * <p>
	 * Except for a global phase change,
	 * it is also equivalent to {@code r(Axis.H, Math.PI)}
	 * 
	 * @return this qubit
	 */
	public IQubit h();
	
	/**
	 * Applies the Pauli X gate to this qubit,
	 * which essentially negates z and y.
	 * It is also known as a bit flip,
	 * because X(a, b) gives (b, a).
	 * <p>
	 * Except for a global phase change,
	 * this is equivalent to RX(pi)<br/>
	 * or in code: {@code rx(Math.PI)}
	 * 
	 * @return this qubit
	 */
	public IQubit x();
	
	/**
	 * Applies the Pauli Y gate to this qubit,
	 * which essentially negates z and x.
	 * <p>
	 * Except for a global phase change,
	 * this is equivalent to RY(pi)<br/>
	 * or in code: {@code ry(Math.PI)}
	 * 
	 * @return this qubit
	 */
	public IQubit y();
	
	/**
	 * Applies the Pauli Z gate to this qubit,
	 * which essentially negates x and y.
	 * <p>
	 * This is equivalent to P(pi)<br/>
	 * or in code: {@code p(Math.PI)}
	 * 
	 * @return this qubit
	 */
	public IQubit z();
	
	/**
	 * Applies the S gate to this qubit,
	 * which is the square root of the Pauli Z gate.
	 * It is essentially a phase shift of pi/2.
	 * <p>
	 * This is equivalent to P(pi/2)<br/>
	 * or in code: {@code p(Math.PI/2)}
	 * 
	 * @return this qubit
	 */
	public IQubit s();
	
	/**
	 * Applies the S dagger gate to this qubit,
	 * which is the negative square root of
	 * the Pauli Z gate.
	 * It is essentially a phase shift of -pi/2.
	 * <p>
	 * This is equivalent to P(-pi/2)<br/>
	 * or in code: {@code p(-Math.PI/2)}
	 * 
	 * @return this qubit
	 */
	public IQubit sdg();
	
	/**
	 * Applies the T gate to this qubit,
	 * which is essentially a phase shift of pi/4.
	 * <p>
	 * This is equivalent to P(pi/4)<br/>
	 * or in code: {@code p(Math.PI/4)}
	 * 
	 * @return this qubit
	 */
	public IQubit t();
	
	/**
	 * Applies the T dagger gate to this qubit,
	 * which is essentially a phase shift of -pi/4.
	 * <p>
	 * This is equivalent to P(-pi/4)<br/>
	 * or in code: {@code p(-Math.PI/4)}
	 * 
	 * @return this qubit
	 */
	public IQubit tdg();
	
	/**
	 * Applies the universal phase shift gate to this qubit,
	 * which effectively rotates this qubit about the Z axis
	 * by the specified angle.
	 * The effect is a clean phase shift without global phase change.
	 * 
	 * @param radians - the angle to rotate
	 * @return this qubit
	 */
	public IQubit p(double radians);
	
	/**
	 * Applies the SX gate to this qubit,
	 * which is the square root of the Pauli X gate.
	 * <p>
	 * Except for a global phase change,
	 * this is equivalent to RX(pi/2)<br/>
	 * or in code: {@code rx(Math.PI/2)}
	 * 
	 * @return this qubit
	 */
	public IQubit sx();
	
	/**
	 * Applies the SX dagger gate to this qubit,
	 * which is the negative square root of
	 * the Pauli X gate.
	 * <p>
	 * Except for a global phase change,
	 * this is equivalent to RX(-pi/2)<br/>
	 * or in code: {@code rx(-Math.PI/2)}
	 * 
	 * @return this qubit
	 */
	public IQubit sxdg();
	
	/**
	 * Rotates this qubit about the X axis
	 * by the specified angle.
	 * <p>
	 * It is equivalent to {@code r(Axis.X, radians)}
	 * 
	 * @param radians - the angle to rotate
	 * @return this qubit
	 */
	public IQubit rx(double radians);
	
	/**
	 * Rotates this qubit about the Y axis
	 * by the specified angle.
	 * <p>
	 * It is equivalent to {@code r(Axis.Y, radians)}
	 * 
	 * @param radians - the angle to rotate
	 * @return this qubit
	 */
	public IQubit ry(double radians);
	
	/**
	 * Rotates this qubit about the Z axis
	 * by the specified angle.
	 * The effect is a phase shift
	 * like the universal phase shift gate,
	 * but with a global phase change.
	 * <p>
	 * It is equivalent to {@code r(Axis.Z, radians)}
	 * 
	 * @param radians - the angle to rotate
	 * @return this qubit
	 */
	public IQubit rz(double radians);
	
	/**
	 * Rotates this qubit about the specified axis
	 * by the specified angle.
	 * Some examples:<ul>
	 * <li>{@code r(Axis.Z, lambda)} is equivalent to {@code rz(lambda)}
	 * <li>{@code r(Axis.Y, lambda)} is equivalent to {@code ry(lambda)}
	 * <li>{@code r(Axis.X, lambda)} is equivalent to {@code rx(lambda)}
	 * </ul>
	 * 
	 * @param axis - the rotation axis
	 * @param radians - the angle to rotate
	 * @return this qubit
	 */
	public IQubit r(Axis axis, double radians);
	
	/**
	 * Applies the universal gate U to this qubit.
	 * <p>
	 * This is equivalent to P(phi) o RY(theta) o P(lambda)<br/>
	 * or in code: {@code p(lambda).ry(theta).p(phi)}.
	 * <p>
	 * Some examples:<ul>
	 * <li>{@code u(theta, 0, 0)} is equivalent to {@code ry(theta)}
	 * <li>{@code u(0, phi, lambda)} is equivalent to {@code p(phi+lambda)}
	 * <li>{@code u(theta, -Math.PI/2, Math.PI/2)} is equivalent to {@code rx(theta)}
	 * <li>{@code u(Math.PI/2, 0, Math.PI)} is equivalent to {@code h()}
	 * <li>{@code u(theta, phi, -phi)} where phi equals this qubit's current phase, adds theta to this qubit's theta
	 * without changing its phase
	 * </ul>
	 * 
	 * @param theta - theta
	 * @param phi - phi
	 * @param lambda - lambda
	 * @return this qubit
	 */
	public IQubit u(double theta, double phi, double lambda);
	
	/**
	 * Applies the NOT gate aka bit flip to this qubit.
	 * <p>
	 * This is equivalent to the Pauli X gate<br/>
	 * or in code: {@code x()}
	 * 
	 * @return this qubit
	 */
	public default IQubit not() {
		return x();
	}
	
	//-----------------
	// Two-qubit gates
	//-----------------
	
	/**
	 * Applies the controlled X (aka CNOT) gate to this qubit,
	 * using the input qubit as control.
	 * 
	 * @param ctrl - the control qubit
	 * @return this qubit (the target)
	 * @throws ClassCastException when {@code ctrl} is not compatible
	 */
	public IQubit cx(IQubit ctrl);
	
	/**
	 * Applies the controlled Z gate to this qubit,
	 * using the input qubit as control.
	 * 
	 * @param ctrl - the control qubit
	 * @return this qubit (the target)
	 * @throws ClassCastException when {@code ctrl} is not compatible
	 */
	public IQubit cz(IQubit ctrl);
	
	/**
	 * Applies a controlled rotation of pi about the specified axis to this qubit.
	 * Some examples:<ul>
	 * <li>{@code c(Axis.X, ctrl)} performs a controlled X, aka CX, equivalent to {@code cx(ctrl)}
	 * <li>{@code c(Axis.Y, ctrl)} performs a controlled Y, aka CY
	 * <li>{@code c(Axis.Z, ctrl)} performs a controlled Z, aka CZ, equivalent to {@code cz(ctrl)}
	 * <li>{@code c(Axis.H, ctrl)} performs a controlled H, aka CH
	 * </ul>
	 * In all cases, the control qubit is affected by a phase kickback,
	 * which is controlled by how this qubit (the target) would be
	 * measured along the specified axis.
	 * <p>
	 * Note that {@code c(anAxis, ctrl)} is not exactly the same as {@code cr(anAxis, Math.PI, ctrl)}.
	 * The difference is that {@code cr} applies an extra phase shift of -lambda/2 = -pi/2 to the control qubit.
	 * 
	 * @param axis - the rotation axis
	 * @param ctrl - the control qubit
	 * @return this qubit (the target)
	 * @throws ClassCastException when {@code ctrl} is not compatible
	 */
	public IQubit c(Axis axis, IQubit ctrl);
	
	/**
	 * Applies a controlled phase shift.
	 * It is similar to {@code cr(Axis.Z, lambda, ctrl)}
	 * but without extra phase shift on the control qubit.
	 * <p>
	 * This operation is symmetric, i.e.
	 * {@code this.cp(lambda, ctrl)} is equivalent to {@code ctrl.cp(lambda, this)}.
	 * 
	 * @param radians - the controlled angle to rotate
	 * @param ctrl - the control qubit
	 * @return this qubit (the target)
	 * @throws ClassCastException when {@code ctrl} is not compatible
	 */
	public IQubit cp(double radians, IQubit ctrl);
	
	/**
	 * Applies a controlled rotation to this qubit.
	 * Some examples:<ul>
	 * <li>{@code cr(Axis.X, lambda, ctrl)} performs a controlled RX(lambda)
	 * <li>{@code cr(Axis.Y, lambda, ctrl)} performs a controlled RY(lambda)
	 * <li>{@code cr(Axis.Z, lambda, ctrl)} performs a controlled RZ(lambda)
	 * </ul>
	 * Note that {@code cr(Axis.Z, lambda, ctrl)} is not symmetric, unlike {@code cp(lambda, ctrl)}.
	 * This is because CRZ(lambda) implies an extra P(-lambda/2) on the control qubit, compared  to CP(lambda).
	 * <p>
	 * Another side effect is that {@code cr(anAxis, Math.PI, ctrl)}
	 * is not exactly the same as {@code c(anAxis, ctrl)},
	 * as can be seen by comparing the QM definitions of
	 * X and CX versus RX(pi) and CRX(pi),
	 * Y and CY versus RY(pi) and CRY(pi), as well as
	 * Z and CZ versus RZ(pi) and CRZ(pi).
	 * 
	 * @param axis - the rotation axis
	 * @param radians - the controlled angle to rotate
	 * @param ctrl - the control qubit
	 * @return this qubit (the target)
	 * @throws ClassCastException when {@code ctrl} is not compatible
	 */
	public IQubit cr(Axis axis, double radians, IQubit ctrl);
	
	/**
	 * Applies a controlled universal gate U to this qubit.
	 * <p>
	 * This is equivalent to a controlled P(phi) o RY(theta) o P(lambda).
	 * <p>
	 * Some examples:<ul>
	 * <li>{@code cu(theta, 0, 0, ctrl)} performs a controlled RY(theta)
	 * <li>{@code cu(0, phi, lambda, ctrl)} performs a controlled P(phi+lambda)
	 * <li>{@code cu(theta, -Math.PI/2, Math.PI/2, ctrl)} performs a controlled RX(theta)
	 * <li>{@code cu(Math.PI/2, 0, Math.PI, ctrl)} performs a controlled H
	 * </ul>
	 * The CU operation usually performs better than the CR operation.
	 * 
	 * @param theta - theta
	 * @param phi - phi
	 * @param lambda - lambda
	 * @param ctrl - the control qubit
	 * @return this qubit
	 * @throws ClassCastException when {@code ctrl} is not compatible
	 */
	public IQubit cu(double theta, double phi, double lambda, IQubit ctrl);
	
	/**
	 * Applies the CNOT gate to this qubit,
	 * using the input qubit as control.
	 * <p>
	 * This is equivalent to the controlled X gate<br/>
	 * or in code: {@code cx(ctrl)}
	 * 
	 * @param ctrl - the control qubit
	 * @return this qubit (the target)
	 * @throws ClassCastException when {@code ctrl} is not compatible
	 */
	public default IQubit cnot(IQubit ctrl) {
		return cx(ctrl);
	}
	
	//-------------------
	// Three-qubit gates
	//-------------------
	
	/**
	 * Applies the controlled controlled X gate to this qubit,
	 * using the input qubits as control.
	 * <p>
	 * It is equivalent to {@code c(Axis.X, ctrl1, ctrl2)},
	 * but it might perform better, depending on the implementation.
	 * 
	 * @param ctrl1 - control qubit 1
	 * @param ctrl2 - control qubit 2
	 * @return this qubit (the target)
	 * @throws ClassCastException when either control qubit is not compatible
	 */
	public default IQubit ccx(IQubit ctrl1, IQubit ctrl2) {
		h();
		ccz(ctrl1, ctrl2);
		h();
		return this;
	}
	
	/**
	 * Applies the controlled controlled Z gate to this qubit,
	 * using the input qubits as control.
	 * <p>
	 * It is equivalent to {@code c(Axis.Z, ctrl1, ctrl2)},
	 * but it might perform better, depending on the implementation.
	 * 
	 * @param ctrl1 - control qubit 1
	 * @param ctrl2 - control qubit 2
	 * @return this qubit (the target)
	 * @throws ClassCastException when either control qubit is not compatible
	 */
	public default IQubit ccz(IQubit ctrl1, IQubit ctrl2) {
		return ccp(Math.PI, ctrl1, ctrl2);
	}
	
	/**
	 * Applies a controlled controlled phase shift.
	 * <p>
	 * It is similar to {@code cr(Axis.Z, radians, ctrl1, ctrl2)}
	 * but without extra phase shift on the control qubits.
	 * <p>
	 * This operation is symmetric, i.e.
	 * {@code this.ccp(lambda, ctrl1, ctrl2)} is equivalent to
	 * {@code ctrl1.cp(lambda, this, ctrl2)} and {@code ctrl2.cp(lambda, ctrl1, this)}.
	 * 
	 * @param radians - the controlled angle to rotate
	 * @param ctrl1 - control qubit 1
	 * @param ctrl2 - control qubit 2
	 * @return this qubit (the target)
	 * @throws ClassCastException when either control qubit is not compatible
	 */
	public default IQubit ccp(double radians, IQubit ctrl1, IQubit ctrl2) {
		double alpha = radians * 0.5;
		cp(alpha, ctrl1);
		cx(ctrl2);
		cp(-alpha, ctrl1);
		cx(ctrl2);
		ctrl1.cp(alpha, ctrl2);
		return this;
	}
	
	/**
	 * Applies the Toffoli (aka CCNOT) gate to this qubit,
	 * using the input qubits as control.
	 * <p>
	 * This is equivalent to the controlled controlled X gate<br/>
	 * or in code: {@code ccx(ctrl1, ctrl2)}
	 * 
	 * @param ctrl1 - control qubit 1
	 * @param ctrl2 - control qubit 2
	 * @return this qubit (the target)
	 * @throws ClassCastException when any control qubit is not compatible
	 */
	public default IQubit toffoli(IQubit ctrl1, IQubit ctrl2) {
		return ccx(ctrl1, ctrl2);
	}
	
	//-------------------
	// Multi-qubit gates
	//-------------------
	
	/**
	 * Applies a multi-controlled rotation of pi about the specified axis to this qubit.
	 * Some examples:<ul>
	 * <li>{@code c(Axis.X, ctrl1, ctrl2)} performs a CCX, equivalent to {@code ccx(ctrl1, ctrl2)}
	 * <li>{@code c(Axis.Z, ctrl1, ctrl2)} performs a CCZ, equivalent to {@code ccz(ctrl1, ctrl2)}
	 * </ul>
	 * 
	 * @param axis - the rotation axis
	 * @param ctrl - the control qubits
	 * @return this qubit (the target)
	 * @throws ClassCastException when any control qubit is not compatible
	 * @throws UnsupportedOperationException when this gate is not supported
	 */
	public default IQubit c(Axis axis, IQubit... ctrl) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Applies a multi-controlled rotation to this qubit.
	 * Some examples:<ul>
	 * <li>{@code cr(Axis.X, lambda, ctrl1, ctrl2)} performs a controlled controlled RX(lambda)
	 * <li>{@code cr(Axis.Y, lambda, ctrl1, ctrl2)} performs a controlled controlled RY(lambda)
	 * <li>{@code cr(Axis.Z, lambda, ctrl1, ctrl2)} performs a controlled controlled RZ(lambda)
	 * </ul>
	 * Note that the same differences apply as for {@code cr()} with a single control qubit.
	 * 
	 * @param axis - the rotation axis
	 * @param radians - the controlled angle to rotate
	 * @param ctrl - the control qubits
	 * @return this qubit (the target)
	 * @throws ClassCastException when any control qubit is not compatible
	 * @throws UnsupportedOperationException when this gate is not supported
	 * @see IQubit#cr(Axis, double, IQubit)
	 */
	public default IQubit cr(Axis axis, double radians, IQubit... ctrl) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Applies a multi-controlled universal gate U to this qubit.
	 * <p>
	 * Some examples:<ul>
	 * <li>{@code cu(theta, 0, 0, ctrl1, ctrl2)} performs a controlled controlled RY(theta)
	 * <li>{@code cu(0, phi, lambda, ctrl1, ctrl2)} performs a controlled controlled P(phi+lambda)
	 * <li>{@code cu(theta, -Math.PI/2, Math.PI/2, ctrl1, ctrl2)} performs a controlled controlled RX(theta)
	 * <li>{@code cu(Math.PI/2, 0, Math.PI, ctrl1, ctrl2)} performs a controlled controlled H
	 * </ul>
	 * The CU operation usually performs better than the CR operation.
	 * 
	 * @param theta - theta
	 * @param phi - phi
	 * @param lambda - lambda
	 * @param ctrl - the control qubits
	 * @return this qubit
	 * @throws ClassCastException when {@code ctrl} is not compatible
	 * @throws UnsupportedOperationException when this gate is not supported
	 */
	public default IQubit cu(double theta, double phi, double lambda, IQubit... ctrl) {
		throw new UnsupportedOperationException();
	}

}
