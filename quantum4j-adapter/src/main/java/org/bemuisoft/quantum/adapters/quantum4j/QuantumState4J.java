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

import com.quantum4j.core.gates.SingleQubitGate;
import com.quantum4j.core.gates.ThreeQubitGate;
import com.quantum4j.core.gates.TwoQubitGate;
import com.quantum4j.core.math.Complex;
import com.quantum4j.core.math.StateVector;

import org.bemuisoft.quantum.api.AbstractQubitAnalyzer;
import org.bemuisoft.quantum.api.IQuantumState;

/**
 * Adapts {@link StateVector} to implement {@link IQuantumState}.
 * 
 * @author Benno Muilwijk
 */
public class QuantumState4J implements IQuantumState {

	private StateVector state;
	private int modCount;

	/**
	 * Constructs a quantum4j state vector
	 * for the specified number of qubits.
	 * 
	 * @param qubits	- number of qubits
	 */
	public QuantumState4J(int qubits) {
		state = new StateVector(qubits);
	}

	@Override
	public int qubits() {
		return state.getNumQubits();
	}

	@Override
	public int size() {
		return state.dimension();
	}

	@Override
	public double getMagnitude(int i) {
		return Math.sqrt(getProbability(i));
	}

	@Override
	public double getPhase(int i) {
		Complex amp = state.getAmplitudes()[i];
		if (Math.hypot(amp.getRe(), amp.getIm()) < 1e-12) {
			return 0.0;
		}
		return Math.atan2(amp.getIm(), amp.getRe());
	}

	@Override
	public double getProbability(int i) {
		return state.getAmplitudes()[i].absSquared();
	}

	@Override
	public boolean hasStrictPhases() {
		return true;
	}

	@Override
	public boolean isRightToLeft() {
		return true;
	}

	/**
	 * Applies a single-qubit gate to the specified qubit.
	 * 
	 * @param gate		- the gate to apply
	 * @param qIndex	- the qubit index
	 * @return this quantum4j state vector
	 */
	public synchronized QuantumState4J apply(SingleQubitGate gate, int qIndex) {
		gate.apply(state, qIndex);
		modCount++;
		return this;
	}

	/**
	 * Applies a two-qubit gate to the specified qubit.
	 * 
	 * @param gate		- the gate to apply
	 * @param cIndex	- the control qubit index
	 * @param qIndex	- the target qubit index
	 * @return this quantum4j state vector
	 */
	public synchronized QuantumState4J apply(TwoQubitGate gate, int cIndex, int qIndex) {
		gate.apply(state, cIndex, qIndex);
		modCount++;
		return this;
	}

	/**
	 * Applies a three-qubit gate to the specified qubit.
	 * 
	 * @param gate		- the gate to apply
	 * @param cIndex1	- index of control qubit 1
	 * @param cIndex2	- index of control qubit 2
	 * @param qIndex	- the target qubit index
	 * @return this quantum4j state vector
	 */
	public synchronized QuantumState4J apply(ThreeQubitGate gate, int cIndex1, int cIndex2, int qIndex) {
		gate.apply(state, cIndex1, cIndex2, qIndex);
		modCount++;
		return this;
	}

	/**
	 * Measures the specified qubit and returns the outcome
	 * as either 0 for |0⟩ or 1 for |1⟩.
	 * <p>
	 * The quantum state is collapsed accordingly.
	 * 
	 * @param qIndex	- the qubit index
	 * @return the measurement outcome, 0 or 1
	 */
	public synchronized int measure(int qIndex) {
		int outcome = state.measureOne(qIndex);
		modCount++;
		return outcome;
	}

	/**
	 * Returns the current system state identifier.
	 * <p>
	 * If the returned value is the same as
	 * from a previous invocation, that means
	 * the quantum state of the system has not
	 * changed in between those two invocations.
	 * Otherwise, it has (possibly) changed.
	 * 
	 * @return	the current state identifier
	 * @see AbstractQubitAnalyzer#stateIdentifier
	 */
	public int stateIdentifier() {
		return modCount;
	}

}
