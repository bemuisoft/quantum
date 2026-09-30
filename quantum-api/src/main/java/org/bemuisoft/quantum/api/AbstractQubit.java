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

import java.util.Arrays;

/**
 * An abstract implementation of the {@link IQubit} interface.
 * <p>
 * This class can be useful as superclass for an {@link IQubit}
 * implementation, because it provides default implementations
 * for most gates, so that only a few essential gates need to be
 * implemented in the subclass.
 * <p>
 * Disclaimer: the provided default implementations are just examples,
 * to show how different gates relate to each other, for education and
 * convenience. They are <i>not</i> always the most efficient implementation!
 * 
 * @author Benno Muilwijk
 */
public abstract class AbstractQubit implements IQubit, Base {

	@Override
	public IQubit reset() {
		// measure this qubit and if the state after measurement is |1⟩
		// then flip it to |0⟩.
		if (measure() == 1) {
			x();
		}
		return this;
	}

	//--------------------
	// Single-qubit gates
	//--------------------

	@Override
	public IQubit h() {
		return u2(0.0, PI);
	}

	@Override
	public IQubit x() {
		u2(0.0, PI);
		u2(0.0, 0.0);
		return this;
	}

	@Override
	public IQubit y() {
		u2(0.0, HALF_PI);
		u2(HALF_PI, 0.0);
		return this;
	}

	@Override
	public IQubit z() {
		return p(PI);
	}

	@Override
	public IQubit s() {
		return p(HALF_PI);
	}

	@Override
	public IQubit sdg() {
		return p(-HALF_PI);
	}

	@Override
	public IQubit t() {
		return p(QUARTER_PI);
	}

	@Override
	public IQubit tdg() {
		return p(-QUARTER_PI);
	}

	/* (non-Javadoc)
	 * This is one of the few single-qubit gates that must be
	 * implemented by the subclass.
	 */
	@Override
	public abstract IQubit p(double radians);

	@Override
	public IQubit sx() {
		u2(0.0, HALF_PI);
		rz(-HALF_PI);
		return this;
	}

	@Override
	public IQubit sxdg() {
		u2(0.0, -HALF_PI);
		rz(HALF_PI);
		return this;
	}

	@Override
	public IQubit rx(double radians) {
		u2(0.0, PI);
		rz(radians);
		u2(0.0, PI);
		return this;
	}

	@Override
	public IQubit ry(double radians) {
		u2(HALF_PI, HALF_PI);
		rz(radians);
		u2(HALF_PI, HALF_PI);
		return this;
	}

	/* (non-Javadoc)
	 * This is one of the few single-qubit gates that must be
	 * implemented by the subclass.
	 */
	@Override
	public abstract IQubit rz(double radians);

	@Override
	public IQubit r(Axis axis, double radians) {
		u(-axis.getTheta(), 0.0, -axis.getPhi());
		rz(radians);
		u( axis.getTheta(), axis.getPhi(), 0.0);
		return this;
	}

	@Override
	public IQubit u(double theta, double phi, double lambda) {
		if (lambda != 0) p(lambda);
		if (theta != 0)	ry(theta);
		if (phi != 0)	 p(phi);
		return this;
	}

	/**
	 * Applies the universal gate U2 to this qubit.
	 * <p>
	 * This is equivalent to P(phi) o RY(pi/2) o P(lambda)<br/>
	 * or in code: {@code p(lambda).ry(PI/2).p(phi)}.
	 * <p>
	 * This is one of the few single-qubit gates that must be
	 * implemented by the subclass.
	 * 
	 * @param phi - phi
	 * @param lambda - lambda
	 * @return this qubit
	 * @see <a href="https://quantum.cloud.ibm.com/docs/en/api/qiskit/qiskit.circuit.library.U2Gate">Qiskit U2 Gate</a>
	 */
	protected abstract IQubit u2(double phi, double lambda);

	//-----------------
	// Two-qubit gates
	//-----------------

	@Override
	public IQubit cx(IQubit ctrl) {
		h();
		cz(ctrl);
		h();
		return this;
	}

	@Override
	public IQubit cz(IQubit ctrl) {
		return cp(PI, ctrl);
	}

	/* (non-Javadoc)
	 * This is the only controlled gate that must be
	 * implemented by the subclass.
	 * With this, all controlled gates,
	 * as well as all controlled controlled gates,
	 * should work.
	 * 
	 * Only multi-controlled gates with more than two
	 * control qubits need the multi-controlled version of
	 * this method to be overridden.
	 */
	@Override
	public abstract IQubit cp(double radians, IQubit ctrl);

