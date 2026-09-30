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

import java.util.Collection;

import org.bemuisoft.math.complex.ColumnVector;
import org.bemuisoft.math.complex.ComplexNumber;
import org.bemuisoft.math.complex.Matrix;
import org.bemuisoft.quantum.api.IQuantumState;

/**
 * A pure quantum system is a special kind of quantum system.
 * Its state can be represented by a state vector,
 * usually referred to as |Ψ⟩ or |psi⟩.
 * <p>
 * Its state can also be represented by a density matrix,
 * like any other quantum system, but
 * only in the case of a pure quantum system,
 * the density matrix can be reduced to a state vector,
 * and be derived from the state vector.
 * 
 * @author Benno Muilwijk
 * @see IQuantumState
 * @see QuantumSystem
 * @see #densityMatrix()
 * @see #setDensityMatrix(Matrix)
 */
public class PureQuantumSystem extends QuantumSystem implements IQuantumState {

	private ColumnVector psi;		// state vector |psi⟩

	/**
	 * Constructs a pure quantum system for
	 * the specified number of qubits.
	 * <p>
	 * The qubits are labeled A, B, ...
	 * from left to right.
	 * <p>
	 * The quantum state is initialized to |0⟩.
	 * 
	 * @param qubits	the number of qubits
	 */
	public PureQuantumSystem(int qubits) {
		this(qubits, false);
	}

	/**
	 * Constructs a pure quantum state for
	 * the specified number of qubits.
	 * <p>
	 * The qubits are labeled A, B, ...
	 * from left to right or from right to left,
	 * as specified.
	 * <p>
	 * The quantum state is initialized to |0⟩.
	 * 
	 * @param qubits		the number of qubits
	 * @param rightToLeft	{@code true} for right-to-left labels,
	 * 						{@code false} for left-to-right labels
	 */
	public PureQuantumSystem(int qubits, boolean rightToLeft) {
		super(qubits, rightToLeft, null);
		setPsi(initialState());
	}

	private ColumnVector initialState() {
		// return a state vector that represents quantum state |0⟩
		// that is, with all qubits set to |0⟩
		ColumnVector initialState = (ColumnVector) Matrix.zero(1 << qubits(), 1);		// 1 << n == 1 * Math.pow(2, n) == 2^n
		initialState.set(0, ONE);
		return initialState;
	}

	private void setPsi(ColumnVector state) {
		// ensure that this private method is only invoked by synchronized methods
		// to guarantee that psi does not change while rho is calculated
		checkState(isValidState(state), "Internal logic error.");
		psi = state;
		super.setRho(null);		// reset density matrix and update modCount
	}

	private boolean isValidState(ColumnVector state) {
		// check dimensions
		if (state.rows() != size()) {
			return false;
		}
		// check sum of probabilities (Born rule)
		double sum = 0.0;
		for (int i = 0; i < state.rows(); i++) {
			sum += state.get(i).abs2();
		}
//		if (round(sum, 12) != 1.0) log("Invalid state, sum is: " + round(sum, 12));
		return (round(sum, 12) == 1.0);
	}

	/**
	 * Sets this quantum system to the specified state.
	 * 
	 * @param state	the state vector to set
	 * @throws		IllegalArgumentException if the state vector is not valid
	 */
	public synchronized void setState(ColumnVector state) {
		// check input state
		check(isValidState(state), "Invalid state vector.");
		setPsi(state.setFinal());
	}

	@Override
	public synchronized void setDensityMatrix(Matrix rho) {
		check(isPure(rho), "Density matrix does not represent a pure state.");
		super.setDensityMatrix(rho);	// performs more checks
		
		// find column with highest probability
		double p = 0.0;
		int col  = -1;
		int size = size();
		for (int i = 0; i < size; i++) {
			double pc = rho.get(i, i).realPart();
			if (p < pc) {
				p = pc;
				col = i;
				if (p >= 0.5) {
					break;
				}
			}
		}
		
		// derive state vector from density matrix column
		// global phase depends on the column
		ComplexNumber magnitude = c(Math.sqrt(p));
		ColumnVector psi = ColumnVector.create(size);
		for (int i = 0; i < size; i++) {
			psi.init(i, rho.get(i, col).divide(magnitude));
		}
		setPsi(psi);
		setRho(rho);
	}

