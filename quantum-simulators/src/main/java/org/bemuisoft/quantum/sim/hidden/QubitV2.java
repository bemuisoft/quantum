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
package org.bemuisoft.quantum.sim.hidden;

import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.IQuantumState;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.QubitFactory;
import org.bemuisoft.quantum.core.QuantumState;
import org.bemuisoft.quantum.core.QubitState;
import org.bemuisoft.quantum.core.RandomBlochVector;

/**
 * This is a qubit simulation with hidden variable variation 2.
 * <p>
 * The qubit itself behaves exactly the same as the standard qubit,
 * as all functionality is inherited from {@link QubitState}.
 * The only difference is that the randomness of a measurement
 * is not generated during measurement (God does not play dice).
 * This makes measurements deterministic in principle:
 * if both the quantum state and the value of the hidden variable
 * are known, the measurement outcome can be predicted exactly.
 * <p>
 * What sets this variation apart from hidden variable
 * variation 1, is that the hidden value is a vector that rotates
 * along with the qubit.
 * So, this hidden variable should be considered as a property of the
 * qubit, not of the measurement device.
 * <p>
 * Having said that, would it be possible to influence this
 * hypothetical hidden property of the qubit?
 * To be honest, I don't know. I have no idea how.
 * Unless (or until) somebody can think of a way to do so,
 * this model cannot be distinguished from the standard qubit.
 * <p>
 * However, although this hidden vector is hypothetical, it can be
 * considered as a locally real variable, because it is not affected
 * by the quantum state.
 * This model also assumes measurement independence insofar as the
 * quantum state allows it.
 * Reality is not simply either local or non-local. A combination of
 * both is very well possible. This model demonstrates that!
 * 
 *  @author Benno Muilwijk
 */
public class QubitV2 extends QubitState {

	/** Hidden variable (a co-rotating vector) */
	private RandomBlochVector lambda = new RandomBlochVector();

	/**
	 * Returns a qubit factory for {@code QubitV2}.
	 * 
	 * @param n				- maximum number of qubits
	 * @param rightToLeft	- direction of qubit label assignment
	 * @return				a qubit factory for {@code QubitV2}
	 * @see					IQuantumState#isRightToLeft()
	 */
	public static QubitFactory<QubitV2> factory(int n, boolean rightToLeft) {
		QuantumState qs = new QuantumState(n, rightToLeft);
		return new QubitFactory<>(qs, QubitV2.class);
	}

	/**
	 * Constructs a qubit with a shared {@link QuantumState}.
	 * <p>
	 * The identifying short label will be derived
	 * from the last character of the long label.
	 * Therefore, the last character must in a consecutive
	 * range starting at either 0 or A or a.
	 * 
	 * @param qs	- the quantum state to share
	 * @param label	- a long label with identifying last character
	 * @see	IQuantumState#isRightToLeft()
	 */
	public QubitV2(QuantumState qs, String label) {
		super(qs, label);
	}

	@Override
	public QubitState reset() {
		super.reset();
		lambda.randomize();
		return this;
	}

	@Override
	public double getHiddenX() {
		return lambda.getX();
	}

	@Override
	public double getHiddenY() {
		return lambda.getY();
	}

	@Override
	public double getHiddenZ() {
		return lambda.getZ();
	}

	@Override
	public QubitState h() {
		lambda.h();
		return super.h();
	}

	@Override
	public QubitState x() {
		lambda.x();
		return super.x();
	}

	@Override
	public QubitState y() {
		lambda.y();
		return super.y();
	}

	@Override
	public QubitState z() {
		lambda.z();
		return super.z();
	}

	@Override
	public QubitState s() {
		lambda.s();
		return super.s();
	}

	@Override
	public QubitState sdg() {
		lambda.sdg();
		return super.sdg();
	}

	@Override
	public QubitState t() {
		lambda.t();
		return super.t();
	}

	@Override
	public QubitState tdg() {
		lambda.tdg();
		return super.tdg();
	}

	@Override
	public QubitState p(double radians) {
		lambda.p(radians);
		return super.p(radians);
	}

	@Override
	public QubitState rx(double radians) {
		lambda.rx(radians);
		return super.rx(radians);
	}

	@Override
	public QubitState ry(double radians) {
		lambda.ry(radians);
		return super.ry(radians);
	}

	@Override
	public QubitState rz(double radians) {
		lambda.rz(radians);
		return super.rz(radians);
	}

	@Override
	public QubitState r(Axis axis, double radians) {
		lambda.r(axis, radians);
		return super.r(axis, radians);
	}

	@Override
	public QubitState u(double theta, double phi, double lambda) {
		this.lambda.u(theta, phi, lambda);
		return super.u(theta, phi, lambda);
	}

	@Override
	public QubitState cx(IQubit ctrl) {
		cr(Axis.X, PI, (QubitV2) ctrl);
		return super.cx(ctrl);
	}

	@Override
	public QubitState cz(IQubit ctrl) {
		cr(Axis.Z, PI, (QubitV2) ctrl);
		return super.cz(ctrl);
	}

	@Override
	public QubitState cp(double radians, IQubit ctrl) {
		cr(Axis.Z, radians, (QubitV2) ctrl);
		return super.cp(radians, ctrl);
	}

	@Override
	public QubitState c(Axis axis, IQubit ctrl) {
		cr(axis, PI, (QubitV2) ctrl);
		return super.c(axis, ctrl);
	}

	@Override
	public QubitState cr(Axis axis, double radians, IQubit ctrl) {
		cr(axis, radians, (QubitV2) ctrl);
		return super.cr(axis, radians, ctrl);
	}

	@Override
	public QubitState cu(double theta, double phi, double lambda, IQubit ctrl) {
		if (lambda != 0.0) cp(lambda, ctrl);
		if (theta != 0.0) {
			cr(Axis.Y, theta, (QubitV2) ctrl);
			super.cu(theta, 0.0, 0.0, ctrl);
		}
		if (phi != 0.0) cp(phi, ctrl);
		return this;
	}

	/**
	 * Applies a controlled rotation to lambda in a classical way.
	 * 
	 * @param axis - the rotation axis
	 * @param radians - the controlled angle to rotate
	 * @param ctrl - the control qubit
	 */
	private void cr(Axis axis, double radians, QubitV2 ctrl) {
		if (this.isMinus(axis)) {
			ctrl.lambda.p(radians);
		}
		if (ctrl.isMinus()) {
			this.lambda.r(axis, radians);
		}
	}

	/**
	 * Returns the result of a "weak" measurement along the specified axis.
	 * This measurement does not change the quantum state in any way.
	 * <p>
	 * Note that {@code isMinus()} is equivalent to {@code isMinus(Axis.Z)}.
	 * 
	 * @param	axis - the measurement axis
	 * @return	{@code true} if this qubit would be measured as |1⟩, {@code false} otherwise
	 * @see QubitState#isMinus
	 */
	private boolean isMinus(Axis axis) {
		return (this.dotProduct(axis) + lambda.dotProduct(axis)) < 0.0;
	}

}