	/**
	 * Applies a clean controlled rotation to this qubit.
	 * <p>
	 * It is similar to {@link #cr(Axis, double, IQubit)},
	 * but without extra phase shift on the control qubit.
	 * 
	 * @param axis - the rotation axis
	 * @param radians - the controlled angle to rotate
	 * @param ctrl - the control qubit
	 * @return this qubit (the target)
	 * @throws ClassCastException when {@code ctrl} is not compatible
	 */
	protected IQubit c(Axis axis, double radians, IQubit ctrl) {
		u(-axis.getTheta(), 0.0, -axis.getPhi());
		cp(radians, ctrl);
		u( axis.getTheta(), axis.getPhi(), 0.0);
		return this;
	}

	@Override
	public IQubit c(Axis axis, IQubit ctrl) {
		c(axis, Math.PI, ctrl);
		return this;
	}

	@Override
	public IQubit cr(Axis axis, double radians, IQubit ctrl) {
		c(axis, radians, ctrl);
		ctrl.p(-radians * 0.5);
		return this;
	}

	@Override
	public IQubit cu(double theta, double phi, double lambda, IQubit ctrl) {
		if (lambda != 0) cp(lambda, ctrl);
		if (theta != 0)	 cr(Axis.Y, theta, ctrl);
		if (phi != 0)	 cp(phi, ctrl);
		return this;
	}

	//-------------------
	// Multi-qubit gates
	//-------------------

	/**
	 * Applies a multi-controlled phase shift.
	 * <p>
	 * It is equivalent to {@code cu(0.0, 0.0, radians, ctrl...)}.
	 * <p>
	 * This operation is symmetric, i.e.
	 * the target qubit can be switched with any control qubit
	 * and the result will be exactly the same.
	 * <p>
	 * This is the only multi-controlled gate method that needs to be
	 * overridden to get all multi-controlled gates to work.
	 * If this method is overridden, it might be a good idea to
	 * also override {@link IQubit#ccp(double, IQubit, IQubit)}
	 * for better performance.
	 * 
	 * @param radians - the controlled angle to rotate
	 * @param ctrl - the control qubits
	 * @return this qubit (the target)
	 * @throws ClassCastException when any control qubit is not compatible
	 * @throws UnsupportedOperationException when this gate is not supported
	 */
	protected IQubit cp(double radians, IQubit... ctrl) {
		switch (ctrl.length) {
			case 2:
				return ccp(radians, ctrl[0], ctrl[1]);
			case 1:
				return cp(radians, ctrl[0]);
			case 0:
				return p(radians);
			default:
				throw new UnsupportedOperationException();
		}
	}

	/**
	 * Applies a clean multi-controlled rotation to this qubit.
	 * <p>
	 * It is similar to {@link #cr(Axis, double, IQubit...)},
	 * but without extra phase shift on the control qubits.
	 * 
	 * @param axis - the rotation axis
	 * @param radians - the controlled angle to rotate
	 * @param ctrl - the control qubits
	 * @return this qubit (the target)
	 * @throws ClassCastException when any control qubit is not compatible
	 * @throws UnsupportedOperationException when this gate is not supported
	 */
	protected IQubit c(Axis axis, double radians, IQubit... ctrl) {
		u(-axis.getTheta(), 0.0, -axis.getPhi());
		cp(radians, ctrl);
		u( axis.getTheta(), axis.getPhi(), 0.0);
		return this;
	}

	@Override
	public IQubit c(Axis axis, IQubit... ctrl) {
		c(axis, PI, ctrl);
		return this;
	}

	@Override
	public IQubit cr(Axis axis, double radians, IQubit... ctrl) {
		if (ctrl.length == 0) {
			return r(axis, radians);
		}
		
		// first perform a clean multi-controlled rotation
		c(axis, radians, ctrl);
		
		// complete cr with cp(-radians/2) on the control qubits
		// using the last control qubit as target
		int last = ctrl.length - 1;
		ctrl[last].cu(0.0, 0.0, -radians * 0.5, Arrays.copyOf(ctrl, last));
		
		return this;
	}

	@Override
	public IQubit cu(double theta, double phi, double lambda, IQubit... ctrl) {
		if (lambda != 0) cp(lambda, ctrl);
		if (theta != 0)	 cr(Axis.Y, theta, ctrl);
		if (phi != 0)	 cp(phi, ctrl);
		return this;
	}

}
