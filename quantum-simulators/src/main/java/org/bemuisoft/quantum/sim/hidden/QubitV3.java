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
import org.bemuisoft.quantum.api.Base;
import org.bemuisoft.quantum.api.IQuantumState;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitAnalyzer;
import org.bemuisoft.quantum.api.QubitFactory;
import org.bemuisoft.quantum.core.QuantumState;
import org.bemuisoft.quantum.core.QubitState;
import org.bemuisoft.quantum.core.RandomBlochVector;

/**
 * This is a qubit simulation with hidden variable variation 3.
 * <p>
 * It works essentially the same as hidden variable variation 2,
 * i.e. with a local vector that rotates along with the qubit,
 * but instead of subclassing {@link QubitState},
 * a global qubit state is wrapped, and treated more or less
 * equal to the local state.
 * 
 * @author Benno Muilwijk
 */
public class QubitV3 implements IQubitAnalyzer, Base {

	private RandomBlochVector local;		// local (classical) state
	private QubitState global;				// global (quantum) state

	/**
	 * Returns a qubit factory for {@code QubitV3}.
	 * 
	 * @param n				- maximum number of qubits
	 * @param rightToLeft	- direction of qubit label assignment
	 * @return				a qubit factory for {@code QubitV3}
	 * @see					IQuantumState#isRightToLeft()
	 */
	public static QubitFactory<QubitV3> factory(int n, boolean rightToLeft) {
		QuantumState qs = new QuantumState(n, rightToLeft);
		return new QubitFactory<>(qs, QubitV3.class);
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
	public QubitV3(QuantumState qs, String label) {
		this.global = new QubitState(qs, label);
		this.local = new RandomBlochVector();
	}

	@Override
	public int components() {
		return global.components();
	}

	@Override
	public String getLabel() {
		return global.getLabel();
	}

	@Override
	public IQuantumState getSystemState() {
		return global.getSystemState();
	}

	@Override
	public double getX() {
		return global.getX();
	}

	@Override
	public double getY() {
		return global.getY();
	}

	@Override
	public double getZ() {
		return global.getZ();
	}

	@Override
	public double getX(int i) {
		return global.getX(i);
	}

	@Override
	public double getY(int i) {
		return global.getY(i);
	}

	@Override
	public double getZ(int i) {
		return global.getZ(i);
	}

	@Override
	public double getMagnitude() {
		return global.getMagnitude();
	}

	@Override
	public double getPhase() {
		return global.getPhase();
	}

	@Override
	public double getTheta() {
		return global.getTheta();
	}

	@Override
	public double getProbability0() {
		return global.getProbability0();
	}

	@Override
	public double getProbability1() {
		return global.getProbability1();
	}

	@Override
	public double getPurity() {
		return global.getPurity();
	}

	@Override
	public boolean isMixed() {
		return global.isMixed();
	}

	@Override
	public boolean isPure() {
		return global.isPure();
	}

	@Override
	public double getHiddenX() {
		return local.getX();
	}

	@Override
	public double getHiddenY() {
		return local.getY();
	}

	@Override
	public double getHiddenZ() {
		return local.getZ();
	}

	@Override
	public int measure() {
		int result = isMinus(Axis.Z) ? 1 : 0;
//		log(label + " result = " + result);
		global.getSystemState().setMeasured(result, global);
		return result;
	}

	@Override
	public QubitV3 reset() {
		if (measure() != 0) {
			x();
		}
		local.randomize();
		return this;
	}

	@Override
	public QubitV3 h() {
		local.h();
		global.h();
		return this;
	}

	@Override
	public QubitV3 x() {
		local.x();
		global.x();
		return this;
	}

	@Override
	public QubitV3 y() {
		local.y();
		global.y();
		return this;
	}

	@Override
	public QubitV3 z() {
		local.z();
		global.z();
		return this;
	}

	@Override
	public QubitV3 s() {
		local.s();
		global.s();
		return this;
	}

	@Override
	public QubitV3 sdg() {
		local.sdg();
		global.sdg();
		return this;
	}

	@Override
	public QubitV3 t() {
		local.t();
		global.t();
		return this;
	}

	@Override
	public QubitV3 tdg() {
		local.tdg();
		global.tdg();
		return this;
	}

	@Override
	public QubitV3 p(double radians) {
		local.p(radians);
		global.p(radians);
		return this;
	}

	@Override
	public QubitV3 sx() {
		local.sx();
		global.sx();
		return this;
	}

	@Override
	public QubitV3 sxdg() {
		local.sxdg();
		global.sxdg();
		return this;
	}

	@Override
	public QubitV3 rx(double radians) {
		local.rx(radians);
		global.rx(radians);
		return this;
	}

	@Override
	public QubitV3 ry(double radians) {
		local.ry(radians);
		global.ry(radians);
		return this;
	}

	@Override
	public QubitV3 rz(double radians) {
		local.rz(radians);
		global.rz(radians);
		return this;
	}

	@Override
	public QubitV3 r(Axis axis, double radians) {
		local.r(axis, radians);
		global.r(axis, radians);
		return this;
	}

	@Override
	public QubitV3 u(double theta, double phi, double lambda) {
		local.u(theta, phi, lambda);
		global.u(theta, phi, lambda);
		return this;
	}

	@Override
	public QubitV3 cx(IQubit ctrl) {
		this.cr(Axis.X, PI, cast(ctrl));
		global.cx(cast(ctrl).global);
		return this;
	}

	@Override
	public QubitV3 cz(IQubit ctrl) {
		this.cr(Axis.Z, PI, cast(ctrl));
		global.cz(cast(ctrl).global);
		return this;
	}

	@Override
	public QubitV3 cp(double radians, IQubit ctrl) {
		this.cr(Axis.Z, radians, cast(ctrl));
		global.cp(radians, cast(ctrl).global);
		return this;
	}

	@Override
	public QubitV3 c(Axis axis, IQubit ctrl) {
		this.cr(axis, PI, cast(ctrl));
		global.c(axis, cast(ctrl).global);
		return this;
	}

	@Override
	public QubitV3 cr(Axis axis, double radians, IQubit ctrl) {
		this.cr(axis, radians, cast(ctrl));
		global.cr(axis, radians, cast(ctrl).global);
		return this;
	}

	@Override
	public QubitV3 cu(double theta, double phi, double lambda, IQubit ctrl) {
		if (lambda != 0.0) cp(lambda, ctrl);
		if (theta != 0.0) {
			this.cr(Axis.Y, theta, cast(ctrl));
			global.cu(theta, 0.0, 0.0, cast(ctrl).global);
		}
		if (phi != 0.0) cp(phi, ctrl);
		return this;
	}

	/**
	 * Applies a controlled rotation to the <b>local</b> state in a classical way.
	 * 
	 * @param axis - the rotation axis
	 * @param radians - the controlled angle to rotate
	 * @param ctrl - the control qubit
	 */
	private void cr(Axis axis, double radians, QubitV3 ctrl) {
		if (this.isMinus(axis)) {
			ctrl.local.p(radians);
		}
		if (ctrl.isMinus(Axis.Z)) {
			this.local.r(axis, radians);
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
		return (global.dotProduct(axis) + local.dotProduct(axis)) < 0.0;
	}

	private QubitV3 cast(IQubit q) {
		return (QubitV3) q;
	}

}
