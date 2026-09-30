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
package org.bemuisoft.quantum.adapters.quantum4j;

import org.bemuisoft.quantum.api.AbstractQubitAnalyzer;
import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.IQuantumState;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitAnalyzer;
import org.bemuisoft.quantum.api.QubitFactory;

/**
 * A full implementation of {@link IQubit}
 * using {@link QuantumState4J} as a backbone.
 * <p>
 * {@link IQubitAnalyzer} functionality is
 * inherited from {@link AbstractQubitAnalyzer}.
 * 
 * @author Benno Muilwijk
 */
public class Qubit4J extends AbstractQubitAnalyzer {

	private QuantumState4J qs;
	private int qIndex;

	/**
	 * Returns a qubit factory for {@code Qubit4J}.
	 * 
	 * @param n	- maximum number of qubits
	 * @return	a qubit factory for {@code Qubit4J}
	 */
	public static QubitFactory<Qubit4J> factory(int n) {
		QuantumState4J qs = new QuantumState4J(n);
		return new QubitFactory<>(qs, Qubit4J.class);
	}

	/**
	 * Constructs a qubit with a shared {@link QuantumState4J}.
	 * <p>
	 * The identifying short label will be derived
	 * from the last character of the long label.
	 * Therefore, the last character must in a consecutive
	 * range starting at either 0 or A or a.
	 * 
	 * @param qs	- the quantum4j state vector to share
	 * @param label	- a label with identifying last character
	 * @see			IQuantumState#isRightToLeft()
	 */
	public Qubit4J(QuantumState4J qs, String label) {
		super(qs, label);
		this.qs = qs;
		this.qIndex = qs.shortLabel(label) - 'A';
	}

	/**
	 * Returns this qubit's index for the
	 * quantum4j state vector.
	 * 
	 * @return	the qubit index
	 */
	public int getIndex() {
		return qIndex;
	}

	@Override
	protected int stateIdentifier() {
		return qs.stateIdentifier();
	}

	//----------------
	// IQubit methods
	//----------------

	@Override
	public int measure() {
		return qs.measure(qIndex);
	}

	@Override
	public Qubit4J reset() {
		if (measure() != 0) {
			x();
		}
		return this;
	}

	//--------------------
	// Single-qubit gates
	//--------------------

	@Override
	public Qubit4J h() {
		qs.apply(Gates4J.h(), qIndex);
		return this;
	}

	@Override
	public Qubit4J x() {
		qs.apply(Gates4J.x(), qIndex);
		return this;
	}

	@Override
	public Qubit4J y() {
		qs.apply(Gates4J.y(), qIndex);
		return this;
	}

	@Override
	public Qubit4J z() {
		qs.apply(Gates4J.z(), qIndex);
		return this;
	}

	@Override
	public Qubit4J s() {
		qs.apply(Gates4J.s(), qIndex);
		return this;
	}

	@Override
	public Qubit4J sdg() {
		qs.apply(Gates4J.u1(-HALF_PI), qIndex);
		return this;
	}

	@Override
	public Qubit4J t() {
		qs.apply(Gates4J.t(), qIndex);
		return this;
	}

	@Override
	public Qubit4J tdg() {
		qs.apply(Gates4J.u1(-QUARTER_PI), qIndex);
		return this;
	}

	@Override
	public Qubit4J p(double radians) {
		qs.apply(Gates4J.u1(radians), qIndex);
		return this;
	}

	@Override
	public Qubit4J sx() {
		qs.apply(Gates4J.u2(0.0, HALF_PI), qIndex);
		qs.apply(Gates4J.rz(-HALF_PI), qIndex);
		return this;
	}

	@Override
	public Qubit4J sxdg() {
		qs.apply(Gates4J.u2(0.0, -HALF_PI), qIndex);
		qs.apply(Gates4J.rz(HALF_PI), qIndex);
		return this;
	}

	@Override
	public Qubit4J rx(double radians) {
		qs.apply(Gates4J.rx(radians), qIndex);
		return this;
	}

