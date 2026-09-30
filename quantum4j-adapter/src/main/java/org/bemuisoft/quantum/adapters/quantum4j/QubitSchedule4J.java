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

import com.quantum4j.core.circuit.QuantumCircuit;

import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.Base;
import org.bemuisoft.quantum.api.IQubit;

/**
 * A qubit on a circuit wire.
 * <p>
 * Gates applied to this kind of qubit are added to
 * the qubit's circuit wire and
 * can be scheduled for execution later on.
 * <p>
 * The circuit can also be drawn or
 * translated to QASM code.
 * 
 * @author Benno Muilwijk
 */
public class QubitSchedule4J implements IQubit, Base {

	private QuantumCircuit qc;
	private String label;
	private int qIndex;

	/**
	 * Constructs a qubit with a shared {@link QuantumCircuit}.
	 * <p>
	 * The identifying label must be in a consecutive
	 * range starting at 'A'.
	 * 
	 * @param qc	- the quantum4j circuit to share
	 * @param label	- a capital as short identifying label
	 */
	public QubitSchedule4J(QuantumCircuit qc, char label) {
		this(qc, label, String.valueOf(label));
	}

	/**
	 * Constructs a qubit with a shared {@link QuantumCircuit}.
	 * <p>
	 * The identifying short label will be derived
	 * from the last character of the long label.
	 * Therefore, the last character must be in a consecutive
	 * range starting at 'A'.
	 * 
	 * @param qc		- the quantum4j circuit to share
	 * @param longLabel	- a label with identifying last character
	 */
	public QubitSchedule4J(QuantumCircuit qc, String longLabel) {
		this(qc, longLabel.charAt(longLabel.length() - 1), longLabel);
	}

	/**
	 * Constructs a qubit with a shared {@link QuantumCircuit}.
	 * <p>
	 * The identifying label must be in a consecutive
	 * range starting at 'A'.
	 * 
	 * @param qc		- the quantum4j circuit to share
	 * @param label		- a capital as short identifying label
	 * @param longLabel	- a long label
	 */
	public QubitSchedule4J(QuantumCircuit qc, char label, String longLabel) {
		this.qc = qc;
		this.label = longLabel;
		this.qIndex = label - 'A'; 
	}

	/**
	 * Returns this qubit's long label.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Returns this qubit's index for the
	 * quantum4j circuit.
	 * 
	 * @return	the qubit index
	 */
	public int getIndex() {
		return qIndex;
	}

	@Override
	public int measure() {
		qc.measure(qIndex, qIndex);
		return 0;
	}

	@Override
	public QubitSchedule4J reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public QubitSchedule4J h() {
		qc.h(qIndex);
		return this;
	}

	@Override
	public QubitSchedule4J x() {
		qc.x(qIndex);
		return this;
	}

	@Override
	public QubitSchedule4J y() {
		qc.y(qIndex);
		return this;
	}

	@Override
	public QubitSchedule4J z() {
		qc.z(qIndex);
		return this;
	}

	@Override
	public QubitSchedule4J s() {
		qc.s(qIndex);
		return this;
	}

	@Override
	public QubitSchedule4J sdg() {
		qc.u1(qIndex, -HALF_PI);
		return this;
	}

	@Override
	public QubitSchedule4J t() {
		qc.t(qIndex);
		return this;
	}

	@Override
	public QubitSchedule4J tdg() {
		qc.u1(qIndex, -QUARTER_PI);
		return this;
	}

	@Override
	public QubitSchedule4J p(double radians) {
		qc.u1(qIndex, radians);
		return this;
	}

	@Override
	public QubitSchedule4J sx() {
		qc.u2(qIndex, 0.0, HALF_PI);
		qc.rz(qIndex, -HALF_PI);
		return this;
	}

	@Override
	public QubitSchedule4J sxdg() {
		qc.u2(qIndex, 0.0, -HALF_PI);
		qc.rz(qIndex, HALF_PI);
		return this;
	}

	@Override
	public QubitSchedule4J rx(double radians) {
		qc.rx(qIndex, radians);
		return this;
	}

	@Override
	public QubitSchedule4J ry(double radians) {
		qc.ry(qIndex, radians);
		return this;
	}

	@Override
	public QubitSchedule4J rz(double radians) {
		qc.rz(qIndex, radians);
		return this;
	}

	@Override
	public QubitSchedule4J r(Axis axis, double radians) {
		double theta = axis.getTheta();
		double phase = axis.getPhi();
		if (theta != 0.0 || phase != 0.0)
			qc.u3(qIndex, -theta, 0.0, -phase);
		qc.rz(qIndex, radians);
		if (theta != 0.0 || phase != 0.0)
			qc.u3(qIndex, theta, phase, 0.0);
		return this;
	}

	@Override
	public QubitSchedule4J u(double theta, double phi, double lambda) {
		qc.u3(qIndex, theta, phi, lambda);
		return this;
	}

	@Override
	public QubitSchedule4J cx(IQubit ctrl) {
		qc.cx(index(ctrl), qIndex);
		return this;
	}

	@Override
	public QubitSchedule4J cz(IQubit ctrl) {
		qc.cz(index(ctrl), qIndex);
		return this;
	}

	@Override
	public QubitSchedule4J c(Axis axis, IQubit ctrl) {
		double theta = axis.getTheta();
		double phase = axis.getPhi();
		if (theta != 0.0 || phase != 0.0)
			qc.u3(qIndex, -theta, 0.0, -phase);
		qc.cz(index(ctrl), qIndex);
		if (theta != 0.0 || phase != 0.0)
			qc.u3(qIndex, theta, phase, 0.0);
		return this;
	}

	@Override
	public QubitSchedule4J cp(double radians, IQubit ctrl) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
//		return this;
	}

	@Override
	public QubitSchedule4J cr(Axis axis, double radians, IQubit ctrl) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
//		return this;
	}

	@Override
	public QubitSchedule4J cu(double theta, double phi, double lambda, IQubit ctrl) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
//		return this;
	}

	private int index(IQubit q) {
		return ((QubitSchedule4J) q).qIndex;
	}

}