	@Override
	public synchronized PureQuantumSystem reset() {
		// synchronized because this method invokes setPsi
		if (modCount != modCount0) {
			setPsi(initialState());
			modCount0 = modCount;
		}
		return this;
	}

	@Override
	public boolean isPure() {
		// must override as rho can be null
		return true;
	}

	/**
	 * Returns the state vector of the current state.
	 * 
	 * @return	the current state vector
	 */
	public ColumnVector state() {
		return psi.setFinal();
	}

	/**
	 * Returns the complex amplitude in the specified state dimension.
	 * 
	 * @param index	the state dimension index
	 * @return	the complex amplitude
	 */
	public ComplexNumber amplitude(int index) {
		return psi.get(index);
	}

	@Override
	public double getMagnitude(int index) {
		return amplitude(index).absValue();
	}

	@Override
	public double getPhase(int index) {
		return amplitude(index).phase();
	}

	@Override
	public double getProbability(int index) {
		return amplitude(index).abs2();
	}

	@Override
	public boolean hasStrictPhases() {
		return true;
	}

	@Override
	public Matrix densityMatrix() {
		Matrix rho = super.getRho();
		if (rho == null) synchronized(this) {
			// synchronized block so that psi is not changed while rho is calculated
			rho = Matrix.product(psi, psi.transpose(true));		// rho = |psi⟩⟨psi|
			super.setRho(rho.setFinal());
		}
		return rho;
	}

	private synchronized void applyInternal(Matrix operation) {
		// synchronized because this method invokes setPsi
		setPsi((ColumnVector) Matrix.product(operation, psi));
	}

	@Override
	public PureQuantumSystem apply(Matrix operation) {
		check(isValidOperation(operation), "Invalid operation matrix.");
		applyInternal(operation);
		return this;
	}

	@Override
	public PureQuantumSystem apply(Gate gate, int qIndex) {
		applyInternal(operation(gate, qIndex));
		return this;
	}

	@Override
	public PureQuantumSystem apply(Gate gate, int cIndex, int qIndex) {
		applyInternal(operation(gate, cIndex, qIndex));
		return this;
	}

	@Override
	public PureQuantumSystem apply(Gate gate, Collection<Integer> cIndices, int qIndex) {
		applyInternal(operation(gate, cIndices, qIndex));
		return this;
	}

	/**
	 * Updates the state of this quantum system with
	 * the information of the outcome of a measurement
	 * on the specified qubit.
	 * 
	 * @param outcome	0 for |0⟩, 1 (or any other value) for |1⟩
	 * @param qIndex	the measured qubit index
	 * @see				#setMeasured(boolean, int)
	 */
	public void setMeasured(int outcome, int qIndex) {
		setMeasured(outcome != 0, qIndex);
	}

	/**
	 * Updates the state of this quantum system with
	 * the information of the outcome of a measurement
	 * on the specified qubit.
	 * 
	 * @param outcome	{@code false} for |0⟩, {@code true} for |1⟩
	 * @param qIndex	the measured qubit index
	 * @see				#setMeasured(int, int)
	 */
	public synchronized void setMeasured(boolean outcome, int qIndex) {
		checkQubitIndex(qIndex);
		int qMask = size() >> (qIndex + 1);
		
		// calculate probability of the outcome
		double p = 0.0;
		for (int i = 0; i < size(); i++) {
			boolean iOutcome = ((i & qMask) == qMask);
			if (iOutcome == outcome) {
				p += getProbability(i);
			}
		}
		check(p > 0.0, "Impossible outcome.");
		if (p == 1.0) {
			// no update needed
			return;
		}
		
		// update the state vector
		ColumnVector newState = (psi.isFinal()) ? ColumnVector.create(size()) : psi;
		ComplexNumber q = c(1.0 / Math.sqrt(p));
		for (int i = 0; i < size(); i++) {
			boolean iOutcome = ((i & qMask) == qMask);
			if (iOutcome == outcome) {
				newState.set(i, psi.get(i).multiply(q));
			} else {
				newState.set(i, ZERO);
			}
		}
		setPsi(newState);
	}

}