	@Override
	public Qubit4J ry(double radians) {
		qs.apply(Gates4J.ry(radians), qIndex);
		return this;
	}

	@Override
	public Qubit4J rz(double radians) {
		qs.apply(Gates4J.rz(radians), qIndex);
		return this;
	}

	@Override
	public Qubit4J r(Axis axis, double radians) {
		double theta = axis.getTheta();
		double phase = axis.getPhi();
		if (theta != 0.0 || phase != 0.0)
			qs.apply(Gates4J.u3(-theta, 0.0, -phase), qIndex);
		qs.apply(Gates4J.rz(radians), qIndex);
		if (theta != 0.0 || phase != 0.0)
			qs.apply(Gates4J.u3(theta, phase, 0.0), qIndex);
		return this;
	}

	@Override
	public Qubit4J u(double theta, double phi, double lambda) {
		qs.apply(Gates4J.u3(theta, phi, lambda), qIndex);
		return this;
	}

	//-----------------
	// Two-qubit gates
	//-----------------

	@Override
	public Qubit4J cx(IQubit ctrl) {
		qs.apply(Gates4J.cnot(), index(ctrl), qIndex);
		return this;
	}

	@Override
	public Qubit4J cz(IQubit ctrl) {
		qs.apply(Gates4J.cz(), index(ctrl), qIndex);
		return this;
	}

	@Override
	public Qubit4J c(Axis axis, IQubit ctrl) {
		double theta = axis.getTheta();
		double phase = axis.getPhi();
		if (theta != 0.0 || phase != 0.0)
			qs.apply(Gates4J.u3(-theta, 0.0, -phase), qIndex);
		qs.apply(Gates4J.cz(), index(ctrl), qIndex);
		if (theta != 0.0 || phase != 0.0)
			qs.apply(Gates4J.u3(theta, phase, 0.0), qIndex);
		return this;
	}

	@Override
	public Qubit4J cp(double radians, IQubit ctrl) {
		qs.apply(Gates4J.cp(radians), index(ctrl), qIndex);
		return this;
	}

	@Override
	public Qubit4J cr(Axis axis, double radians, IQubit ctrl) {
		double theta = axis.getTheta();
		double phase = axis.getPhi();
		if (theta != 0.0 || phase != 0.0)
			qs.apply(Gates4J.u3(-theta, 0.0, -phase), qIndex);
		// CRZ(lambda, a, b) == P(-lambda/2, a)*CP(lambda, a, b)
		qs.apply(Gates4J.cp(radians), index(ctrl), qIndex);
		qs.apply(Gates4J.u1(-radians*0.5), index(ctrl));
		if (theta != 0.0 || phase != 0.0)
			qs.apply(Gates4J.u3(theta, phase, 0.0), qIndex);
		return this;
	}

	@Override
	public Qubit4J cu(double theta, double phi, double lambda, IQubit ctrl) {
		qs.apply(Gates4J.cu(theta, phi, lambda), index(ctrl), qIndex);
		return this;
	}

	@Override
	public IQubit swap(IQubit ctrl) {
		qs.apply(Gates4J.swap(), index(ctrl), qIndex);
		return this;
	}

	private int index(IQubit q) {
		return ((Qubit4J) q).qIndex;
	}

	//-------------------
	// Three-qubit gates
	//-------------------

	@Override
	public Qubit4J ccx(IQubit ctrl1, IQubit ctrl2) {
		qs.apply(Gates4J.ccx(), index(ctrl1), index(ctrl2), qIndex);
		return this;
	}

	@Override
	public Qubit4J ccz(IQubit ctrl1, IQubit ctrl2) {
		h();
		ccx(ctrl1, ctrl2);
		h();
		return this;
	}

	//-------------------
	// Other methods
	//-------------------

	@Override
	public String toString() {
		return getClass().getSimpleName() + ' ' + getLabel() +
				" {x=" + roundSmart(getX()) +
				", y=" + roundSmart(getY()) +
				", z=" + roundSmart(getZ()) +
				", ϕ=" + toPi(Math.atan2(getY(), getX())) +
				"}";
	}

}
